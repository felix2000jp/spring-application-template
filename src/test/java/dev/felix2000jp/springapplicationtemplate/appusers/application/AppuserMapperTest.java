package dev.felix2000jp.springapplicationtemplate.appusers.application;

import dev.felix2000jp.springapplicationtemplate.appusers.domain.Appuser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppuserMapperTest {

    private final AppuserMapper appuserMapper = new AppuserMapper();

    @Test
    void should_map_appuser_to_appuserDTO_successfully() {
        var appuser = new Appuser("username", "password");
        appuser.addApplicationScope();

        var actual = appuserMapper.toDTO(appuser);

        assertEquals(appuser.getId(), actual.id());
        assertEquals(appuser.getUsername(), actual.username());
        assertEquals("APPLICATION", actual.authorities().iterator().next());
    }

    @Test
    void should_map_appusers_to_appuserListDTO_successfully() {
        var appusers = List.of(new Appuser("username", "password"));

        var actual = appuserMapper.toDTO(appusers);

        assertEquals(appusers.size(), actual.appusers().size());
    }

}
