package com.darkfactory.education.platform.configuration;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScopePathTest {

    @Test
    void prependsCountryScopeWhenCallerProvidesOnlyLowerScopes() {
        ScopePath scopePath = ScopePath.fromRequests(List.of(
                new ConfigurationScopeRequest("REGION", "mazowieckie"),
                new ConfigurationScopeRequest("INSTITUTION", "school-01")
        ));

        assertThat(scopePath.scopes())
                .extracting(ConfigurationScope::scopeType)
                .containsExactly(
                        ConfigurationScopeType.COUNTRY,
                        ConfigurationScopeType.REGION,
                        ConfigurationScopeType.INSTITUTION
                );
        assertThat(scopePath.scopes().get(0).scopeKey()).isEqualTo(ConfigurationScope.COUNTRY_SCOPE_KEY);
    }

    @Test
    void rejectsOutOfOrderScopePath() {
        assertThatThrownBy(() -> ScopePath.fromRequests(List.of(
                new ConfigurationScopeRequest("INSTITUTION", "school-01"),
                new ConfigurationScopeRequest("REGION", "mazowieckie")
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ordered");
    }

    @Test
    void rejectsNonReservedCountryScopeKey() {
        assertThatThrownBy(() -> ScopePath.fromRequests(List.of(
                new ConfigurationScopeRequest("COUNTRY", "PL")
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reserved key 'country'");
    }
}

