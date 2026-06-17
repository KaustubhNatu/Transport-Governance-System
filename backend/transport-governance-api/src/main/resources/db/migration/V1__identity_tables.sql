CREATE TABLE schools (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);


CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    school_id INT,
    vendor_id INT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(50),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_users_school
        FOREIGN KEY (school_id)
            REFERENCES schools(id)
);


CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
            REFERENCES roles(id)
            ON DELETE CASCADE
);

INSERT INTO roles (name, description) VALUES
    ('SUPER_ADMIN', 'Platform administrator'),
    ('SCHOOL_ADMIN', 'School management'),
    ('VENDOR_ADMIN', 'Transport vendor manager'),
    ('DRIVER', 'Vehicle driver'),
    ('PARENT', 'Student parent');