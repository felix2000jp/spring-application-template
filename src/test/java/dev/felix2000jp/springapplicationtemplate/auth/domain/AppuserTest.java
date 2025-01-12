package dev.felix2000jp.springapplicationtemplate.auth.domain;

import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppuserTest {

    @Test
    void should_create_new_appuser_successfully() {
        var appuser = new Appuser("username", "password");

        assertEquals("username", appuser.getUsername());
        assertEquals("password", appuser.getPassword());
        assertEquals(Set.of(), appuser.getAuthorities());
    }

    @Test
    void should_set_username_successfully() {
        var appuser = new Appuser("username", "password");
        appuser.setUsername("new username");

        assertEquals("new username", appuser.getUsername());
    }

    @Test
    void should_set_password_successfully() {
        var appuser = new Appuser("username", "password");
        appuser.setPassword("new password");

        assertEquals("new password", appuser.getPassword());
    }

    @Test
    void should_add_scope_admin_successfully() {
        var appuser = new Appuser("username", "password");
        appuser.addScopeAdmin();

        assertEquals(1, appuser.getAuthorities().size());
        assertEquals(SecurityService.Scope.ADMIN.name(), appuser.getAuthoritiesScopes().iterator().next());
        assertEquals(SecurityService.Scope.ADMIN.toAuthority(), appuser.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void should_add_scope_application_successfully() {
        var appuser = new Appuser("username", "password");
        appuser.addScopeApplication();

        assertEquals(1, appuser.getAuthorities().size());
        assertEquals(SecurityService.Scope.APPLICATION.name(), appuser.getAuthoritiesScopes().iterator().next());
        assertEquals(SecurityService.Scope.APPLICATION.toAuthority(), appuser.getAuthorities().iterator().next().getAuthority());
    }

}
