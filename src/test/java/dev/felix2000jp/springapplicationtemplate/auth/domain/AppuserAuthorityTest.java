package dev.felix2000jp.springapplicationtemplate.auth.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppuserAuthorityTest {

    @Test
    void constructor_given_valid_parameters_then_create_appuser_authority() {
        var authority = new AppuserAuthority("APPLICATION");

        assertThat(authority.getScope()).isEqualTo("APPLICATION");
        assertThat(authority.getAuthority()).isEqualTo("SCOPE_APPLICATION");
    }

}
