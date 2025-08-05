package com.desirArman.restaurant.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KeycloakAdminClientConfig {

    @Value("${keycloak.admin-client.server-url}")
    private String serverUrl;

    @Value("${keycloak.registration-client.realm}")
    private String realm;

    @Value("${keycloak.registration-client.client-id}")
    private String clientId;

    @Value("${keycloak.registration-client.client-secret}")
     private String clientSecret;

    @Bean
    public Keycloak keycloak() {
//        log.info("keycloak.admin-client.server-url"+ serverUrl);
//        log.info("keycloak.registration-client.realm"+ realm);
//        log.info("keycloak.registration-client.client-id"+ clientId);


        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}