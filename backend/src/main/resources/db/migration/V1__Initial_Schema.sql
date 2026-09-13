-- =====================================================
-- CAR RENTAL DATABASE INITIAL SCHEMA - PHASE 1
-- =====================================================

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    license_number VARCHAR(50),
    license_expiry DATE,
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- =====================================================
-- ROLES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_roles_name ON roles(name);

-- =====================================================
-- USER_ROLES JUNCTION TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- =====================================================
-- LOCATIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    email VARCHAR(100),
    opening_time TIME,
    closing_time TIME,
    available_cars INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_locations_city ON locations(city);
CREATE INDEX IF NOT EXISTS idx_locations_country ON locations(country);

-- =====================================================
-- CARS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS cars (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL CHECK (year >= 1900 AND year <= 2100),
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    license_plate VARCHAR(50) NOT NULL UNIQUE,
    vin VARCHAR(100) NOT NULL UNIQUE,
    car_type VARCHAR(50) NOT NULL CHECK (car_type IN ('SEDAN', 'SUV', 'VAN', 'TRUCK', 'COUPE', 'HATCHBACK')),
    seats INT DEFAULT 5 CHECK (seats > 0 AND seats <= 15),
    transmission VARCHAR(50) CHECK (transmission IN ('MANUAL', 'AUTOMATIC', 'CVT')),
    fuel_type VARCHAR(50) CHECK (fuel_type IN ('PETROL', 'DIESEL', 'HYBRID', 'ELECTRIC')),
    daily_rate DECIMAL(10, 2) NOT NULL CHECK (daily_rate > 0),
    status VARCHAR(50) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'RENTED', 'MAINTENANCE', 'RETIRED')),
    color VARCHAR(50),
    mileage BIGINT DEFAULT 0 CHECK (mileage >= 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_cars_status ON cars(status);
CREATE INDEX IF NOT EXISTS idx_cars_registration ON cars(registration_number);
CREATE INDEX IF NOT EXISTS idx_cars_type ON cars(car_type);

-- =====================================================
-- CAR_FEATURES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS car_features (
    id BIGSERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    feature_value VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_car_features_car_id ON car_features(car_id);

-- =====================================================
-- BOOKINGS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    pickup_location_id BIGINT NOT NULL,
    dropoff_location_id BIGINT NOT NULL,
    pickup_date TIMESTAMP NOT NULL,
    dropoff_date TIMESTAMP NOT NULL,
    total_cost DECIMAL(10, 2),
    booking_status VARCHAR(50) DEFAULT 'PENDING' CHECK (booking_status IN ('PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (car_id) REFERENCES cars(id),
    FOREIGN KEY (pickup_location_id) REFERENCES locations(id),
    FOREIGN KEY (dropoff_location_id) REFERENCES locations(id)
);

CREATE INDEX IF NOT EXISTS idx_bookings_user ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_car ON bookings(car_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(booking_status);
CREATE INDEX IF NOT EXISTS idx_bookings_pickup_date ON bookings(pickup_date);
CREATE INDEX IF NOT EXISTS idx_bookings_dropoff_date ON bookings(dropoff_date);

-- =====================================================
-- BOOKING_STATUS_HISTORY TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS booking_status_history (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_booking_status_history_booking ON booking_status_history(booking_id);
CREATE INDEX IF NOT EXISTS idx_booking_status_history_created ON booking_status_history(created_at);

-- =====================================================
-- PAYMENTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CREDIT_CARD', 'DEBIT_CARD', 'CASH', 'BANK_TRANSFER', 'PAYPAL')),
    payment_status VARCHAR(50) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX IF NOT EXISTS idx_payments_booking ON payments(booking_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(payment_status);
CREATE UNIQUE INDEX IF NOT EXISTS idx_payments_transaction ON payments(transaction_id);

-- =====================================================
-- INVOICES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    subtotal DECIMAL(10, 2),
    tax DECIMAL(10, 2),
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount > 0),
    invoice_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    invoice_status VARCHAR(50) DEFAULT 'PENDING' CHECK (invoice_status IN ('PENDING', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED')),
    paid_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 1,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_invoices_user ON invoices(user_id);
CREATE INDEX IF NOT EXISTS idx_invoices_booking ON invoices(booking_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(invoice_status);
CREATE UNIQUE INDEX IF NOT EXISTS idx_invoices_number ON invoices(invoice_number);

-- =====================================================
-- AUDIT_LOGS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    action VARCHAR(50),
    old_values TEXT,
    new_values TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at);

-- =====================================================
-- INSERT DEFAULT ROLES
-- =====================================================
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full access to the system')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('MANAGER', 'Manager with moderate access and reporting capabilities')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('USER', 'Regular user with booking and profile management')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('GUEST', 'Guest user with limited browsing capabilities')
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- INSERT SAMPLE LOCATIONS
-- =====================================================
INSERT INTO locations (name, address, city, country, phone, email, opening_time, closing_time)
SELECT 'Downtown Center', '123 Main Street', 'Tunis', 'Tunisia', '+216-71-123-456', 'downtown@carrental.com', '08:00:00', '20:00:00'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Downtown Center');

INSERT INTO locations (name, address, city, country, phone, email, opening_time, closing_time)
SELECT 'Airport Terminal', '456 Airport Road', 'Tunis', 'Tunisia', '+216-71-789-012', 'airport@carrental.com', '06:00:00', '23:00:00'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Airport Terminal');

INSERT INTO locations (name, address, city, country, phone, email, opening_time, closing_time)
SELECT 'Beach Resort', '789 Beach Avenue', 'Hammamet', 'Tunisia', '+216-72-345-678', 'beach@carrental.com', '07:00:00', '21:00:00'
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Beach Resort');

-- =====================================================
-- INSERT SAMPLE CARS
-- =====================================================
INSERT INTO cars (make, model, year, registration_number, license_plate, vin, car_type, seats, transmission, fuel_type, daily_rate, color)
SELECT 'Toyota', 'Corolla', 2023, 'REG001', 'PLT001', 'VIN001', 'SEDAN', 5, 'AUTOMATIC', 'PETROL', 50.00, 'Silver'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE registration_number = 'REG001');

INSERT INTO cars (make, model, year, registration_number, license_plate, vin, car_type, seats, transmission, fuel_type, daily_rate, color)
SELECT 'Honda', 'Civic', 2023, 'REG002', 'PLT002', 'VIN002', 'SEDAN', 5, 'AUTOMATIC', 'PETROL', 55.00, 'Blue'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE registration_number = 'REG002');

INSERT INTO cars (make, model, year, registration_number, license_plate, vin, car_type, seats, transmission, fuel_type, daily_rate, color)
SELECT 'Toyota', 'Hiace', 2022, 'REG003', 'PLT003', 'VIN003', 'VAN', 8, 'AUTOMATIC', 'DIESEL', 80.00, 'White'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE registration_number = 'REG003');

INSERT INTO cars (make, model, year, registration_number, license_plate, vin, car_type, seats, transmission, fuel_type, daily_rate, color)
SELECT 'Mercedes', 'C-Class', 2023, 'REG004', 'PLT004', 'VIN004', 'SEDAN', 5, 'AUTOMATIC', 'PETROL', 120.00, 'Black'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE registration_number = 'REG004');

INSERT INTO cars (make, model, year, registration_number, license_plate, vin, car_type, seats, transmission, fuel_type, daily_rate, color)
SELECT 'BMW', 'X5', 2023, 'REG005', 'PLT005', 'VIN005', 'SUV', 7, 'AUTOMATIC', 'PETROL', 150.00, 'Gray'
WHERE NOT EXISTS (SELECT 1 FROM cars WHERE registration_number = 'REG005');

-- =====================================================
-- GRANT PERMISSIONS TO CAR_USER
-- =====================================================
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO car_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO car_user;

-- =====================================================
-- END OF INITIAL SCHEMA
-- =====================================================