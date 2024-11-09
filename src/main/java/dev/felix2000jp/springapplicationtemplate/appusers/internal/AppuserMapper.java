package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface AppuserMapper {

    AppuserDTO toDTO(Appuser appuser);

    default String toAuthorityValue(AppuserAuthority authority) {
        return authority.getScopeValue();
    }

}
