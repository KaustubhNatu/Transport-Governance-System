-- ============================================
-- Prevent duplicate driver document types
-- ============================================

ALTER TABLE driver_documents
    ADD CONSTRAINT uk_driver_document_type
        UNIQUE (driver_id, document_type);

-- ============================================
-- Prevent duplicate vehicle document types
-- ============================================

ALTER TABLE vehicle_documents
    ADD CONSTRAINT uk_vehicle_document_type
        UNIQUE (vehicle_id, document_type);