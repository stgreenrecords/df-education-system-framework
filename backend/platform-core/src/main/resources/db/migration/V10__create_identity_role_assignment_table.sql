CREATE TABLE identity_role_assignment (
    assignment_id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES platform_tenant (tenant_id),
    user_id UUID NOT NULL REFERENCES identity_user (user_id) ON DELETE CASCADE,
    role_code VARCHAR(64) NOT NULL,
    scope_path TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_identity_role_assignment_tenant_user_role_scope UNIQUE (tenant_id, user_id, role_code, scope_path),
    CONSTRAINT chk_identity_role_assignment_role_code CHECK (
        role_code IN (
            'COUNTRY_ADMIN',
            'REGION_ADMIN',
            'CITY_ADMIN',
            'INSTITUTION_ADMIN',
            'TEACHER',
            'STUDENT',
            'PARENT'
        )
    )
);

CREATE INDEX idx_identity_role_assignment_tenant_user
    ON identity_role_assignment (tenant_id, user_id);

CREATE INDEX idx_identity_role_assignment_tenant_role
    ON identity_role_assignment (tenant_id, role_code);

