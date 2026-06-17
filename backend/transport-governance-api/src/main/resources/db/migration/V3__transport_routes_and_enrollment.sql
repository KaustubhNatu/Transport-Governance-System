-- =========================
-- Routes
-- =========================

CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    vendor_id INT NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    start_location TEXT,
    end_location TEXT,
    vehicle_id INT,
    driver_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_route_vendor
        FOREIGN KEY (vendor_id)
            REFERENCES vendors(id),

    CONSTRAINT fk_route_vehicle
        FOREIGN KEY (vehicle_id)
            REFERENCES vehicles(id),

    CONSTRAINT fk_route_driver
        FOREIGN KEY (driver_id)
            REFERENCES drivers(id)
);

-- =========================
-- Student Transport Enrollment
-- =========================

CREATE TABLE student_transport (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL,
    vendor_id INT NOT NULL,
    route_id INT,
    vehicle_id INT,
    enrollment_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_st_vendor
        FOREIGN KEY (vendor_id)
            REFERENCES vendors(id),
    CONSTRAINT fk_st_route
        FOREIGN KEY (route_id)
            REFERENCES routes(id),

    CONSTRAINT fk_st_vehicle
        FOREIGN KEY (vehicle_id)
            REFERENCES vehicles(id)
);