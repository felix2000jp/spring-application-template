package dev.felix2000jp.springapplicationtemplate.auth.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppuserRepository {

    List<Appuser> findAll(int pageNumber);

    Optional<Appuser> findById(UUID id);

    boolean existsById(UUID id);

    Optional<Appuser> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteById(UUID id);

    void save(Appuser appuser);

}
