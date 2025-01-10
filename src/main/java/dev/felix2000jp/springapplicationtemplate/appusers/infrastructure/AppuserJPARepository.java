package dev.felix2000jp.springapplicationtemplate.appusers.infrastructure;

import dev.felix2000jp.springapplicationtemplate.appusers.domain.Appuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface AppuserJPARepository extends JpaRepository<Appuser, UUID> {

    Optional<Appuser> findByUsername(String username);

    boolean existsByUsername(String username);

}