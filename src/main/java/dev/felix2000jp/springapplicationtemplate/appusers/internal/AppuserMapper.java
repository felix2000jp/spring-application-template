package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface AppuserMapper {

    AppuserDto toDto(Appuser appuser);

    default AuthorityValue toAuthorityValue(AppuserAuthority authority) {
        return authority.getAuthorityValue();
    }

}
