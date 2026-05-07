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
- **Public endpoints**: `POST /api/usuarios/registro`, `GET /api/usuarios/verificar`, `POST /api/auth/login`.
- **Everything else** requires `Authorization: Bearer <token>`.
- **CORS** is configured centrally in `SecurityConfig` (localhost:5173 and 5174). Never add `@CrossOrigin` to individual controllers.
- **Current user identity**: Always extract from `SecurityContextHolder`, never trust a query param for the caller's ID. Pattern used in all controllers:
  ```java
  private Long getIdAutenticado() {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      return usuarioService.obtenerPorEmail(email).orElseThrow().getId();
  }
  ```

## Key Business Rules

- **Registration**: Only `.edu` emails from a fixed list of 16 Guatemalan universities are accepted (validated in `UsuarioService.esEmailEducativo`).
- **Email verification**: UUID token emailed on registration, expires in 24h. Users cannot log in until verified (`verificado=true`).
- **Soft deletes**: Entities use an `activo` boolean flag instead of hard deletes.
- **Images**: Max 5 per publication, uploaded to Cloudinary, stored as ordered `ImagenPublicacion` records. Only the publication owner can upload.
- **Calificacion**: Integer 1–5, enforced in `ResenaService.crear`.

## Entity Relationships

```
Usuario (vendedor) ──< Publicacion >── Categoria
Publicacion ──< ImagenPublicacion
Publicacion ──< Mensaje (remitente, destinatario → Usuario)
Publicacion ──< Resena (autor, vendedor → Usuario)
```

## External Services

All credentials are in `application.properties` (not committed-safe; treated as dev-only config):
- **MySQL**: `localhost:3306/umarket_db`
- **Cloudinary**: image hosting — `CloudinaryService.subirImagen()` validates `image/*` content type before upload.
- **Gmail SMTP**: for verification emails — base URL configured via `app.url` property, consumed in `EmailService`.
- **JWT secret**: `jwt.secret` property, consumed via `@Value` in `JwtUtil`.
