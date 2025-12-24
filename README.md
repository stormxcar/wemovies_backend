# WeMovies Backend

Spring Boot backend application for WeMovies platform - a movie streaming service.

## Features

- User authentication & authorization (JWT)
- Movie management system
- User profiles & watchlists
- Review & rating system
- Email notifications
- RESTful API

## Tech Stack

- **Backend**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Security**: JWT Authentication
- **Build Tool**: Maven
- **Java**: 17+

## Quick Setup

See [SETUP.md](SETUP.md) for detailed installation instructions.

1. **Clone repository**

   ```bash
   git clone <repository-url>
   cd wemovies-backend
   ```

2. **Configure environment** (see SETUP.md)

3. **Run application**
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

Base URL: `http://localhost:8080/api/`

### Main Endpoints

- `POST /api/auth/login` - User login
- `GET /api/movies` - Get movies
- `POST /api/auth/register` - User registration
- `GET /api/auth/verifyUser` - Verify authentication

## Project Structure

```
src/main/java/com/example/demo/
├── config/          # Configuration classes
├── controllers/     # REST controllers
├── models/          # JPA entities
├── repositories/    # Data repositories
├── services/        # Business logic
├── dto/            # Data transfer objects
├── enums/          # Enum classes
└── utils/          # Utility classes
```

## Security

- JWT-based authentication
- Password encryption with BCrypt
- CORS configuration for frontend
- Role-based access control

## Contributing

1. Create feature branch from `main`
2. Make changes and test thoroughly
3. Submit pull request with description

## License

Private project - All rights reserved.
