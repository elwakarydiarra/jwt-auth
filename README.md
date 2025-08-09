
# JWT-Auth Rental API

Une application Spring Boot + Angular permettant de gérer des locations immobilières avec authentification JWT.  

Une API REST sécurisée avec Spring Boot, JWT, Spring Security et JPA, permettant la gestion :

- des utilisateurs (authentification avec JWT),
- des locations (`rentals`),
- des messages (`messages`),
- avec documentation Swagger.

---

## Technologies utilisées

- Java 21
- Spring Boot 3.5.x
- Spring Security
- JWT (Json Web Token)
- JPA (Hibernate)
- MySQL / H2 (base de données)
- Maven
- Swagger / OpenAPI (Springdoc)

---

## Démarrage rapide

### 1. Cloner le projet

```bash
git clone https://github.com/elwakarydiarra/jwt-auth-rental.git
cd jwt-auth-rental
```

### 2. Configurer la base de données

Modifier le fichier `application.properties` ou `application.yml` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rental_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
```

### 3. Lancer l'application

```bash
mvn clean install
mvn spring-boot:run
```

---

## Authentification JWT

### Inscription

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "email": "test@example.com",
  "password": "123456",
  "name": "Jean Dupont"
}
```

### Connexion

```http
POST /api/auth/login
```

```json
{
  "email": "test@example.com",
  "password": "123456"
}
```

Réponse : un token JWT (à envoyer dans les `Authorization` headers pour les routes protégées) :

```http
Authorization: Bearer <token>
```

---



## Swagger - Documentation API

Accès à la doc interactive sur :

```
http://localhost:8080/swagger-ui.html
```

ou :

```
http://localhost:8080/swagger-ui/index.html
```

---

## Structure du projet

```
src/main/java/rentals/jwt_auth/
├── config/       # Configuration Spring (WebConfig, SwaggerConfig…)
├── controller/   # Contrôleurs REST (RentalController, UserController, AuthController…)
├── dto/          # Objets de transfert (UserRequest, LoginRequest…)
├── model/        # Entités JPA (Rental, User…)
├── repository/   # Interfaces JPA (UserRepository, RentalRepository…)
├── security/     # JWT, SecurityConfig, filtres…
├── service/      # Services métier (UserService, RentalService…)

```

---

## Auteur

Développé par El hadji Diarra(https://github.com/elwakarydiarra)

---
