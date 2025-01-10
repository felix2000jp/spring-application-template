package dev.felix2000jp.springapplicationtemplate.appusers.domain;

import java.util.List;
import java.util.UUID;

public interface AppuserRepository {

    List<Appuser> getAll(int pageNumber);

    Appuser getById(UUID id);

    boolean existsById(UUID id);

    Appuser getByUsername(String username);

    boolean existsByUsername(String username);

    void deleteById(UUID id);

    void save(Appuser appuser);

}
