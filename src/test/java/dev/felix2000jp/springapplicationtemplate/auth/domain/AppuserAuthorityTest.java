package dev.felix2000jp.springapplicationtemplate.auth.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppuserAuthorityTest {

    @Test
    void should_create_new_appuserAuthority_successfully() {
        var authority = new AppuserAuthority("APPLICATION");

        assertEquals("APPLICATION", authority.getScopeValue());
        assertEquals("SCOPE_APPLICATION", authority.getAuthority());
    }

}
