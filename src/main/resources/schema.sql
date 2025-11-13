-- Full Schema with Audit Trail and Enhanced Tables
-- Comprehensive Taxi Management System Database Schema

-- Drop existing tables to recreate with updates
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS levy_fine_disciplinary_workflow CASCADE;
DROP TABLE IF EXISTS levy_fines CASCADE;
DROP TABLE IF EXISTS levy_payments CASCADE;
DROP TABLE IF EXISTS member_finance CASCADE;
DROP TABLE IF EXISTS membership_application_documents CASCADE;
DROP TABLE IF EXISTS membership_application CASCADE;
DROP TABLE IF EXISTS payment_method CASCADE;
DROP TABLE IF EXISTS bank_payment CASCADE;
DROP TABLE IF EXISTS receipt CASCADE;
DROP TABLE IF EXISTS assoc_member CASCADE;
DROP TABLE IF EXISTS taxi CASCADE;
DROP TABLE IF EXISTS route CASCADE;
DROP TABLE IF EXISTS driver CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;

-- Table: User_Roles
CREATE TABLE user_roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    permissions JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: Users
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) UNIQUE,
    role_id INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES user_roles(role_id) ON DELETE CASCADE
);

-- Table: Driver
CREATE TABLE driver (
    driver_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL CHECK (license_number <> ''),
    contact_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: Route
CREATE TABLE route (
    route_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_point VARCHAR(100) NOT NULL,
    end_point VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Table: Taxi
CREATE TABLE taxi (
    taxi_id SERIAL PRIMARY KEY,
    plate_number VARCHAR(20) UNIQUE NOT NULL CHECK (plate_number <> ''),
    model VARCHAR(100),
    driver_id INT,
    route_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (driver_id) REFERENCES driver(driver_id) ON DELETE SET NULL,
    FOREIGN KEY (route_id) REFERENCES route(route_id) ON DELETE SET NULL
);

-- Table: Assoc_Member
CREATE TABLE assoc_member (
    assoc_member_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    squad_number VARCHAR(50) UNIQUE NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    blacklisted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: Membership_Application
CREATE TABLE membership_application (
    application_id SERIAL PRIMARY KEY,
    applicant_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    application_status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    route_id INT,
    secretary_reviewed BOOLEAN DEFAULT FALSE,
    chairperson_reviewed BOOLEAN DEFAULT FALSE,
    decision_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES route(route_id) ON DELETE SET NULL
);

-- Table: Membership_Application_Documents
CREATE TABLE membership_application_documents (
    document_id SERIAL PRIMARY KEY,
    application_id INT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_path VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES membership_application(application_id) ON DELETE CASCADE
);

-- Table: Member_Finance
CREATE TABLE member_finance (
    finance_id SERIAL PRIMARY KEY,
    assoc_member_id INT NOT NULL,
    joining_fee_paid BOOLEAN DEFAULT FALSE,
    joining_fee_amount DECIMAL(10, 2) NOT NULL,
    membership_card_issued BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE
);

-- Table: Payment_Method
CREATE TABLE payment_method (
    payment_method_id SERIAL PRIMARY KEY,
    method_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Table: Levy_Payments
CREATE TABLE levy_payments (
    levy_payment_id SERIAL PRIMARY KEY,
    assoc_member_id INT NOT NULL,
    week_start_date DATE NOT NULL,
    week_end_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'Pending',
    payment_method_id INT,
    receipt_number VARCHAR(50),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE,
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(payment_method_id) ON DELETE SET NULL
);

-- Table: Levy_Fines
CREATE TABLE levy_fines (
    levy_fine_id SERIAL PRIMARY KEY,
    assoc_member_id INT NOT NULL,
    fine_amount DECIMAL(10, 2) NOT NULL CHECK (fine_amount > 0),
    fine_reason TEXT NOT NULL,
    fine_status VARCHAR(50) NOT NULL DEFAULT 'Unpaid',
    payment_method_id INT,
    receipt_number VARCHAR(50),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE,
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(payment_method_id) ON DELETE SET NULL
);

-- Table: Levy_Fine_Disciplinary_Workflow
CREATE TABLE levy_fine_disciplinary_workflow (
    workflow_id SERIAL PRIMARY KEY,
    levy_fine_id INT NOT NULL,
    assoc_member_id INT NOT NULL,
    case_statement TEXT NOT NULL,
    secretary_decision VARCHAR(50) NOT NULL DEFAULT 'Pending',
    chairperson_decision VARCHAR(50) NOT NULL DEFAULT 'Pending',
    payment_arrangement TEXT,
    chairperson_override BOOLEAN DEFAULT FALSE,
    final_status VARCHAR(50) NOT NULL DEFAULT 'Ongoing',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (levy_fine_id) REFERENCES levy_fines(levy_fine_id) ON DELETE CASCADE,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE
);

-- Table: Bank_Payment
CREATE TABLE bank_payment (
    bank_payment_id SERIAL PRIMARY KEY,
    assoc_member_id INT NOT NULL,
    levy_payment_id INT,
    levy_fine_id INT,
    bank_name VARCHAR(100) NOT NULL,
    branch_code VARCHAR(20),
    account_number VARCHAR(50) NOT NULL,
    transaction_reference VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_date DATE NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE,
    FOREIGN KEY (levy_payment_id) REFERENCES levy_payments(levy_payment_id) ON DELETE CASCADE,
    FOREIGN KEY (levy_fine_id) REFERENCES levy_fines(levy_fine_id) ON DELETE CASCADE
);

-- Table: Receipt
CREATE TABLE receipt (
    receipt_id SERIAL PRIMARY KEY,
    assoc_member_id INT NOT NULL,
    levy_payment_id INT,
    levy_fine_id INT,
    bank_payment_id INT,
    receipt_number VARCHAR(50) UNIQUE NOT NULL,
    issued_by VARCHAR(100) NOT NULL,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE,
    FOREIGN KEY (levy_payment_id) REFERENCES levy_payments(levy_payment_id) ON DELETE CASCADE,
    FOREIGN KEY (levy_fine_id) REFERENCES levy_fines(levy_fine_id) ON DELETE CASCADE,
    FOREIGN KEY (bank_payment_id) REFERENCES bank_payment(bank_payment_id) ON DELETE CASCADE
);

-- Table: Notification
CREATE TABLE notification (
    notification_id SERIAL PRIMARY KEY,
    assoc_member_id INT NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(10) DEFAULT 'UNREAD' CHECK (status IN ('READ', 'UNREAD')),
    notification_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assoc_member_id) REFERENCES assoc_member(assoc_member_id) ON DELETE CASCADE
);

-- Table: Audit_Log
CREATE TABLE audit_log (
    audit_id SERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id INT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_by VARCHAR(100) NOT NULL,
    action_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_data JSONB,
    new_data JSONB
);

-- Create indexes for performance
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_role ON users(role_id);
CREATE INDEX idx_driver_license ON driver(license_number);
CREATE INDEX idx_taxi_plate ON taxi(plate_number);
CREATE INDEX idx_taxi_driver ON taxi(driver_id);
CREATE INDEX idx_taxi_route ON taxi(route_id);
CREATE INDEX idx_member_squad ON assoc_member(squad_number);
CREATE INDEX idx_member_blacklisted ON assoc_member(blacklisted);
CREATE INDEX idx_application_status ON membership_application(application_status);
CREATE INDEX idx_levy_payment_status ON levy_payments(payment_status);
CREATE INDEX idx_levy_payment_member ON levy_payments(assoc_member_id);
CREATE INDEX idx_levy_fine_status ON levy_fines(fine_status);
CREATE INDEX idx_levy_fine_member ON levy_fines(assoc_member_id);
CREATE INDEX idx_notification_status ON notification(status);
CREATE INDEX idx_notification_member ON notification(assoc_member_id);
CREATE INDEX idx_audit_table ON audit_log(table_name);
CREATE INDEX idx_audit_record ON audit_log(record_id);
CREATE INDEX idx_audit_action_by ON audit_log(action_by);
CREATE INDEX idx_audit_action_at ON audit_log(action_at);

-- Insert default user roles
INSERT INTO user_roles (role_name, permissions) VALUES
('Admin Clerk', '{"can_manage_payments": true, "can_view_reports": true, "can_manage_members": true}'),
('Secretary', '{"can_review_applications": true, "can_review_fines": true, "can_view_reports": true}'),
('Chairperson', '{"can_override_decisions": true, "can_approve_applications": true, "can_view_all": true}'),
('Cashier', '{"can_process_payments": true, "can_issue_receipts": true}');

-- Insert default payment methods
INSERT INTO payment_method (method_name, description) VALUES
('Cash', 'Cash payment'),
('Bank Transfer', 'Electronic bank transfer'),
('EFT', 'Electronic Funds Transfer'),
('Cheque', 'Cheque payment');

-- Insert sample data for testing
INSERT INTO driver (name, license_number, contact_number) VALUES
('John Doe', 'DL-2023-001', '0712345678'),
('Jane Smith', 'DL-2023-002', '0723456789');

INSERT INTO route (name, start_point, end_point, is_active) VALUES
('Route A', 'Johannesburg CBD', 'Soweto', true),
('Route B', 'Johannesburg CBD', 'Alexandra', true),
('Route C', 'Sandton', 'Randburg', true);

INSERT INTO taxi (plate_number, model, driver_id, route_id) VALUES
('GP-123-ABC', 'Toyota Quantum', 1, 1),
('GP-456-DEF', 'Nissan NV350', 2, 2);

INSERT INTO assoc_member (name, contact_number, squad_number, created_by) VALUES
('Michael Johnson', '0734567890', 'SQ-001', 'system'),
('Sarah Williams', '0745678901', 'SQ-002', 'system');

-- Create audit trigger function
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
DECLARE
    current_user_val VARCHAR(100);
BEGIN
    current_user_val := COALESCE(current_setting('app.current_user', TRUE), 'system');

    IF (TG_OP = 'INSERT') THEN
        INSERT INTO audit_log (table_name, record_id, action_type, action_by, old_data, new_data)
        VALUES (TG_TABLE_NAME,
                CASE TG_TABLE_NAME
                    WHEN 'users' THEN NEW.user_id
                    WHEN 'driver' THEN NEW.driver_id
                    WHEN 'route' THEN NEW.route_id
                    WHEN 'taxi' THEN NEW.taxi_id
                    WHEN 'assoc_member' THEN NEW.assoc_member_id
                    WHEN 'levy_payments' THEN NEW.levy_payment_id
                    WHEN 'levy_fines' THEN NEW.levy_fine_id
                    WHEN 'membership_application' THEN NEW.application_id
                    ELSE 0
                END,
                'INSERT', current_user_val, NULL, row_to_json(NEW));
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO audit_log (table_name, record_id, action_type, action_by, old_data, new_data)
        VALUES (TG_TABLE_NAME,
                CASE TG_TABLE_NAME
                    WHEN 'users' THEN NEW.user_id
                    WHEN 'driver' THEN NEW.driver_id
                    WHEN 'route' THEN NEW.route_id
                    WHEN 'taxi' THEN NEW.taxi_id
                    WHEN 'assoc_member' THEN NEW.assoc_member_id
                    WHEN 'levy_payments' THEN NEW.levy_payment_id
                    WHEN 'levy_fines' THEN NEW.levy_fine_id
                    WHEN 'membership_application' THEN NEW.application_id
                    ELSE 0
                END,
                'UPDATE', current_user_val, row_to_json(OLD), row_to_json(NEW));
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO audit_log (table_name, record_id, action_type, action_by, old_data, new_data)
        VALUES (TG_TABLE_NAME,
                CASE TG_TABLE_NAME
                    WHEN 'users' THEN OLD.user_id
                    WHEN 'driver' THEN OLD.driver_id
                    WHEN 'route' THEN OLD.route_id
                    WHEN 'taxi' THEN OLD.taxi_id
                    WHEN 'assoc_member' THEN OLD.assoc_member_id
                    WHEN 'levy_payments' THEN OLD.levy_payment_id
                    WHEN 'levy_fines' THEN OLD.levy_fine_id
                    WHEN 'membership_application' THEN OLD.application_id
                    ELSE 0
                END,
                'DELETE', current_user_val, row_to_json(OLD), NULL);
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for all tables
CREATE TRIGGER audit_users AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_driver AFTER INSERT OR UPDATE OR DELETE ON driver
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_route AFTER INSERT OR UPDATE OR DELETE ON route
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_taxi AFTER INSERT OR UPDATE OR DELETE ON taxi
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_assoc_member AFTER INSERT OR UPDATE OR DELETE ON assoc_member
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_levy_payments AFTER INSERT OR UPDATE OR DELETE ON levy_payments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_levy_fines AFTER INSERT OR UPDATE OR DELETE ON levy_fines
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_membership_application AFTER INSERT OR UPDATE OR DELETE ON membership_application
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at columns
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_driver_updated_at BEFORE UPDATE ON driver
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_route_updated_at BEFORE UPDATE ON route
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_taxi_updated_at BEFORE UPDATE ON taxi
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_assoc_member_updated_at BEFORE UPDATE ON assoc_member
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_levy_payments_updated_at BEFORE UPDATE ON levy_payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_levy_fines_updated_at BEFORE UPDATE ON levy_fines
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_membership_application_updated_at BEFORE UPDATE ON membership_application
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comments for documentation
COMMENT ON TABLE users IS 'System users with role-based access';
COMMENT ON TABLE user_roles IS 'Defines user roles and permissions';
COMMENT ON TABLE driver IS 'Taxi drivers information';
COMMENT ON TABLE route IS 'Taxi routes in the system';
COMMENT ON TABLE taxi IS 'Taxi vehicles information';
COMMENT ON TABLE assoc_member IS 'Association members (taxi owners/operators)';
COMMENT ON TABLE membership_application IS 'New membership applications';
COMMENT ON TABLE levy_payments IS 'Weekly levy payments by members';
COMMENT ON TABLE levy_fines IS 'Fines issued to members';
COMMENT ON TABLE levy_fine_disciplinary_workflow IS 'Disciplinary workflow for fines';
COMMENT ON TABLE notification IS 'Notifications sent to members';
COMMENT ON TABLE audit_log IS 'Comprehensive audit trail for all changes';
