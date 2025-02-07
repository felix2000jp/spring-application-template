package dev.felix2000jp.springapplicationtemplate.auth.domain;

import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityScope;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppuserTest {

    @Test
    void constructor_given_valid_parameters_then_create_appuser() {
        var appuser = new Appuser("username", "password");

        assertThat(appuser.getUsername()).isEqualTo("username");
        assertThat(appuser.getPassword()).isEqualTo("password");
        assertThat(appuser.getAuthorities()).isEmpty();
    }

    @Test
    void setUsername_given_valid_username_then_set_username() {
        var appuser = new Appuser("username", "password");
        var newUsername = "new-username";

        appuser.setUsername(newUsername);

        assertThat(appuser.getUsername()).isEqualTo(newUsername);
    }

    @Test
    void setPassword_given_valid_password_then_set_password() {
        var appuser = new Appuser("username", "password");
        var newPassword = "new-password";

        appuser.setPassword(newPassword);

        assertThat(appuser.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void addScopeAdmin_given_appuser_then_add_admin_scope() {
        var appuser = new Appuser("username", "password");
        appuser.addScopeAdmin();

        assertThat(appuser.getAuthorities()).isNotEmpty();
        assertThat(appuser.getAuthoritiesScopes()).containsExactly(SecurityScope.ADMIN.name());
    }

    @Test
    void addScopeApplication_given_appuser_then_add_application_scope() {
        var appuser = new Appuser("username", "password");
        appuser.addScopeApplication();

        assertThat(appuser.getAuthorities()).isNotEmpty();
        assertThat(appuser.getAuthoritiesScopes()).containsExactly(SecurityScope.APPLICATION.name());
    }

}
