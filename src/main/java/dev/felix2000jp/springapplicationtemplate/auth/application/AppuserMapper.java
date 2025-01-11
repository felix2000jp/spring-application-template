package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class AppuserMapper {

    AppuserDTO toDTO(Appuser appuser) {
        return new AppuserDTO(appuser.getId(), appuser.getUsername(), appuser.getAuthoritiesScopes());
    }

    AppuserListDTO toDTO(List<Appuser> appusers) {
        return new AppuserListDTO(appusers.stream().map(this::toDTO).toList());
    }

}
