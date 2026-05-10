# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./mvnw.cmd spring-boot:run

# Build (skip tests)
./mvnw.cmd clean package -DskipTests

# Run all tests
./mvnw.cmd test

# Run a single test class
./mvnw.cmd test -Dtest=BackendApplicationTests
```

MySQL must be running locally on port 3306 with a database named `umarket_db`. Spring will create/update tables automatically via `ddl-auto=update`.

## Architecture

Standard Spring Boot layered architecture: Controller → Service → Repository → Entity.

**Package root:** `com.UMarket.backend`

| Layer | Package | Responsibility |
|---|---|---|
| Controllers | `.controller` | HTTP endpoints, extract user from JWT, delegate to service |
| Services | `.service` | Business rules, validation, orchestration |
| Repositories | `.repository` | JPA data access, custom queries |
| Models | `.model` | JPA entities (6 tables) |
| Security | `.security` | JWT filter, Spring Security config |
| DTOs | `.dto` | `LoginRequest`, `LoginResponse` |

## Security & Authentication

- **JWT**: Stateless auth. Token generated on login, validated in `JwtAuthFilter` on every request.
- **Public endpoints**: `POST /api/users/register`, `GET /api/users/verify`, `POST /api/auth/login`.
- **Everything else** requires `Authorization: Bearer <token>`.
- **CORS** is configured centrally in `SecurityConfig` (localhost:3000, 5173, 5174). Never add `@CrossOrigin` to individual controllers.
- **Current user identity**: Always extract from `SecurityContextHolder`, never trust a query param for the caller's ID. Pattern used in all controllers:
  ```java
  private Long getAuthenticatedId() {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      return userService.findByEmail(email).orElseThrow().getId();
  }
  ```

## Key Business Rules

- **Registration**: Only `.edu` emails from a fixed list of 16 Guatemalan universities are accepted (validated in `UserService.isEducationalEmail`).
- **Email verification**: UUID token emailed on registration, expires in 24h. Users cannot log in until verified (`verified=true`).
- **Soft deletes**: Entities use an `active` boolean flag instead of hard deletes.
- **Images**: Max 5 per listing, uploaded to Cloudinary, stored as ordered `ListingImage` records. Only the listing owner can upload.
- **Rating**: Integer 1–5, enforced in `ReviewService.create`.

## Entity Relationships

```
User (seller) ──< Listing >── Category
Listing ──< ListingImage
Listing ──< Message (sender, recipient → User)
Listing ──< Review (author, seller → User)
```

## Entities & DB Tables

| Class | Table | Key fields |
|---|---|---|
| `User` | `users` | `name`, `email`, `password`, `university`, `verified`, `active`, `createdAt`, `verificationToken`, `tokenExpiration` |
| `Listing` | `listings` | `title`, `description`, `price`, `itemCondition` (→ `item_condition`), `active`, `publishedAt`, `seller`, `category` |
| `Category` | `categories` | `name`, `description` |
| `Message` | `messages` | `content`, `sentAt`, `read` (→ `is_read`), `sender`, `recipient`, `listing` |
| `Review` | `reviews` | `rating`, `comment`, `createdAt`, `author`, `seller`, `listing` |
| `ListingImage` | `listing_images` | `url`, `sortOrder`, `listing` |

## API Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/users/register` | Public | Register new user |
| GET | `/api/users/verify?token=` | Public | Verify email token |
| POST | `/api/auth/login` | Public | Login → JWT |
| GET | `/api/users` | Required | List active users |
| GET/PUT/DELETE | `/api/users/{id}` | Required | Get/update/deactivate user |
| GET/POST | `/api/listings` | Required | Feed / create listing |
| GET | `/api/listings/{id}` | Required | Get listing |
| GET | `/api/listings/seller/{id}` | Required | Listings by seller |
| GET | `/api/listings/search?term=` | Required | Search listings |
| DELETE | `/api/listings/{id}` | Required | Deactivate listing |
| POST | `/api/images/listing/{id}` | Required | Upload images (multipart, param: `files`) |
| GET/POST | `/api/categories` | Required | List / create category |
| GET/PUT/DELETE | `/api/categories/{id}` | Required | Get/update/delete category |
| POST | `/api/messages?recipientId=&listingId=` | Required | Send message |
| GET | `/api/messages/inbox` | Required | Inbox |
| GET | `/api/messages/unread` | Required | Unread count |
| PUT | `/api/messages/{id}/read` | Required | Mark as read |
| POST | `/api/reviews?sellerId=` | Required | Create review |
| GET | `/api/reviews/seller/{id}` | Required | Reviews for seller |
| GET | `/api/reviews/average/{id}` | Required | Average rating |

## External Services

All credentials are in `application.properties` (not committed; treated as dev-only config):
- **MySQL**: `localhost:3306/umarket_db`
- **Cloudinary**: image hosting — `CloudinaryService.uploadImage()` validates `image/*` content type before upload.
- **Gmail SMTP**: for verification emails — base URL configured via `app.url` property, consumed in `EmailService`.
- **JWT secret**: `jwt.secret` property, consumed via `@Value` in `JwtUtil`.

## Idioma
Responde siempre en español.

## Contexto del proyecto
UMarket es un marketplace exclusivo para estudiantes universitarios guatemaltecos.
La verificación de email .edu es el diferenciador principal — genera confianza entre compradores y vendedores.
El modelo de monetización está pendiente de definir, no implementar nada al respecto por ahora.
