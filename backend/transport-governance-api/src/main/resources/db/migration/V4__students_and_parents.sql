-- =========================
-- Parents
-- =========================

CREATE TABLE parents (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    address TEXT,
    emergency_contact VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_parent_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

-- =========================
-- Students
-- =========================

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    school_id INT NOT NULL,
    parent_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    grade VARCHAR(20),
    section VARCHAR(10),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_student_school
        FOREIGN KEY (school_id)
            REFERENCES schools(id),

    CONSTRAINT fk_student_parent
        FOREIGN KEY (parent_id)
            REFERENCES parents(id)
);