-- =========================
-- Audit Logs
-- =========================

CREATE TABLE audit_logs (
                            id SERIAL PRIMARY KEY,

                            entity_type VARCHAR(100) NOT NULL,
                            entity_id INT NOT NULL,

                            action VARCHAR(100) NOT NULL,

                            old_value TEXT,
                            new_value TEXT,

                            performed_by INT,
                            ip_address VARCHAR(50),

                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_user
                                FOREIGN KEY (performed_by)
                                    REFERENCES users(id)
);