package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;

public interface AppuserService {

    AppuserListDto getAppusers(int pageNumber);

    AppuserDto getAppuserForCurrentUser();

    AppuserDto updateAppuserForCurrentUser(UpdateAppuserDto updateAppuserDto);

    AppuserDto deleteAppuserForCurrentUser();

}
