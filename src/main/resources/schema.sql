-- Drop existing tables if they exist
DROP TABLE IF EXISTS taxi_service_audit_log CASCADE;
DROP TABLE IF EXISTS taxis CASCADE;

-- Create taxis table
CREATE TABLE taxis (
    taxi_id BIGSERIAL PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL CHECK (year > 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 1),
    capacity INTEGER NOT NULL CHECK (capacity > 0 AND capacity <= 50),
    color VARCHAR(50),
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'ASSIGNED', 'MAINTENANCE', 'OUT_OF_SERVICE')),
    driver_id BIGINT,
    route_id BIGINT,
    fuel_type VARCHAR(20) CHECK (fuel_type IN ('PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID', 'CNG')),
    vehicle_type VARCHAR(30) CHECK (vehicle_type IN ('SEDAN', 'SUV', 'VAN', 'MINIBUS', 'HATCHBACK')),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create audit log table
CREATE TABLE taxi_service_audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id BIGINT NOT NULL,
    action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE')),
    action_by VARCHAR(100) NOT NULL,
    action_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_value TEXT,
    new_value TEXT,
    changes TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(200)
);

-- Create indexes for taxis table
CREATE INDEX idx_taxi_license_plate ON taxis(license_plate);
CREATE INDEX idx_taxi_driver_id ON taxis(driver_id);
CREATE INDEX idx_taxi_route_id ON taxis(route_id);
CREATE INDEX idx_taxi_status ON taxis(status);
CREATE INDEX idx_taxi_vehicle_type ON taxis(vehicle_type);
CREATE INDEX idx_taxi_fuel_type ON taxis(fuel_type);

-- Create indexes for audit log table
CREATE INDEX idx_audit_table_name ON taxi_service_audit_log(table_name);
CREATE INDEX idx_audit_record_id ON taxi_service_audit_log(record_id);
CREATE INDEX idx_audit_action_by ON taxi_service_audit_log(action_by);
CREATE INDEX idx_audit_action_type ON taxi_service_audit_log(action_type);
CREATE INDEX idx_audit_timestamp ON taxi_service_audit_log(action_timestamp);
CREATE INDEX idx_audit_table_record ON taxi_service_audit_log(table_name, record_id);

-- Drop existing trigger and function if they exist
DROP TRIGGER IF EXISTS taxis_audit_trigger ON taxis;
DROP FUNCTION IF EXISTS audit_taxis_changes();

-- Create audit trigger function
CREATE OR REPLACE FUNCTION audit_taxis_changes()
RETURNS TRIGGER AS $$
DECLARE
    old_data TEXT;
    new_data TEXT;
    changes_data TEXT;
    action_type_val VARCHAR(20);
    current_user_val VARCHAR(100);
BEGIN
    -- Get current user (can be customized based on your authentication system)
    current_user_val := COALESCE(current_setting('app.current_user', TRUE), 'system');

    -- Determine action type and prepare data
    IF (TG_OP = 'INSERT') THEN
        action_type_val := 'INSERT';
        old_data := NULL;
        new_data := row_to_json(NEW)::TEXT;
        changes_data := 'New record created';

        INSERT INTO taxi_service_audit_log (
            table_name, record_id, action_type, action_by,
            action_timestamp, old_value, new_value, changes
        ) VALUES (
            TG_TABLE_NAME, NEW.taxi_id, action_type_val, current_user_val,
            CURRENT_TIMESTAMP, old_data, new_data, changes_data
        );

        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        action_type_val := 'UPDATE';
        old_data := row_to_json(OLD)::TEXT;
        new_data := row_to_json(NEW)::TEXT;

        -- Generate detailed changes
        changes_data := '';
        IF OLD.license_plate <> NEW.license_plate THEN
            changes_data := changes_data || 'license_plate: ' || OLD.license_plate || ' -> ' || NEW.license_plate || '; ';
        END IF;
        IF OLD.model <> NEW.model THEN
            changes_data := changes_data || 'model: ' || OLD.model || ' -> ' || NEW.model || '; ';
        END IF;
        IF OLD.manufacturer <> NEW.manufacturer THEN
            changes_data := changes_data || 'manufacturer: ' || OLD.manufacturer || ' -> ' || NEW.manufacturer || '; ';
        END IF;
        IF OLD.year <> NEW.year THEN
            changes_data := changes_data || 'year: ' || OLD.year || ' -> ' || NEW.year || '; ';
        END IF;
        IF OLD.capacity <> NEW.capacity THEN
            changes_data := changes_data || 'capacity: ' || OLD.capacity || ' -> ' || NEW.capacity || '; ';
        END IF;
        IF OLD.status <> NEW.status THEN
            changes_data := changes_data || 'status: ' || OLD.status || ' -> ' || NEW.status || '; ';
        END IF;
        IF (OLD.driver_id IS DISTINCT FROM NEW.driver_id) THEN
            changes_data := changes_data || 'driver_id: ' || COALESCE(OLD.driver_id::TEXT, 'NULL') || ' -> ' || COALESCE(NEW.driver_id::TEXT, 'NULL') || '; ';
        END IF;
        IF (OLD.route_id IS DISTINCT FROM NEW.route_id) THEN
            changes_data := changes_data || 'route_id: ' || COALESCE(OLD.route_id::TEXT, 'NULL') || ' -> ' || COALESCE(NEW.route_id::TEXT, 'NULL') || '; ';
        END IF;

        IF changes_data = '' THEN
            changes_data := 'No significant changes detected';
        END IF;

        INSERT INTO taxi_service_audit_log (
            table_name, record_id, action_type, action_by,
            action_timestamp, old_value, new_value, changes
        ) VALUES (
            TG_TABLE_NAME, NEW.taxi_id, action_type_val, current_user_val,
            CURRENT_TIMESTAMP, old_data, new_data, changes_data
        );

        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        action_type_val := 'DELETE';
        old_data := row_to_json(OLD)::TEXT;
        new_data := NULL;
        changes_data := 'Record deleted';

        INSERT INTO taxi_service_audit_log (
            table_name, record_id, action_type, action_by,
            action_timestamp, old_value, new_value, changes
        ) VALUES (
            TG_TABLE_NAME, OLD.taxi_id, action_type_val, current_user_val,
            CURRENT_TIMESTAMP, old_data, new_data, changes_data
        );

        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for taxis table
CREATE TRIGGER taxis_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON taxis
FOR EACH ROW EXECUTE FUNCTION audit_taxis_changes();

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_taxis_updated_at
BEFORE UPDATE ON taxis
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data (optional - for testing)
INSERT INTO taxis (license_plate, model, manufacturer, year, capacity, color, status, fuel_type, vehicle_type, notes)
VALUES
    ('TX-001-ABC', 'Camry', 'Toyota', 2022, 4, 'White', 'AVAILABLE', 'HYBRID', 'SEDAN', 'Well maintained vehicle'),
    ('TX-002-DEF', 'Accord', 'Honda', 2021, 4, 'Black', 'AVAILABLE', 'PETROL', 'SEDAN', 'New vehicle'),
    ('TX-003-GHI', 'Sienna', 'Toyota', 2023, 7, 'Silver', 'AVAILABLE', 'HYBRID', 'VAN', 'Family vehicle');

-- Comments for documentation
COMMENT ON TABLE taxis IS 'Stores information about taxis in the fleet';
COMMENT ON TABLE taxi_service_audit_log IS 'Audit log for tracking all changes to taxi records';
COMMENT ON COLUMN taxis.status IS 'Status can be: AVAILABLE, ASSIGNED, MAINTENANCE, OUT_OF_SERVICE';
COMMENT ON COLUMN taxis.fuel_type IS 'Fuel type: PETROL, DIESEL, ELECTRIC, HYBRID, CNG';
COMMENT ON COLUMN taxis.vehicle_type IS 'Vehicle type: SEDAN, SUV, VAN, MINIBUS, HATCHBACK';
