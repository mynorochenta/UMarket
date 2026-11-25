# UMarket — Backend

API REST del marketplace universitario UMarket. Solo permite registro con correo institucional (.edu) verificado.

**Estado: en desarrollo**

## Stack
- Java 21 + Spring Boot 4.0
- Spring Security + JWT
- MySQL + Spring Data JPA
- Cloudinary (imágenes) y Gmail SMTP (verificación de correo)
- Maven

## Funcionalidades
- Registro con verificación de correo .edu
- Login con JWT
- Publicaciones con hasta 5 imágenes
- Categorías, mensajería y reseñas

## Cómo correrlo
1. Copiar `application-example.properties` a `application.properties` y llenar los valores
2. `./mvnw spring-boot:run`

Frontend: https://github.com/mynorochenta/umarket-frontend
