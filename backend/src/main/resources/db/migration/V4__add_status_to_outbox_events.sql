-- Migration to add missing 'status' column to outbox_events table
ALTER TABLE outbox_events 
ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'PENDING';

-- Add index for better query performance
CREATE INDEX IF NOT EXISTS idx_outbox_events_status ON outbox_events(status);

-- Add comment to the column
COMMENT ON COLUMN outbox_events.status IS 'Status of the outbox event: PENDING, PROCESSING, SENT, FAILED';
