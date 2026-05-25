package com.darkfactory.education.platform;

import com.darkfactory.education.identityaccess.auth.AuthenticationTokenService;
import com.darkfactory.education.identityaccess.auth.IdentityBootstrapService;
import com.darkfactory.education.identityaccess.auth.IdentityUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.darkfactory.education.platform.tenant.TenantBootstrapRunner;
import com.darkfactory.education.platform.tenant.TenantContext;
import com.darkfactory.education.platform.tenant.TenantContextService;
import com.darkfactory.education.platform.translation.TranslationCache;
import com.darkfactory.education.platform.translation.TranslationCacheKey;
import com.darkfactory.education.platform.translation.TranslationCacheWarmup;
import com.darkfactory.education.platform.translation.TranslationProperties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
class EducationSystemApplicationIT {

    private static final String JWT_SECRET = "local-test-jwt-secret-value-1234567890";
    private static final String BOOTSTRAP_ADMIN_USERNAME = "bootstrap-admin";
    private static final String BOOTSTRAP_ADMIN_PASSWORD = "BootstrapPassword!123";
    private static final String BOOTSTRAP_ADMIN_DISPLAY_NAME = "Bootstrap Administrator";
    private static final UUID GREETING_EN_ID = UUID.fromString("00000000-0000-0000-0000-000000000301");
    private static final UUID GREETING_FR_ID = UUID.fromString("00000000-0000-0000-0000-000000000302");
    private static final UUID STATUS_READY_EN_ID = UUID.fromString("00000000-0000-0000-0000-000000000303");

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("education_framework")
            .withUsername("education_framework")
            .withPassword("education_framework");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("edu.tenant.country-code", () -> "pl");
        registry.add("edu.tenant.display-name", () -> "Poland Reference Deployment");
        registry.add("edu.tenant.timezone", () -> "Europe/Warsaw");
        registry.add("edu.tenant.locale", () -> "pl-PL");
        registry.add("edu.translation.default-language", () -> "fr");
        registry.add("edu.translation.global-fallback-language", () -> "en");
        registry.add("edu.translation.cache-ttl", () -> "PT2M");
        registry.add("edu.auth.jwt-secret", () -> JWT_SECRET);
        registry.add("edu.auth.bootstrap-admin-username", () -> BOOTSTRAP_ADMIN_USERNAME);
        registry.add("edu.auth.bootstrap-admin-password", () -> BOOTSTRAP_ADMIN_PASSWORD);
        registry.add("edu.auth.bootstrap-admin-display-name", () -> BOOTSTRAP_ADMIN_DISPLAY_NAME);
    }

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @Autowired
    private TenantBootstrapRunner tenantBootstrapRunner;

    @Autowired
    private TenantContextService tenantContextService;

    @Autowired
    private TranslationCache translationCache;

    @Autowired
    private TranslationCacheWarmup translationCacheWarmup;

    @Autowired
    private TranslationProperties translationProperties;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    @Autowired
    private IdentityUserRepository identityUserRepository;

    @Autowired
    private IdentityBootstrapService identityBootstrapService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        resetAuditData();
        resetConfigurationData();
        resetTranslationData();
        resetIdentityData();
    }

    @Test
    void contextStartsAndConnectsToPostgres() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(2)).isTrue();
        }

        Integer one = jdbcTemplate.queryForObject("select 1", Integer.class);
        assertThat(one).isEqualTo(1);
    }

    @Test
    void flywayBootstrapMigrationsAreAppliedOnStartup() {
        Integer markerCount = jdbcTemplate.queryForObject(
                "select count(*) from platform_bootstrap_marker where marker_key = 'baseline'",
                Integer.class
        );

        assertThat(markerCount).isEqualTo(1);
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("11");
    }

    @Test
    void reRunningFlywayDoesNotReapplyExistingMigrations() {
        Integer historyCountBefore = jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history where success = true",
                Integer.class
        );

        MigrateResult migrateResult = flyway.migrate();

        Integer historyCountAfter = jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history where success = true",
                Integer.class
        );

        assertThat(migrateResult.migrationsExecuted).isZero();
        assertThat(historyCountAfter).isEqualTo(historyCountBefore);
    }

    @Test
    void flywayAppliesMigrationsInVersionOrder() {
        String[] appliedVersions = Arrays.stream(flyway.info().applied())
                .map(info -> info.getVersion().getVersion())
                .toArray(String[]::new);

        assertThat(appliedVersions).containsExactly("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
    }

    @Test
    void bootstrapAdminIsCreatedForTheActiveDeploymentTenant() {
        Integer userCount = jdbcTemplate.queryForObject("select count(*) from identity_user", Integer.class);
        Integer roleAssignmentCount = jdbcTemplate.queryForObject("select count(*) from identity_role_assignment", Integer.class);

        assertThat(userCount).isEqualTo(1);
        assertThat(roleAssignmentCount).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select username from identity_user", String.class))
                .isEqualTo(BOOTSTRAP_ADMIN_USERNAME);
        assertThat(jdbcTemplate.queryForObject("select authority from identity_user", String.class))
                .isEqualTo("ADMIN");
        assertThat(jdbcTemplate.queryForObject("select status from identity_user", String.class))
                .isEqualTo("ACTIVE");
        assertThat(jdbcTemplate.queryForObject("select role_code from identity_role_assignment", String.class))
                .isEqualTo("COUNTRY_ADMIN");
        assertThat(jdbcTemplate.queryForObject("select scope_path from identity_role_assignment", String.class))
                .isEqualTo("TENANT:%s".formatted(tenantContextService.getActiveTenant().tenantId()));
    }

    @Test
    void validCredentialsReturnBearerAccessToken() throws Exception {
        mockMvc.perform(post("/api/v1/identity/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());
    }

    @Test
    void invalidCredentialsReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/identity/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"wrong-password"}
                                """.formatted(BOOTSTRAP_ADMIN_USERNAME)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedIdentityEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/identity/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredTokenReturnsUnauthorizedForProtectedEndpoint() throws Exception {
        String expiredToken = authenticationTokenService.issueAccessToken(
                identityUserRepository.findByTenantAndUsername(
                                tenantContextService.getActiveTenant().tenantId(),
                                BOOTSTRAP_ADMIN_USERNAME
                        )
                        .orElseThrow()
                        .toPrincipal(),
                OffsetDateTime.now().minusMinutes(30),
                Duration.ofMinutes(5)
        ).accessToken();

        mockMvc.perform(get("/api/v1/identity/me")
                        .header("Authorization", bearer(expiredToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanRegisterUserAndThatUserCanLogIn() throws Exception {
        String adminToken = loginAndExtractAccessToken(BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_PASSWORD);

        mockMvc.perform(post("/api/v1/identity/users")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"teacher.one",
                                  "initialPassword":"TeacherPassword!123",
                                  "displayName":"Teacher One",
                                  "status":"ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("teacher.one"))
                .andExpect(jsonPath("$.displayName").value("Teacher One"))
                .andExpect(jsonPath("$.authority").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Integer auditCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from audit_event
                where entity_type = 'IDENTITY_USER'
                  and action_type = 'CREATE'
                  and actor = ?
                  and new_value_json ->> 'username' = ?
                """,
                Integer.class,
                BOOTSTRAP_ADMIN_USERNAME,
                "teacher.one"
        );
        assertThat(auditCount).isEqualTo(1);

        String userToken = loginAndExtractAccessToken("teacher.one", "TeacherPassword!123");

        mockMvc.perform(get("/api/v1/identity/me")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("teacher.one"))
                .andExpect(jsonPath("$.displayName").value("Teacher One"))
                .andExpect(jsonPath("$.authority").value("USER"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").isEmpty());
    }

    @Test
    void countryAdminCanAssignTeacherRoleAndTeacherCannotAccessAnotherInstitution() throws Exception {
        String adminToken = loginAndExtractAccessToken(BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_PASSWORD);
        UUID teacherUserId = createUserAndExtractUserId(
                adminToken,
                "teacher.scope",
                "TeacherPassword!123",
                "Teacher Scope"
        );

        assignRole(
                adminToken,
                teacherUserId,
                "teacher",
                institutionScopePathJson("school-01")
        );

        mockMvc.perform(get("/api/v1/identity/users/{userId}/role-assignments", teacherUserId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleCode").value("teacher"))
                .andExpect(jsonPath("$[0].scopePath[0].scopeType").value("TENANT"))
                .andExpect(jsonPath("$[0].scopePath[1].scopeType").value("INSTITUTION"))
                .andExpect(jsonPath("$[0].scopePath[1].scopeKey").value("school-01"));

        Integer auditCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from audit_event
                where entity_type = 'IDENTITY_ROLE_ASSIGNMENT'
                  and action_type = 'CREATE'
                  and actor = ?
                  and new_value_json ->> 'roleCode' = ?
                """,
                Integer.class,
                BOOTSTRAP_ADMIN_USERNAME,
                "teacher"
        );
        assertThat(auditCount).isEqualTo(1);

        String teacherToken = loginAndExtractAccessToken("teacher.scope", "TeacherPassword!123");

        mockMvc.perform(get("/api/v1/identity/access/institutions/{institutionKey}/teaching-view", "school-01")
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permission").value("VIEW_INSTITUTION"))
                .andExpect(jsonPath("$.effectiveRoles[0]").value("teacher"));

        mockMvc.perform(get("/api/v1/identity/access/institutions/{institutionKey}/teaching-view", "school-02")
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/identity/access/institutions/{institutionKey}/management", "school-01")
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void institutionAdminCanManageOwnInstitutionButNotAnother() throws Exception {
        String adminToken = loginAndExtractAccessToken(BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_PASSWORD);
        UUID institutionAdminUserId = createUserAndExtractUserId(
                adminToken,
                "institution.admin",
                "InstitutionPassword!123",
                "Institution Admin"
        );

        assignRole(
                adminToken,
                institutionAdminUserId,
                "institution-admin",
                institutionScopePathJson("school-01")
        );

        String institutionAdminToken = loginAndExtractAccessToken("institution.admin", "InstitutionPassword!123");

        mockMvc.perform(post("/api/v1/identity/access/institutions/{institutionKey}/management", "school-01")
                        .header("Authorization", bearer(institutionAdminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permission").value("MANAGE_INSTITUTION"))
                .andExpect(jsonPath("$.effectiveRoles[0]").value("institution-admin"));

        mockMvc.perform(post("/api/v1/identity/access/institutions/{institutionKey}/management", "school-02")
                        .header("Authorization", bearer(institutionAdminToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void parentCanViewOnlyOwnChildStudentScope() throws Exception {
        String adminToken = loginAndExtractAccessToken(BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_PASSWORD);
        UUID parentUserId = createUserAndExtractUserId(
                adminToken,
                "parent.scope",
                "ParentPassword!123",
                "Parent Scope"
        );

        assignRole(
                adminToken,
                parentUserId,
                "parent",
                studentScopePathJson("school-01", "student-01")
        );

        String parentToken = loginAndExtractAccessToken("parent.scope", "ParentPassword!123");

        mockMvc.perform(get("/api/v1/identity/access/students/{institutionKey}/{studentKey}/view", "school-01", "student-01")
                        .header("Authorization", bearer(parentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permission").value("VIEW_STUDENT"))
                .andExpect(jsonPath("$.effectiveRoles[0]").value("parent"));

        mockMvc.perform(get("/api/v1/identity/access/students/{institutionKey}/{studentKey}/view", "school-01", "student-02")
                        .header("Authorization", bearer(parentToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tenantBootstrapCreatesSingleActiveDeploymentTenant() {
        Integer tenantCount = jdbcTemplate.queryForObject("select count(*) from platform_tenant", Integer.class);

        assertThat(tenantCount).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select country_code from platform_tenant", String.class)).isEqualTo("PL");
        assertThat(jdbcTemplate.queryForObject("select display_name from platform_tenant", String.class))
                .isEqualTo("Poland Reference Deployment");
        assertThat(jdbcTemplate.queryForObject("select default_timezone from platform_tenant", String.class))
                .isEqualTo("Europe/Warsaw");
        assertThat(jdbcTemplate.queryForObject("select default_locale from platform_tenant", String.class))
                .isEqualTo("pl-PL");
    }

    @Test
    void rerunningTenantBootstrapRemainsIdempotent() throws Exception {
        tenantBootstrapRunner.run(null);

        Integer tenantCount = jdbcTemplate.queryForObject("select count(*) from platform_tenant", Integer.class);
        assertThat(tenantCount).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select country_code from platform_tenant", String.class)).isEqualTo("PL");
    }

    @Test
    void tenantContextReturnsActiveDeploymentTenant() {
        TenantContext activeTenant = tenantContextService.getActiveTenant();

        assertThat(activeTenant.countryCode()).isEqualTo("PL");
        assertThat(activeTenant.displayName()).isEqualTo("Poland Reference Deployment");
        assertThat(activeTenant.defaultTimezone()).isEqualTo("Europe/Warsaw");
        assertThat(activeTenant.defaultLocale()).isEqualTo("pl-PL");
    }

    @Test
    void translationsAreLoadedIntoCacheOnStartupWithConfiguredTtl() {
        assertThat(translationProperties.getCacheTtl()).isEqualTo(Duration.parse("PT2M"));
        assertThat(translationCache.contains(new TranslationCacheKey("ui.greeting", "default", "en"))).isTrue();
        assertThat(translationCache.contains(new TranslationCacheKey("ui.greeting", "default", "fr"))).isTrue();
        assertThat(translationCache.contains(new TranslationCacheKey("ui.status.ready", "default", "en"))).isTrue();
    }

    @Test
    void duplicateTranslationKeyLanguageAndNamespaceIsRejected() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                """
                insert into translation (id, translation_key, language_code, namespace, value, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)
                """,
                UUID.fromString("00000000-0000-0000-0000-000000000399"),
                "ui.greeting",
                "en",
                "default",
                "Duplicate",
                1
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void resolveEndpointReturnsRequestedLanguageWhenPresent() throws Exception {
        mockMvc.perform(get("/api/v1/translations/resolve")
                        .param("key", "ui.greeting")
                        .param("lang", "fr")
                        .param("namespace", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translationId").value(GREETING_FR_ID.toString()))
                .andExpect(jsonPath("$.resolvedLanguage").value("fr"))
                .andExpect(jsonPath("$.value").value("Bonjour"))
                .andExpect(jsonPath("$.fallbackApplied").value(false))
                .andExpect(jsonPath("$.cacheHit").value(true));
    }

    @Test
    void resolveEndpointFallsBackToDeploymentDefaultLanguage() throws Exception {
        mockMvc.perform(get("/api/v1/translations/resolve")
                        .param("key", "ui.greeting")
                        .param("lang", "de")
                        .param("namespace", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolvedLanguage").value("fr"))
                .andExpect(jsonPath("$.value").value("Bonjour"))
                .andExpect(jsonPath("$.fallbackApplied").value(true));
    }

    @Test
    void resolveEndpointFallsBackToGlobalLanguageWhenDefaultLanguageIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/translations/resolve")
                        .param("key", "ui.status.ready")
                        .param("lang", "de")
                        .param("namespace", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translationId").value(STATUS_READY_EN_ID.toString()))
                .andExpect(jsonPath("$.resolvedLanguage").value("en"))
                .andExpect(jsonPath("$.value").value("Ready"))
                .andExpect(jsonPath("$.fallbackApplied").value(true));
    }

    @Test
    void updateEndpointInvalidatesCachedEntryAndCreatesGenericAuditEvent() throws Exception {
        mockMvc.perform(put("/api/v1/translations/{translationId}", GREETING_EN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"Hi","actor":"system-admin"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translationId").value(GREETING_EN_ID.toString()))
                .andExpect(jsonPath("$.languageCode").value("en"))
                .andExpect(jsonPath("$.value").value("Hi"))
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(get("/api/v1/translations/resolve")
                        .param("key", "ui.greeting")
                        .param("lang", "en")
                        .param("namespace", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translationId").value(GREETING_EN_ID.toString()))
                .andExpect(jsonPath("$.resolvedLanguage").value("en"))
                .andExpect(jsonPath("$.value").value("Hi"))
                .andExpect(jsonPath("$.cacheHit").value(true));

        Integer auditCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from audit_event
                where entity_type = 'TRANSLATION'
                  and entity_id = ?
                  and action_type = 'UPDATE'
                  and actor = ?
                  and old_value_json ->> 'value' = ?
                  and new_value_json ->> 'value' = ?
                """,
                Integer.class,
                GREETING_EN_ID.toString(),
                "system-admin",
                "Hello",
                "Hi"
        );
        assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void auditEventsEndpointSupportsEntityActorAndTimeRangeFilters() throws Exception {
        mockMvc.perform(put("/api/v1/translations/{translationId}", GREETING_EN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"Hi","actor":"system-admin"}
                                """))
                .andExpect(status().isOk());

        OffsetDateTime from = OffsetDateTime.now().minusMinutes(1);
        OffsetDateTime to = OffsetDateTime.now().plusMinutes(1);

        mockMvc.perform(get("/api/v1/platform/audit/events")
                        .param("entityType", "translation")
                        .param("actor", "system-admin")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events[0].entityType").value("TRANSLATION"))
                .andExpect(jsonPath("$.events[0].entityId").value(GREETING_EN_ID.toString()))
                .andExpect(jsonPath("$.events[0].actionType").value("UPDATE"))
                .andExpect(jsonPath("$.events[0].actor").value("system-admin"))
                .andExpect(jsonPath("$.events[0].oldValue.value").value("Hello"))
                .andExpect(jsonPath("$.events[0].newValue.value").value("Hi"));
    }

    @Test
    void auditExportEndpointReturnsComplianceRelevantFields() throws Exception {
        mockMvc.perform(put("/api/v1/translations/{translationId}", GREETING_EN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"Hi","actor":"system-admin"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/platform/audit/events/export")
                        .param("entityType", "TRANSLATION")
                        .param("actor", "system-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exportedCount").value(1))
                .andExpect(jsonPath("$.events[0].id").isNotEmpty())
                .andExpect(jsonPath("$.events[0].tenantId").isNotEmpty())
                .andExpect(jsonPath("$.events[0].entityType").value("TRANSLATION"))
                .andExpect(jsonPath("$.events[0].entityId").value(GREETING_EN_ID.toString()))
                .andExpect(jsonPath("$.events[0].actionType").value("UPDATE"))
                .andExpect(jsonPath("$.events[0].actor").value("system-admin"))
                .andExpect(jsonPath("$.events[0].occurredAt").isNotEmpty())
                .andExpect(jsonPath("$.events[0].oldValue.value").value("Hello"))
                .andExpect(jsonPath("$.events[0].newValue.value").value("Hi"));
    }

    @Test
    void auditEndpointsDoNotAllowMutationOperations() throws Exception {
        mockMvc.perform(put("/api/v1/platform/audit/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(delete("/api/v1/platform/audit/events"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void countryLevelConfigurationResolvesAtInstitutionLevelWithoutOverride() throws Exception {
        putConfigurationFieldDefinition("grading.policy", "STRING", "REPLACE", true);
        putConfigurationValue(
                "grading.policy",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "\"national-default\"",
                false
        );

        mockMvc.perform(post("/api/v1/platform/configuration/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"grading.policy",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"REGION","scopeKey":"mazowieckie"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldKey").value("grading.policy"))
                .andExpect(jsonPath("$.effectiveValue").value("national-default"))
                .andExpect(jsonPath("$.sourceScopeType").value("COUNTRY"))
                .andExpect(jsonPath("$.sourceScopeKey").value("country"))
                .andExpect(jsonPath("$.inherited").value(true))
                .andExpect(jsonPath("$.merged").value(false));
    }

    @Test
    void institutionLevelOverrideTakesPrecedenceOverInheritedConfiguration() throws Exception {
        putConfigurationFieldDefinition("grading.policy", "STRING", "REPLACE", true);
        putConfigurationValue(
                "grading.policy",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "\"national-default\"",
                false
        );
        putConfigurationValue(
                "grading.policy",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"},
                  {"scopeType":"REGION","scopeKey":"mazowieckie"},
                  {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                ]
                """,
                "\"school-specific\"",
                false
        );

        mockMvc.perform(post("/api/v1/platform/configuration/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"grading.policy",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"REGION","scopeKey":"mazowieckie"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.effectiveValue").value("school-specific"))
                .andExpect(jsonPath("$.sourceScopeType").value("INSTITUTION"))
                .andExpect(jsonPath("$.sourceScopeKey").value("school-01"))
                .andExpect(jsonPath("$.inherited").value(false));
    }

    @Test
    void lockedCountryConfigurationRejectsLowerScopeOverride() throws Exception {
        putConfigurationFieldDefinition("grading.policy", "STRING", "REPLACE", true);
        putConfigurationValue(
                "grading.policy",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "\"national-default\"",
                true
        );

        mockMvc.perform(put("/api/v1/platform/configuration/values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"grading.policy",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ],
                                  "value":"school-specific",
                                  "locked":false
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(status().reason(containsString("locked by ancestor scope")));
    }

    @Test
    void validationEndpointReturnsConflictForLockedLowerScopeOverrideAttempt() throws Exception {
        putConfigurationFieldDefinition("grading.policy", "STRING", "REPLACE", true);
        putConfigurationValue(
                "grading.policy",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "\"national-default\"",
                true
        );

        mockMvc.perform(post("/api/v1/platform/configuration/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"grading.policy",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ],
                                  "value":"school-specific",
                                  "locked":false
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.fieldKey").value("grading.policy"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("BLOCKED_BY_ANCESTOR_LOCK"))
                .andExpect(jsonPath("$.message", containsString("locked by ancestor scope")))
                .andExpect(jsonPath("$.blockingScopes[0].scopeType").value("COUNTRY"));
    }

    @Test
    void inheritanceBreakRequestIsRecordedAndAudited() throws Exception {
        putConfigurationFieldDefinition("grading.policy", "STRING", "REPLACE", true);
        putConfigurationValue(
                "grading.policy",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "\"national-default\"",
                true
        );

        mockMvc.perform(post("/api/v1/platform/configuration/inheritance-break-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"grading.policy",
                                  "targetScopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ],
                                  "proposedValue":"school-specific",
                                  "justification":"School pilot requires a local grading exception",
                                  "requestedBy":"configuration-admin"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fieldKey").value("grading.policy"))
                .andExpect(jsonPath("$.blockingAncestorScopeType").value("COUNTRY"))
                .andExpect(jsonPath("$.blockingAncestorScopeKey").value("country"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.justification").value("School pilot requires a local grading exception"))
                .andExpect(jsonPath("$.requestedBy").value("configuration-admin"));

        Integer requestCount = jdbcTemplate.queryForObject(
                "select count(*) from configuration_inheritance_break_request where field_key = ? and requested_by = ?",
                Integer.class,
                "grading.policy",
                "configuration-admin"
        );
        assertThat(requestCount).isEqualTo(1);

        Integer auditCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from audit_event
                where entity_type = 'CONFIGURATION_INHERITANCE_BREAK_REQUEST'
                  and action_type = 'CREATE'
                  and actor = ?
                  and new_value_json ->> 'fieldKey' = ?
                """,
                Integer.class,
                "configuration-admin",
                "grading.policy"
        );
        assertThat(auditCount).isEqualTo(1);
    }

    @Test
    void compatibilityReportListsAffectedInstitutionOverridesForCountryChange() throws Exception {
        putConfigurationFieldDefinition("subjects.allowed", "STRING_SET", "EXTEND_SET", true);
        putConfigurationValue(
                "subjects.allowed",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "[\"math\",\"history\"]",
                false
        );
        putConfigurationValue(
                "subjects.allowed",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"},
                  {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                ]
                """,
                "[\"biology\"]",
                false
        );
        putConfigurationValue(
                "subjects.allowed",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"},
                  {"scopeType":"INSTITUTION","scopeKey":"school-02"}
                ]
                """,
                "[\"chemistry\"]",
                false
        );

        mockMvc.perform(post("/api/v1/platform/configuration/compatibility-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"subjects.allowed",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"}
                                  ],
                                  "proposedValue":["math","history","physics"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldKey").value("subjects.allowed"))
                .andExpect(jsonPath("$.impactCount").value(2))
                .andExpect(jsonPath("$.impacts[0].institutionScope.scopeKey").value("school-01"))
                .andExpect(jsonPath("$.impacts[0].impactLevel").value("WARNING"))
                .andExpect(jsonPath("$.impacts[0].reason", containsString("changes the effective institution configuration")))
                .andExpect(jsonPath("$.impacts[0].projectedEffectiveValue[2]").value("physics"))
                .andExpect(jsonPath("$.impacts[1].institutionScope.scopeKey").value("school-02"));
    }

    @Test
    void extensibleConfigurationMergesInheritedAndLocalOptions() throws Exception {
        putConfigurationFieldDefinition("subjects.allowed", "STRING_SET", "EXTEND_SET", true);
        putConfigurationValue(
                "subjects.allowed",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"}
                ]
                """,
                "[\"math\",\"history\"]",
                false
        );
        putConfigurationValue(
                "subjects.allowed",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"},
                  {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                ]
                """,
                "[\"biology\",\"math\"]",
                false
        );

        mockMvc.perform(post("/api/v1/platform/configuration/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"subjects.allowed",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.effectiveValue[0]").value("math"))
                .andExpect(jsonPath("$.effectiveValue[1]").value("history"))
                .andExpect(jsonPath("$.effectiveValue[2]").value("biology"))
                .andExpect(jsonPath("$.sourceScopeType").value("INSTITUTION"))
                .andExpect(jsonPath("$.merged").value(true))
                .andExpect(jsonPath("$.contributingScopes[0].scopeType").value("COUNTRY"))
                .andExpect(jsonPath("$.contributingScopes[1].scopeType").value("INSTITUTION"));
    }

    @Test
    void regionLevelConfigurationChangeFlowsToInstitutionWithinRegion() throws Exception {
        putConfigurationFieldDefinition("timezone.override", "STRING", "REPLACE", true);
        putConfigurationValue(
                "timezone.override",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"},
                  {"scopeType":"REGION","scopeKey":"mazowieckie"}
                ]
                """,
                "\"Europe/Warsaw\"",
                false
        );

        mockMvc.perform(post("/api/v1/platform/configuration/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"timezone.override",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"REGION","scopeKey":"mazowieckie"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.effectiveValue").value("Europe/Warsaw"))
                .andExpect(jsonPath("$.sourceScopeType").value("REGION"));

        putConfigurationValue(
                "timezone.override",
                """
                [
                  {"scopeType":"COUNTRY","scopeKey":"country"},
                  {"scopeType":"REGION","scopeKey":"mazowieckie"}
                ]
                """,
                "\"Europe/Warsaw-Updated\"",
                false
        );

        mockMvc.perform(post("/api/v1/platform/configuration/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"timezone.override",
                                  "scopePath":[
                                    {"scopeType":"COUNTRY","scopeKey":"country"},
                                    {"scopeType":"REGION","scopeKey":"mazowieckie"},
                                    {"scopeType":"INSTITUTION","scopeKey":"school-01"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.effectiveValue").value("Europe/Warsaw-Updated"))
                .andExpect(jsonPath("$.sourceScopeType").value("REGION"));
    }

    @Test
    void apiDocsEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void apiDocsContainsOpenApiVersion() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("openapi")));
    }

    @Test
    void apiDocsContainsPlatformStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/platform/status")));
    }

    @Test
    void apiDocsContainsTranslationResolveEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/translations/resolve")));
    }

    @Test
    void apiDocsContainsConfigurationResolveEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/platform/configuration/resolve")));
    }

    @Test
    void apiDocsContainsConfigurationValidationEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/platform/configuration/validate")))
                .andExpect(content().string(containsString("/api/v1/platform/configuration/inheritance-break-requests")))
                .andExpect(content().string(containsString("/api/v1/platform/configuration/compatibility-report")));
    }

    @Test
    void apiDocsContainsAuditEventsEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/platform/audit/events")));
    }

    @Test
    void apiDocsContainsTenantEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/platform/tenant")));
    }

    @Test
    void apiDocsContainsIdentityEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/v1/identity/auth/login")))
                .andExpect(content().string(containsString("/api/v1/identity/users")))
                .andExpect(content().string(containsString("/api/v1/identity/me")))
                .andExpect(content().string(containsString("/api/v1/identity/role-assignments")))
                .andExpect(content().string(containsString("/api/v1/identity/users/{userId}/role-assignments")))
                .andExpect(content().string(containsString("/api/v1/identity/access/institutions/{institutionKey}/teaching-view")))
                .andExpect(content().string(containsString("/api/v1/identity/access/institutions/{institutionKey}/management")))
                .andExpect(content().string(containsString("/api/v1/identity/access/students/{institutionKey}/{studentKey}/view")));
    }

    @Test
    void tenantEndpointReturnsActiveDeploymentTenantMetadata() throws Exception {
        mockMvc.perform(get("/api/v1/platform/tenant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryCode").value("PL"))
                .andExpect(jsonPath("$.displayName").value("Poland Reference Deployment"))
                .andExpect(jsonPath("$.timezone").value("Europe/Warsaw"))
                .andExpect(jsonPath("$.locale").value("pl-PL"));
    }

    @Test
    void swaggerUiIsReachable() throws Exception {
        // Springdoc redirects /swagger-ui to the concrete Swagger UI page — accept 2xx or 3xx
        mockMvc.perform(get("/swagger-ui"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status < 200 || status >= 400) {
                        throw new AssertionError("Expected 2xx or 3xx but got: " + status);
                    }
                });
    }

    private void resetAuditData() {
        jdbcTemplate.update("delete from audit_event");
    }

    private void resetConfigurationData() {
        jdbcTemplate.update("delete from configuration_inheritance_break_request");
        jdbcTemplate.update("delete from configuration_value");
        jdbcTemplate.update("delete from configuration_field_definition");
    }

    private void resetTranslationData() {
        jdbcTemplate.update("delete from translation_audit");
        jdbcTemplate.update("delete from translation");

        insertTranslation(GREETING_EN_ID, "ui.greeting", "en", "default", "Hello");
        insertTranslation(GREETING_FR_ID, "ui.greeting", "fr", "default", "Bonjour");
        insertTranslation(STATUS_READY_EN_ID, "ui.status.ready", "en", "default", "Ready");

        translationCacheWarmup.warmAllTranslations();
    }

    private void resetIdentityData() {
        jdbcTemplate.update("delete from identity_role_assignment");
        jdbcTemplate.update("delete from identity_user");
        identityBootstrapService.ensureBootstrapAdmin();
    }

    private UUID createUserAndExtractUserId(String adminToken, String username, String password, String displayName) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/identity/users")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"%s",
                                  "initialPassword":"%s",
                                  "displayName":"%s",
                                  "status":"ACTIVE"
                                }
                                """.formatted(username, password, displayName)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(responseBody).path("userId").asText());
    }

    private void assignRole(String adminToken, UUID userId, String roleCode, String scopePathJson) throws Exception {
        mockMvc.perform(post("/api/v1/identity/role-assignments")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId":"%s",
                                  "roleCode":"%s",
                                  "scopePath":%s
                                }
                                """.formatted(userId, roleCode, scopePathJson)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.roleCode").value(roleCode));
    }

    private String institutionScopePathJson(String institutionKey) {
        return """
                [
                  {"scopeType":"TENANT","scopeKey":"%s"},
                  {"scopeType":"INSTITUTION","scopeKey":"%s"}
                ]
                """.formatted(tenantContextService.getActiveTenant().tenantId(), institutionKey);
    }

    private String studentScopePathJson(String institutionKey, String studentKey) {
        return """
                [
                  {"scopeType":"TENANT","scopeKey":"%s"},
                  {"scopeType":"INSTITUTION","scopeKey":"%s"},
                  {"scopeType":"STUDENT","scopeKey":"%s"}
                ]
                """.formatted(tenantContextService.getActiveTenant().tenantId(), institutionKey, studentKey);
    }

    private void putConfigurationFieldDefinition(String fieldKey, String valueType, String mergeStrategy, boolean overridesAllowed) throws Exception {
        mockMvc.perform(put("/api/v1/platform/configuration/fields/{fieldKey}", fieldKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "valueType":"%s",
                                  "mergeStrategy":"%s",
                                  "overridesAllowed":%s
                                }
                                """.formatted(valueType, mergeStrategy, overridesAllowed)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldKey").value(fieldKey));
    }

    private void putConfigurationValue(String fieldKey, String scopePathJson, String valueJson, boolean locked) throws Exception {
        mockMvc.perform(put("/api/v1/platform/configuration/values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fieldKey":"%s",
                                  "scopePath":%s,
                                  "value":%s,
                                  "locked":%s
                                }
                                """.formatted(fieldKey, scopePathJson, valueJson, locked)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fieldKey").value(fieldKey));
    }

    private void insertTranslation(UUID id, String translationKey, String languageCode, String namespace, String value) {
        jdbcTemplate.update(
                """
                insert into translation (id, translation_key, language_code, namespace, value, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                translationKey,
                languageCode,
                namespace,
                value,
                1,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    private String loginAndExtractAccessToken(String username, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/identity/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).path("accessToken").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
