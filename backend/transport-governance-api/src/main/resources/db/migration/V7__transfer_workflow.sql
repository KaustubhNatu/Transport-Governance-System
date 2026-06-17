-- =========================
-- Transfer Requests
-- =========================

CREATE TABLE transfer_requests (
                                   id SERIAL PRIMARY KEY,
                                   student_id INT NOT NULL,
                                   vendor_id INT NOT NULL,

                                   dues_status VARCHAR(50) DEFAULT 'UNKNOWN',
                                   dues_amount NUMERIC(10,2) DEFAULT 0,

                                   status VARCHAR(50) NOT NULL,

                                   initiated_by INT NOT NULL,
                                   initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   completed_at TIMESTAMP,

                                   CONSTRAINT fk_transfer_student
                                       FOREIGN KEY (student_id)
                                           REFERENCES students(id),

                                   CONSTRAINT fk_transfer_vendor
                                       FOREIGN KEY (vendor_id)
                                           REFERENCES vendors(id),

                                   CONSTRAINT fk_transfer_initiator
                                       FOREIGN KEY (initiated_by)
                                           REFERENCES users(id)
);

-- =========================
-- Transfer Events (Audit Trail)
-- =========================

CREATE TABLE transfer_events (
                                 id SERIAL PRIMARY KEY,
                                 transfer_id INT NOT NULL,
                                 event_type VARCHAR(100) NOT NULL,
                                 notes TEXT,
                                 performed_by INT,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_event_transfer
                                     FOREIGN KEY (transfer_id)
                                         REFERENCES transfer_requests(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_event_user
                                     FOREIGN KEY (performed_by)
                                         REFERENCES users(id)
);