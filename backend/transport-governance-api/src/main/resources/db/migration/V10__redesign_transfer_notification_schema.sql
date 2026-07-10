-- =====================================================
-- V10__redesign_transfer_notification_schema.sql
-- Redesign Transfer Workflow -> Transfer Notification
-- =====================================================

-- =====================================================
-- Rename transfer_requests -> transfer_notifications
-- =====================================================

ALTER TABLE transfer_requests
    RENAME TO transfer_notifications;

-- =====================================================
-- Rename sequence
-- =====================================================

ALTER SEQUENCE transfer_requests_id_seq
    RENAME TO transfer_notifications_id_seq;

ALTER TABLE transfer_notifications
    ALTER COLUMN id
        SET DEFAULT nextval('transfer_notifications_id_seq');

ALTER SEQUENCE transfer_notifications_id_seq
    OWNED BY transfer_notifications.id;

-- =====================================================
-- Rename columns
-- =====================================================

ALTER TABLE transfer_notifications
    RENAME COLUMN initiated_by TO created_by;

ALTER TABLE transfer_notifications
    RENAME COLUMN initiated_at TO created_at;

-- =====================================================
-- Remove old approval workflow column
-- =====================================================

ALTER TABLE transfer_notifications
DROP COLUMN status;

-- =====================================================
-- Add notification timestamps
-- =====================================================

ALTER TABLE transfer_notifications
    ADD COLUMN vendor_notified_at TIMESTAMP;

ALTER TABLE transfer_notifications
    ADD COLUMN parent_notified_at TIMESTAMP;

-- =====================================================
-- Rename primary key constraint
-- =====================================================

ALTER TABLE transfer_notifications
    RENAME CONSTRAINT transfer_requests_pkey
    TO transfer_notifications_pkey;

-- =====================================================
-- Rename foreign key constraints
-- =====================================================

ALTER TABLE transfer_notifications
    RENAME CONSTRAINT fk_transfer_student
    TO fk_notification_student;

ALTER TABLE transfer_notifications
    RENAME CONSTRAINT fk_transfer_vendor
    TO fk_notification_vendor;

ALTER TABLE transfer_notifications
    RENAME CONSTRAINT fk_transfer_initiator
    TO fk_notification_creator;

-- =====================================================
-- Update transfer_events
-- =====================================================

ALTER TABLE transfer_events
    RENAME COLUMN transfer_id TO notification_id;

ALTER TABLE transfer_events
DROP CONSTRAINT fk_event_transfer;

ALTER TABLE transfer_events
    ADD CONSTRAINT fk_event_notification
        FOREIGN KEY (notification_id)
            REFERENCES transfer_notifications(id)
            ON DELETE CASCADE;