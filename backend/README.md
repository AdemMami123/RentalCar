# Car Rental API - Backend

Spring Boot backend for the Car Rental application using modular monolith architecture.

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- PostgreSQL 12 or higher
- Git

## Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd car-rental-app/backend
```

### 2. Create Database
```bash
psql -U postgres -c "CREATE DATABASE car_rental_db;"
```

If the database already exists in pgAdmin, open **Query Tool** while connected as
the `postgres` user and run this once. The application connects as `car_user`,
not as the pgAdmin maintenance user:

```sql
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'car_user') THEN
    CREATE ROLE car_user LOGIN PASSWORD 'ademmami';
  ELSE
    ALTER ROLE car_user WITH LOGIN PASSWORD 'ademmami';
  END IF;
END
$$;

GRANT CONNECT ON DATABASE rentalcar TO car_user;
```

Select the `rentalcar` database in pgAdmin before running the following statements:

```sql
GRANT USAGE, CREATE ON SCHEMA public TO car_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO car_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO car_user;
```

### 3. Configure Database
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/car_rental_db
    username: postgres
    password: your_password
```

Or use environment variables in `.env`:
```bash
cp .env.example .env
# Edit .env with your configuration
```

Spring Boot does not load `.env` automatically. Either use the defaults in
`application.yml`, export the variables in the terminal before starting Maven,
or pass them through your IDE's run configuration. The current defaults are:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=rentalcar
DB_USER=car_user
DB_PASSWORD=ademmami
```

### 4. Build Project
```bash
mvn clean install
```

### 5. Run Application
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080/api`

## Project Structure

```
backend/
├── src/main/java/com/carrental/
│   ├── CarRentalApplication.java      # Main application class
│   ├── config/                         # Spring configurations
│   ├── security/                       # Security configuration & JWT
│   ├── shared/                         # Shared utilities
│   │   ├── exceptions/                 # Common exceptions
│   │   ├── dtos/                       # Common DTOs
│   │   ├── utils/                      # Utility classes
│   │   └── mappers/                    # Common mappers
│   └── modules/                        # Business modules
│       ├── car/                        # Car management
│       ├── booking/                    # Booking management
│       ├── payment/                    # Payment processing
│       ├── user/                       # User management
│       └── location/                   # Location management
├── src/main/resources/
│   ├── application.yml                 # Main configuration
│   ├── application-dev.yml             # Development configuration
│   └── db/migration/                   # Database migrations
├── src/test/java/                      # Unit & integration tests
└── pom.xml                             # Maven configuration
```

## API Endpoints

All endpoints are prefixed with `/api`

### Car Management
- `GET /cars` - List all cars
- `GET /cars/{id}` - Get car by ID
- `POST /cars` - Create new car
- `PUT /cars/{id}` - Update car
- `DELETE /cars/{id}` - Delete car

### Booking Management
- `GET /bookings` - List bookings
- `GET /bookings/{id}` - Get booking details
- `POST /bookings` - Create booking
- `PUT /bookings/{id}` - Update booking
- `DELETE /bookings/{id}` - Cancel booking

### User Management
- `POST /users/register` - Register new user
- `POST /users/login` - Login user
- `GET /users/profile` - Get user profile
- `PUT /users/profile` - Update profile

### Payment
- `POST /payments` - Process payment
- `GET /payments/{id}` - Get payment status
- `GET /invoices/{id}` - Get invoice

## Security

- JWT-based authentication
- CORS enabled for frontend (localhost:4200)
- Password encryption with BCrypt
- Request validation and sanitization

### JWT Token
Token is returned in login response. Include in subsequent requests:
```
Authorization: Bearer <token>
```

## Environment Profiles

### Development
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Production
```bash
mvn clean package -DskipTests
java -jar target/car-rental-api-1.0.0.jar --spring.profiles.active=prod
```

## Database Migrations

Migrations are automatically applied on startup using Flyway/Liquibase.
Add migration files to `src/main/resources/db/migration/`

## Testing

### Run all tests
```bash
mvn test
```

### Run specific test
```bash
mvn test -Dtest=UserControllerTest
```

### Run with coverage
```bash
mvn test jacoco:report
```

## Troubleshooting

### Database Connection Error
- Ensure PostgreSQL is running
- Verify credentials in application.yml
- Check database exists: `psql -U postgres -l`

### Port Already in Use
- Change port in application.yml: `server.port: 8081`
- Or kill process: `lsof -ti:8080 | xargs kill -9`

### Build Issues
- Clean Maven cache: `mvn clean`
- Update dependencies: `mvn dependency:resolve`
- Check Java version: `java -version`

## Contributing

1. Create feature branch: `git checkout -b feature/module-name`
2. Make changes following modular structure
3. Write tests for new features
4. Submit pull request

## License

MIT

## Support

For issues, please open a GitHub issue or contact the development team.
