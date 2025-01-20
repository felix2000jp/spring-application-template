package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppuserMapperTest {

    private final AppuserMapper appuserMapper = new AppuserMapper();

    @Test
    void toDto_given_appuser_then_map_to_dto() {
        var appuser = new Appuser("username", "password");
        appuser.addScopeApplication();

        var actual = appuserMapper.toDto(appuser);

        assertThat(actual.id()).isEqualTo(appuser.getId());
        assertThat(actual.username()).isEqualTo(appuser.getUsername());
        assertThat(actual.scopes()).isEqualTo(appuser.getAuthoritiesScopes());
    }

    @Test
    void toDto_given_list_of_appusers_then_map_to_list_dto() {
        var appusers = List.of(new Appuser("username", "password"));

        var actual = appuserMapper.toDto(appusers);

        assertThat(actual.appusers()).hasSameSizeAs(appusers);
    }

}
