package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDTO;

import java.util.UUID;

public interface AppuserService {

    AppuserListDTO getAll(int pageNumber);

    AppuserDTO getById(UUID id);

    AppuserDTO create(CreateAppuserDTO createAppuserDTO);

    AppuserDTO updateById(UUID id, UpdateAppuserDTO updateAppuserDTO);

    AppuserDTO deleteById(UUID id);

    String createToken();

}
