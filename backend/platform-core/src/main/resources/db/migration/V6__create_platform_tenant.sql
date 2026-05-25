CREATE TABLE platform_tenant (
    tenant_id UUID PRIMARY KEY,
    country_code VARCHAR(16) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    default_timezone VARCHAR(64) NOT NULL,
    default_locale VARCHAR(35) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_platform_tenant_country_code UNIQUE (country_code)
);

CREATE INDEX idx_platform_tenant_country_code
    ON platform_tenant (country_code);

