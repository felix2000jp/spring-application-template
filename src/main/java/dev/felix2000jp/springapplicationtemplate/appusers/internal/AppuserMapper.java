package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AuthenticatedAppuserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface AppuserMapper {

    AppuserDTO toDto(Appuser appuser);
    AuthenticatedAppuserDTO toAuthenticatedDto(Appuser appuser);

    default String toAuthorityValue(AppuserAuthority authority) {
        return authority.getScopeValue();
    }

}
