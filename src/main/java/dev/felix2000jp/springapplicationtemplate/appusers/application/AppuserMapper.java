package dev.felix2000jp.springapplicationtemplate.appusers.application;

import dev.felix2000jp.springapplicationtemplate.appusers.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.appusers.domain.AppuserAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
class AppuserMapper {

    AppuserDTO toDTO(Appuser appuser) {
        return new AppuserDTO(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthorities().stream().map(AppuserAuthority::getScopeValue).collect(Collectors.toSet())
        );
    }

    AppuserListDTO toDTO(List<Appuser> appusers) {
        return new AppuserListDTO(appusers.stream().map(this::toDTO).toList());
    }

}
