CREATE TABLE configuration_inheritance_break_request (
    request_id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES platform_tenant (tenant_id),
    field_key VARCHAR(255) NOT NULL REFERENCES configuration_field_definition (field_key),
    target_scope_type VARCHAR(32) NOT NULL,
    target_scope_key VARCHAR(255) NOT NULL,
    target_scope_path_json JSONB NOT NULL,
    blocking_ancestor_scope_type VARCHAR(32) NOT NULL,
    blocking_ancestor_scope_key VARCHAR(255) NOT NULL,
    proposed_value_json JSONB NOT NULL,
    justification TEXT NOT NULL,
    requested_by VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT configuration_inheritance_break_request_target_scope_type_chk
        CHECK (target_scope_type IN ('REGION', 'CITY', 'INSTITUTION', 'UNIT')),
    CONSTRAINT configuration_inheritance_break_request_blocking_scope_type_chk
        CHECK (blocking_ancestor_scope_type IN ('COUNTRY', 'REGION', 'CITY', 'INSTITUTION')),
    CONSTRAINT configuration_inheritance_break_request_status_chk
        CHECK (status IN ('SUBMITTED'))
);

CREATE INDEX idx_configuration_inheritance_break_request_tenant_field
    ON configuration_inheritance_break_request (tenant_id, field_key);

