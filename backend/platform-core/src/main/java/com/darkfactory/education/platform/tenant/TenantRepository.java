package com.darkfactory.education.platform.tenant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantRepository {

    private static final RowMapper<TenantRecord> TENANT_ROW_MAPPER = (rs, rowNum) -> new TenantRecord(
            rs.getObject("tenant_id", UUID.class),
            rs.getString("country_code"),
            rs.getString("display_name"),
            rs.getString("default_timezone"),
            rs.getString("default_locale")
    );

    private final JdbcTemplate jdbcTemplate;

    public TenantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TenantRecord> findSingleTenant() {
        List<TenantRecord> tenants = jdbcTemplate.query(
                """
                select tenant_id, country_code, display_name, default_timezone, default_locale
                from platform_tenant
                order by created_at asc
                """,
                TENANT_ROW_MAPPER
        );

        if (tenants.isEmpty()) {
            return Optional.empty();
        }
        if (tenants.size() > 1) {
            throw new IllegalStateException("Expected exactly one active deployment tenant but found " + tenants.size() + ".");
        }
        return Optional.of(tenants.getFirst());
    }

    public TenantRecord insert(TenantBootstrapConfiguration configuration) {
        UUID tenantId = UUID.randomUUID();
        jdbcTemplate.update(
                """
                insert into platform_tenant (tenant_id, country_code, display_name, default_timezone, default_locale)
                values (?, ?, ?, ?, ?)
                """,
                tenantId,
                configuration.countryCode(),
                configuration.displayName(),
                configuration.defaultTimezone(),
                configuration.defaultLocale()
        );
        return new TenantRecord(
                tenantId,
                configuration.countryCode(),
                configuration.displayName(),
                configuration.defaultTimezone(),
                configuration.defaultLocale()
        );
    }

    public TenantRecord update(TenantRecord existing, TenantBootstrapConfiguration configuration) {
        jdbcTemplate.update(
                """
                update platform_tenant
                set display_name = ?,
                    default_timezone = ?,
                    default_locale = ?,
                    updated_at = current_timestamp
                where tenant_id = ?
                """,
                configuration.displayName(),
                configuration.defaultTimezone(),
                configuration.defaultLocale(),
                existing.tenantId()
        );
        return new TenantRecord(
                existing.tenantId(),
                existing.countryCode(),
                configuration.displayName(),
                configuration.defaultTimezone(),
                configuration.defaultLocale()
        );
    }
}

