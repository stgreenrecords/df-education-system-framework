CREATE TABLE audit_event (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES platform_tenant (tenant_id),
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(150) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    actor VARCHAR(150) NOT NULL,
    old_value_json JSONB,
    new_value_json JSONB,
    metadata_json JSONB,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_event_tenant_occurred_at
    ON audit_event (tenant_id, occurred_at DESC);

CREATE INDEX idx_audit_event_tenant_entity_type_occurred_at
    ON audit_event (tenant_id, entity_type, occurred_at DESC);

CREATE INDEX idx_audit_event_tenant_actor_occurred_at
    ON audit_event (tenant_id, actor, occurred_at DESC);

