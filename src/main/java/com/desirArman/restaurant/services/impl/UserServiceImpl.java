package com.desirArman.restaurant.services.impl;

import com.desirArman.restaurant.domain.UserCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.User;
import com.desirArman.restaurant.exceptions.UserRegistrationException;
import com.desirArman.restaurant.services.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;

    @Value("${keycloak.registration-client.realm}")
    private String realm;

    private static final String DEFAULT_ROLE = "USER";


    @Override
    public User createUser(UserCreateUpdateRequest request) {
        log.info("Creating Keycloak user - Username: '{}'", request.getUsername());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEnabled(true);

        // 2. Prepare the password credential securely
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        userRepresentation.setCredentials(Collections.singletonList(credential));

        // 3. Make the API call to create the user in Keycloak
        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(userRepresentation);

        int status = response.getStatus();
        String responseBody = null;

// Read entity before any logic that could close the response
        if (status != 201) {
            try {
                responseBody = response.readEntity(String.class);
            } catch (Exception e) {
                responseBody = "Could not read error body: " + e.getMessage();
            }
        }

// Now handle errors
        if (status != 201) {
            log.error("Keycloak user creation failed. Status: {}, Body: {}", status, responseBody);

            if (status == 409) {
                throw new UserRegistrationException("User already exists.");
            }
            throw new UserRegistrationException("Failed to register user. Status: " + status + " Response: " + responseBody);
        }

        log.info("User {} created successfully in Keycloak", request.getUsername());
        String userId = getCreatedUserId(response);

        // 5. Assign the default 'USER' role
        assignDefaultRole(usersResource, userId);


        // 6. Return a User object representing the new user
        return User.builder()
                .id(userId)
                .username(request.getUsername())
                .email(request.getEmail())
                .givenName(request.getFirstName())
                .familyName(request.getLastName())
                .roles(Collections.singleton(DEFAULT_ROLE))
                .build();
    }



    private String getCreatedUserId(Response response) {
        String locationHeader = response.getHeaderString("Location");
        if (locationHeader == null || locationHeader.isEmpty()) {
            throw new UserRegistrationException("Could not retrieve created user ID from Keycloak response.");
        }
        return locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
    }

    private void assignDefaultRole(UsersResource usersResource, String userId) {
        try {
            log.debug("Assigning default role to user: {}", userId);

            // Optional: short wait to ensure user is fully created
            Thread.sleep(500);

            RoleRepresentation userRole = keycloak.realm(realm)
                    .roles()
                    .get("USER") // Use "USER" directly for testing
                    .toRepresentation();

            log.debug("Fetched role '{}'. Assigning to user '{}'", userRole.getName(), userId);

            usersResource.get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(userRole));

            log.info("Assigned default role 'USER' to user {}", userId);
        } catch (Exception e) {
            log.error("Failed to assign default role to user {}: {}", userId, e.getMessage(), e);
            throw new UserRegistrationException("User created, but failed to assign default role.");
        }
    }

}
