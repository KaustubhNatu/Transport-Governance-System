-- =========================
-- Vehicle Documents
-- =========================

CREATE TABLE vehicle_documents (
                                   id SERIAL PRIMARY KEY,
                                   vehicle_id INT NOT NULL,
                                   document_type VARCHAR(50) NOT NULL,
                                   document_url TEXT,
                                   expiry_date DATE,
                                   uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_vehicle_doc_vehicle
                                       FOREIGN KEY (vehicle_id)
                                           REFERENCES vehicles(id)
                                           ON DELETE CASCADE
);

-- =========================
-- Driver Documents
-- =========================

CREATE TABLE driver_documents (
                                  id SERIAL PRIMARY KEY,
                                  driver_id INT NOT NULL,
                                  document_type VARCHAR(50) NOT NULL,
                                  document_url TEXT,
                                  expiry_date DATE,
                                  uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_driver_doc_driver
                                      FOREIGN KEY (driver_id)
                                          REFERENCES drivers(id)
                                          ON DELETE CASCADE
);