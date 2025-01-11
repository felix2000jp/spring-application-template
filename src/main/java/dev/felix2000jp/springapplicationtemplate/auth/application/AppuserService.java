package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDTO;

public interface AppuserService {

    AppuserListDTO getAll(int pageNumber);

    AppuserDTO getAuthenticated();

    AppuserDTO updateAuthenticated(UpdateAppuserDTO updateAppuserDTO);

    AppuserDTO deleteAuthenticated();

}
