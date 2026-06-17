-- =========================
-- Invoices
-- =========================

CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL,
    vendor_id INT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,

    CONSTRAINT fk_invoice_student
        FOREIGN KEY (student_id)
            REFERENCES students(id),

    CONSTRAINT fk_invoice_vendor
        FOREIGN KEY (vendor_id)
            REFERENCES vendors(id)
);

-- =========================
-- Payments
-- =========================

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    invoice_id INT NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(50),
    transaction_reference VARCHAR(255),
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by INT,

    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id)
            REFERENCES invoices(id),

    CONSTRAINT fk_payment_user
        FOREIGN KEY (recorded_by)
            REFERENCES users(id)
);