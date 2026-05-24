CREATE TABLE translation_audit (
    id UUID PRIMARY KEY,
    translation_id UUID NOT NULL REFERENCES translation (id),
    actor VARCHAR(150) NOT NULL,
    translation_key VARCHAR(150) NOT NULL,
    language_code VARCHAR(35) NOT NULL,
    namespace VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_translation_audit_translation_changed_at
    ON translation_audit (translation_id, changed_at DESC);

