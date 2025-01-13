package dev.felix2000jp.springapplicationtemplate.auth.domain;

import java.util.List;
import java.util.UUID;

public interface AppuserRepository {

    List<Appuser> findAll(int pageNumber);

    Appuser findById(UUID id);

    boolean existsById(UUID id);

    Appuser findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteById(UUID id);

    void save(Appuser appuser);

}
