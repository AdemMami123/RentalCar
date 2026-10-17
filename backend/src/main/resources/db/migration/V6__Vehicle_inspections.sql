CREATE TABLE vehicle_inspections (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    car_id BIGINT NOT NULL REFERENCES cars(id),
    inspection_type VARCHAR(20) NOT NULL CHECK (inspection_type IN ('PICKUP', 'RETURN')),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'COMPLETED')),
    inspected_by BIGINT NOT NULL REFERENCES users(id),
    inspected_at TIMESTAMP,
    odometer_reading BIGINT NOT NULL CHECK (odometer_reading >= 0),
    fuel_level NUMERIC(5,2) NOT NULL CHECK (fuel_level >= 0 AND fuel_level <= 100),
    exterior_condition VARCHAR(30) NOT NULL,
    interior_condition VARCHAR(30) NOT NULL,
    notes TEXT,
    customer_acknowledged_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT uq_vehicle_inspection_booking_type UNIQUE (booking_id, inspection_type)
);

CREATE INDEX idx_vehicle_inspections_car_id ON vehicle_inspections(car_id);
CREATE INDEX idx_vehicle_inspections_booking_id ON vehicle_inspections(booking_id);

CREATE TABLE inspection_damages (
    id BIGSERIAL PRIMARY KEY,
    inspection_id BIGINT NOT NULL REFERENCES vehicle_inspections(id) ON DELETE CASCADE,
    damage_type VARCHAR(50) NOT NULL,
    location_on_vehicle VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX idx_inspection_damages_inspection_id ON inspection_damages(inspection_id);

CREATE TABLE inspection_photos (
    id BIGSERIAL PRIMARY KEY,
    inspection_id BIGINT NOT NULL REFERENCES vehicle_inspections(id) ON DELETE CASCADE,
    damage_id BIGINT REFERENCES inspection_damages(id) ON DELETE SET NULL,
    photo_url VARCHAR(1000),
    storage_key VARCHAR(255) NOT NULL,
    category VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX idx_inspection_photos_inspection_id ON inspection_photos(inspection_id);
