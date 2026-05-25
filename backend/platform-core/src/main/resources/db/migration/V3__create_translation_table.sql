CREATE TABLE translation (
    id UUID PRIMARY KEY,
    translation_key VARCHAR(150) NOT NULL,
    language_code VARCHAR(35) NOT NULL,
    namespace VARCHAR(100) NOT NULL DEFAULT 'default',
    value TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_translation_key_language_namespace UNIQUE (translation_key, language_code, namespace)
);

CREATE INDEX idx_translation_lookup
    ON translation (translation_key, namespace, language_code);

CREATE INDEX idx_translation_namespace_language
    ON translation (language_code, namespace);

