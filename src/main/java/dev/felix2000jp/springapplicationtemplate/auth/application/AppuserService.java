package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;

public interface AppuserService {

    AppuserListDto getAll(int pageNumber);

    AppuserDto getByCurrentUser();

    AppuserDto updateByCurrentUser(UpdateAppuserDto updateAppuserDTO);

    AppuserDto deleteByCurrentUser();

}
