🍽️ Restaurant Review Backend

This is a Spring Boot backend service for a full-stack restaurant review platform. It supports user registration via frontend, JWT-based authentication using Keycloak, and role-based access for managing restaurants and reviews. It also integrates with Elasticsearch, Kibana, and Docker.

<pre> ```bash src/ └── main/ └── java/ └── com.desirArman.restaurant/ ├── config/ # Keycloak config, security ├── controllers/ # REST API controllers ├── domain/ │ ├── dtos/ # DTOs for API requests/responses │ ├── entities/ # JPA Entities │ └── GeoLocation/ # Embedded location data ├── exceptions/ # Custom exception handling ├── mappers/ # DTO <-> Entity mappers ├── repositories/ # Spring Data JPA Repos └── services/ # Business logic ``` </pre>



🚀 Tech Stack
  - Spring Boot 3
  - PostgreSQL
  - Keycloak (for authentication & authorization)
  - Elasticsearch & Kibana (for search & monitoring)
  - Docker (for containerization)
  - Spring Security with @PreAuthorize

🔐 Keycloak Setup
  - Realm: `restaurant-review`
  - Clients:
    - `restaurant-review-app` (Frontend Client)
    - `restaurant-backend-service` (Backend Client for Admin API)
  - Roles:
    - `USER`: Can write/edit/delete their reviews
    - `OWNER`: Can create/update/delete restaurants they own
    - `ADMIN`: Can manage all data and users
      
🚀 Features Implemented
 ✅ Authentication & Authorization
- Login via Keycloak (`/protocol/openid-connect/token`)
- Admin REST API user registration (using backend service account)
- Token-based authentication with access & refresh tokens
✅ User Registration
- Backend controller to register new users via Keycloak Admin REST API
- Automatically assigns the `USER` role on registration
 ✅ Restaurant & Review APIs (Basic)
- Endpoints to create and fetch restaurants
- Endpoints to create and fetch reviews

🔄 In Progress:
- Role-Based Access Control
  - Restrict restaurant management to `OWNER`
  - Restrict review posting/editing to `USER`
  - `ADMIN` can override all access
  - Secure endpoints using `@PreAuthorize` annotations
  - Add Swagger/OpenAPI documentation
  - Add custom validation and global error handling
 
    
