# Car Rental Application - Modular Monolith Architecture

## Overview
This is a modular monolith architecture with Angular frontend and Spring Boot backend, connected to PostgreSQL.

## Architecture Principles
- **Modules**: Independent business domains with clear boundaries
- **Separation of Concerns**: Each module handles specific business logic
- **Reusability**: Shared utilities and base classes across modules
- **Scalability**: Easy to convert to microservices if needed

## Directory Structure

### Backend Structure
```
backend/
├── src/main/java/com/carrental/
│   ├── config/              # Global configuration
│   ├── security/            # Authentication & authorization
│   ├── shared/              # Shared utilities, exceptions, DTOs
│   ├── modules/             # Business modules
│   │   ├── car/
│   │   ├── booking/
│   │   ├── payment/
│   │   ├── user/
│   │   └── location/
│   └── CarRentalApplication.java
├── resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── db/migration/        # Flyway/Liquibase migrations
└── pom.xml
```

### Frontend Structure
```
frontend/
├── src/
│   ├── app/
│   │   ├── core/            # Core module (singleton services, guards)
│   │   ├── shared/          # Shared components, pipes, directives
│   │   ├── layout/          # Layout components
│   │   ├── modules/         # Feature modules
│   │   │   ├── car/
│   │   │   ├── booking/
│   │   │   ├── payment/
│   │   │   ├── user/
│   │   │   └── admin/
│   │   └── app.module.ts
│   ├── assets/
│   └── environments/
└── angular.json
```

## Module Breakdown

### Backend Modules

**1. Car Module**
- List available cars
- Car details and specifications
- Car maintenance tracking
- Inventory management with `fleetQuantity` per car model/fleet record

**2. Booking Module**
- Create/update bookings
- Booking status management
- Booking history
- Cancellation logic
- Date-aware capacity checks against overlapping bookings
- Alternative date suggestions when all fleet units are reserved

**3. Payment Module**
- Payment processing
- Invoice generation
- Payment history
- Refund management

**4. User Module**
- User registration & authentication
- User profile management
- License verification
- User preferences

**5. Location Module**
- Rental locations
- Location inventory
- Pickup/dropoff management

### Frontend Modules

**1. Car Module**
- Car listing page
- Car details page
- Search and filter
- Car availability calendar
- Fleet totals shown to customers

**2. Booking Module**
- Booking form
- Booking confirmation
- Booking history
- Booking management
- Selected-date total and available car counts
- Suggested available date ranges when no units remain

**3. Payment Module**
- Payment form
- Payment confirmation
- Invoice view
- Payment history

**4. User Module**
- Login/Register
- Profile management
- Account settings
- License management

**5. Admin Module**
- Dashboard
- Car management
- User management
- Booking management

## Technology Stack
- **Backend**: Spring Boot 3.x, Spring Data JPA, Spring Security
- **Frontend**: Angular 18+, TypeScript, RxJS
- **Database**: PostgreSQL
- **Build**: Maven (Backend), npm (Frontend)
