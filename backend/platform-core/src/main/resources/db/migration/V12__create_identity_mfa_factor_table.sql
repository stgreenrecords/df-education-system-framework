CREATE TABLE identity_mfa_factor (
    factor_id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES platform_tenant (tenant_id),
    user_id UUID NOT NULL REFERENCES identity_user (user_id) ON DELETE CASCADE,
    factor_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    secret_ciphertext TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMPTZ,
    CONSTRAINT chk_identity_mfa_factor_type CHECK (factor_type IN ('TOTP')),
    CONSTRAINT chk_identity_mfa_factor_status CHECK (status IN ('PENDING', 'ACTIVE'))
);

CREATE UNIQUE INDEX uk_identity_mfa_factor_active_totp_per_user
    ON identity_mfa_factor (tenant_id, user_id)
    WHERE factor_type = 'TOTP' AND status = 'ACTIVE';

CREATE UNIQUE INDEX uk_identity_mfa_factor_pending_totp_per_user
    ON identity_mfa_factor (tenant_id, user_id)
    WHERE factor_type = 'TOTP' AND status = 'PENDING';

CREATE INDEX idx_identity_mfa_factor_tenant_user
    ON identity_mfa_factor (tenant_id, user_id);

