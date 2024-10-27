package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppuserRepository extends JpaRepository<Appuser, UUID> {

    Optional<Appuser> findByUsername(String username);

    boolean existsByUsername(String username);

}