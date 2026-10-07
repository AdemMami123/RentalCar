ALTER TABLE cars
    ADD COLUMN IF NOT EXISTS fleet_quantity INT NOT NULL DEFAULT 1;

ALTER TABLE cars
    ADD CONSTRAINT cars_fleet_quantity_positive CHECK (fleet_quantity > 0);