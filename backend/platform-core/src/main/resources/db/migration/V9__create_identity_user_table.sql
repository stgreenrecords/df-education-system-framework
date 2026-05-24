CREATE TABLE identity_user (
    user_id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES platform_tenant (tenant_id),
    username VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    display_name VARCHAR(255),
    authority VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_identity_user_tenant_username UNIQUE (tenant_id, username),
    CONSTRAINT chk_identity_user_status CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT chk_identity_user_authority CHECK (authority IN ('ADMIN', 'USER'))
);

CREATE INDEX idx_identity_user_tenant_username
    ON identity_user (tenant_id, username);

CREATE INDEX idx_identity_user_tenant_authority
    ON identity_user (tenant_id, authority);

