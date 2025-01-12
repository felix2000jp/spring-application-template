package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class AppuserMapper {

    AppuserDto toDTO(Appuser appuser) {
        return new AppuserDto(appuser.getId(), appuser.getUsername(), appuser.getAuthoritiesScopes());
    }

    AppuserListDto toDTO(List<Appuser> appusers) {
        return new AppuserListDto(appusers.stream().map(this::toDTO).toList());
    }

}
