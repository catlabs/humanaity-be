# Humanaity Backend

Backend Spring Boot implémentant un système d'authentification JWT complet avec architecture réactive et sécurité moderne.

## Stack Technique

- **Spring Boot 3.5.0** (Java 17)
- **Spring Security WebFlux** - Architecture réactive et non-bloquante
- **Spring Data JPA** - Accès aux données avec Hibernate
- **JWT (JJWT 0.12.5)** - Tokens stateless pour l'authentification
- **BCrypt** - Hashing sécurisé des mots de passe
- **H2 Database** - Base de données embarquée
- **Lombok** - Réduction du code boilerplate
- **GraphQL** - API GraphQL avec WebSocket
- **Spring AI** - Intégration OpenAI

## Fonctionnalités d'Authentification

### Endpoints REST

- `POST /auth/signup` - Création de compte avec validation
- `POST /auth/login` - Connexion et génération de tokens
- `POST /auth/refresh` - Rafraîchissement automatique des tokens
- `POST /auth/logout` - Déconnexion et invalidation des tokens

### Architecture de Sécurité

**JWT Stateless**
- Access tokens (15 minutes) - Tokens de courte durée pour les requêtes
- Refresh tokens (7 jours) - Tokens de longue durée stockés en base
- Validation de signature et expiration à chaque requête
- Extraction sécurisée des claims (email, roles)

**Spring Security WebFlux**
- Filtre JWT personnalisé (`JwtAuthenticationWebFilter`)
- Convertisseur d'authentification (`JwtServerAuthenticationConverter`)
- Configuration réactive avec `ServerHttpSecurity`
- Routes publiques `/auth/**` exemptées de l'authentification
- Protection automatique de toutes les autres routes

**Gestion des Mots de Passe**
- Hashing avec BCrypt (10 rounds par défaut)
- Validation côté serveur avant stockage
- Vérification sécurisée lors du login (pas de comparaison en clair)

## Architecture

### Structure en Couches

```
Controller → Service → Repository → Entity
```

- **Controllers** (`@RestController`) - Points d'entrée REST, gestion des erreurs HTTP
- **Services** (`@Service`) - Logique métier, transactions (`@Transactional`)
- **Repositories** (`JpaRepository`) - Accès aux données, requêtes personnalisées
- **Entities** (`@Entity`) - Modèle de données JPA avec relations

### DTOs et Validation

- `SignupRequest` - Validation email unique, confirmation de mot de passe
- `AuthRequest` - Authentification avec email/password
- `AuthResponse` - Retour des tokens (access + refresh)
- Séparation claire entre couche présentation et modèle de données

### Gestion Transactionnelle

- `@Transactional` sur toutes les opérations modifiant la base
- Rollback automatique en cas d'erreur
- Isolation des opérations critiques (signup, login, refresh, logout)

## Compétences Démontrées

✅ **Sécurité Backend**
- Implémentation complète JWT avec refresh tokens
- Protection contre les attaques courantes (CSRF désactivé pour API stateless)
- Hashing sécurisé des mots de passe (jamais stockés en clair)
- Validation et sanitization des entrées

✅ **Architecture Réactive**
- Spring WebFlux pour la scalabilité
- Filtres de sécurité non-bloquants
- Gestion asynchrone des requêtes

✅ **Bonnes Pratiques Spring**
- Injection de dépendances (constructor injection)
- Configuration externalisée (`application.properties`)
- Séparation des responsabilités (SRP)
- Gestion d'erreurs structurée

✅ **Qualité de Code**
- Code propre et maintenable
- Transactions explicites
- DTOs pour l'isolation des couches
- Configuration CORS pour le frontend

## Configuration

```properties
# JWT
jwt.secret=your-secret-key...
jwt.access-token-expiration=900000      # 15 minutes
jwt.refresh-token-expiration=604800000  # 7 jours
```

## Démarrage

```bash
mvn spring-boot:run
```

L'API est accessible sur `http://localhost:8080`

