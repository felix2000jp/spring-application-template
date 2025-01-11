package dev.felix2000jp.springapplicationtemplate.auth.domain;

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
    void should_add_application_scope_successfully() {
        var appuser = new Appuser("username", "password");
        appuser.addApplicationScope();

        assertEquals(1, appuser.getAuthorities().size());
        assertEquals("APPLICATION", appuser.getAuthoritiesScopeValues().iterator().next());
        assertEquals("SCOPE_APPLICATION", appuser.getAuthorities().iterator().next().getAuthority());
    }

}
