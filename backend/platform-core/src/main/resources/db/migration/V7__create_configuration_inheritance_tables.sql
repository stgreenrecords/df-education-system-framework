CREATE TABLE configuration_field_definition (
    field_key VARCHAR(255) PRIMARY KEY,
    value_type VARCHAR(32) NOT NULL,
    merge_strategy VARCHAR(32) NOT NULL,
    overrides_allowed BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT configuration_field_definition_value_type_chk CHECK (value_type IN ('STRING', 'STRING_SET')),
    CONSTRAINT configuration_field_definition_merge_strategy_chk CHECK (merge_strategy IN ('REPLACE', 'EXTEND_SET'))
);

CREATE TABLE configuration_value (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES platform_tenant (tenant_id),
    field_key VARCHAR(255) NOT NULL REFERENCES configuration_field_definition (field_key),
    scope_type VARCHAR(32) NOT NULL,
    scope_key VARCHAR(255) NOT NULL,
    value_json JSONB NOT NULL,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT configuration_value_scope_type_chk CHECK (scope_type IN ('COUNTRY', 'REGION', 'CITY', 'INSTITUTION', 'UNIT')),
    CONSTRAINT configuration_value_unique_scope UNIQUE (tenant_id, field_key, scope_type, scope_key)
);

CREATE INDEX idx_configuration_value_tenant_field
    ON configuration_value (tenant_id, field_key);

CREATE INDEX idx_configuration_value_tenant_scope
    ON configuration_value (tenant_id, scope_type, scope_key);

