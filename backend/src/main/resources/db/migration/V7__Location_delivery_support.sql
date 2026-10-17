ALTER TABLE locations
    ADD COLUMN IF NOT EXISTS location_type VARCHAR(20) NOT NULL DEFAULT 'OFFICE',
    ADD COLUMN IF NOT EXISTS pickup_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS return_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS delivery_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS timezone VARCHAR(80) NOT NULL DEFAULT 'UTC';

CREATE TABLE IF NOT EXISTS delivery_zones (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL,
    center_latitude DECIMAL(10, 8) NOT NULL,
    center_longitude DECIMAL(11, 8) NOT NULL,
    radius_km DECIMAL(8, 2) NOT NULL CHECK (radius_km > 0),
    minimum_notice_hours INT NOT NULL DEFAULT 24 CHECK (minimum_notice_hours >= 0),
    base_fee DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (base_fee >= 0),
    per_kilometer_fee DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (per_kilometer_fee >= 0),
    minimum_fee DECIMAL(10, 2),
    maximum_fee DECIMAL(10, 2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS booking_locations (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    location_type VARCHAR(20) NOT NULL CHECK (location_type IN ('PICKUP', 'RETURN')),
    fulfillment_type VARCHAR(30) NOT NULL CHECK (fulfillment_type IN ('OFFICE_PICKUP', 'DELIVERY')),
    rental_location_id BIGINT REFERENCES locations(id),
    address VARCHAR(500) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    delivery_instructions VARCHAR(1000),
    scheduled_at TIMESTAMP NOT NULL,
    timezone VARCHAR(80) NOT NULL DEFAULT 'UTC',
    delivery_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT uq_booking_location_type UNIQUE (booking_id, location_type)
);

CREATE INDEX IF NOT EXISTS idx_booking_locations_booking_id ON booking_locations(booking_id);
