-- =========================
-- Vendors
-- =========================

CREATE TABLE vendors (
    id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    owner_user_id INT NOT NULL,
    company_name VARCHAR(255),
    contact_phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vendor_school
    FOREIGN KEY (school_id)
        REFERENCES schools(id),

    CONSTRAINT fk_vendor_owner
        FOREIGN KEY (owner_user_id)
            REFERENCES users(id)
);

-- =========================
-- Drivers
-- =========================

CREATE TABLE drivers (
    id SERIAL PRIMARY KEY,
    user_id INT,
    vendor_id INT NOT NULL,
    license_number VARCHAR(100) NOT NULL,
    license_expiry DATE,
    background_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_driver_user
        FOREIGN KEY (user_id)
            REFERENCES users(id),

        CONSTRAINT fk_driver_vendor
            FOREIGN KEY (vendor_id)
                REFERENCES vendors(id)
);

-- =========================
-- Vehicles
-- =========================

CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    vendor_id INT NOT NULL,
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    capacity INT,
    make VARCHAR(100),
    model VARCHAR(100),
    manufacture_year INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vehicle_vendor
        FOREIGN KEY (vendor_id)
            REFERENCES vendors(id)
);