# RentalCar

A modular monolith architecture for a car rental platform built with Spring Boot and Angular.

## Project Structure

```
car-rental-app/
├── backend/                 # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/carrental/
│   │   │   │   ├── config/              # Global configurations
│   │   │   │   ├── security/            # Security & authentication
│   │   │   │   ├── shared/              # Shared utilities & exceptions
│   │   │   │   └── modules/             # Business modules
│   │   │   │       ├── car/             # Car management module
│   │   │   │       ├── booking/         # Booking management module
│   │   │   │       ├── payment/         # Payment processing module
│   │   │   │       ├── user/            # User management module
│   │   │   │       └── location/        # Location management module
│   │   │   └── resources/               # Configuration files & migrations
│   │   └── test/                        # Tests
│   └── pom.xml
│
└── frontend/                # Angular Frontend
    ├── src/
    │   ├── app/
    │   │   ├── core/                    # Core services & guards
    │   │   ├── shared/                  # Shared components & utilities
    │   │   ├── layout/                  # Layout components
    │   │   └── modules/                 # Feature modules
    │   │       ├── car/                 # Car listing & details
    │   │       ├── booking/             # Booking management
    │   │       ├── payment/             # Payment handling
    │   │       ├── user/                # Authentication & profile
    │   │       └── admin/               # Admin dashboard
    │   ├── assets/                      # Static assets
    │   └── environments/                # Environment configurations
    ├── package.json
    ├── angular.json
    └── tsconfig.json
```

## Backend Architecture

### Modules

Each module follows a layered architecture:
- **Controller**: HTTP endpoints
- **Service**: Business logic
- **Repository**: Data access layer
- **Entity**: JPA entities
- **DTO**: Data transfer objects
- **Mapper**: Entity to DTO mapping
- **Exception**: Module-specific exceptions

### Shared Components
- **config**: Global Spring Boot configurations
- **security**: Authentication and authorization
- **shared/utils**: Utility classes and base entities
- **shared/exceptions**: Common exceptions
- **shared/dtos**: Common DTOs

## Frontend Architecture

### Core Module
- Singleton services
- Route guards
- Interceptors
- Authentication logic

### Shared Module
- Reusable components
- Common pipes
- Directives
- Shared services

### Feature Modules
- Car Management
- Booking Management
- Payment Processing
- User Management
- Admin Dashboard

## Getting Started

### Backend Setup

1. Ensure PostgreSQL is installed and running:
   ```bash
   # Create database
   psql -U postgres
   CREATE DATABASE car_rental_db;
   ```

2. Navigate to backend directory:
   ```bash
   cd backend
   ```

3. Update `application.yml` with your database credentials

4. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080/api`

### Frontend Setup

1. Navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start development server:
   ```bash
   npm start
   ```

The frontend will be available at `http://localhost:4200`

## Technology Stack

### Backend
- Java 17+
- Spring Boot 3.2
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven

### Frontend
- Angular 18+
- TypeScript 5.4+
- RxJS 7.8+
- Bootstrap/Material (Optional UI framework)

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Car Endpoints
- `GET /cars` - List all cars
- `GET /cars/{id}` - Get car details
- `POST /cars` - Create new car
- `PUT /cars/{id}` - Update car
- `DELETE /cars/{id}` - Delete car

### Booking Endpoints
- `GET /bookings` - List bookings
- `GET /bookings/{id}` - Get booking details
- `POST /bookings` - Create booking
- `PUT /bookings/{id}` - Update booking
- `DELETE /bookings/{id}` - Cancel booking

### User Endpoints
- `POST /users/register` - User registration
- `POST /users/login` - User login
- `GET /users/profile` - Get user profile
- `PUT /users/profile` - Update profile

## Development Guide

### Adding a New Module

1. Create module folder in `backend/src/main/java/com/carrental/modules/[module-name]`
2. Create subdirectories: `controller`, `service`, `repository`, `entity`, `dto`, `mapper`, `exception`
3. Implement entity extending `BaseEntity`
4. Create repository extending `JpaRepository`
5. Implement service with business logic
6. Create controller with REST endpoints
7. Add DTO and Mapper for data transformation

### Adding a Feature to Frontend

1. Create feature module in `frontend/src/app/modules/[feature-name]`
2. Create pages and components
3. Create service for API communication
4. Create models for type safety
5. Update routing in `app.routes.ts`

## Configuration

### Backend Configuration
Edit `backend/src/main/resources/application.yml`:
- Database connection settings
- JWT secret key
- CORS origins
- Server port

### Frontend Configuration
Edit `frontend/src/environments/environment.ts`:
- API URL
- Feature flags
- Application settings

## Security

- JWT-based authentication
- Role-based access control (RBAC)
- CORS enabled for frontend communication
- Request validation and sanitization

## Database Schema

Database migrations are managed in `backend/src/main/resources/db/migration/`

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Deployment

### Backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/car-rental-api-1.0.0.jar
```

### Frontend
```bash
cd frontend
npm run build:prod
# Deploy dist/car-rental-frontend to your web server
```

## License

MIT

## Support

For issues and questions, please create an issue in the repository.
