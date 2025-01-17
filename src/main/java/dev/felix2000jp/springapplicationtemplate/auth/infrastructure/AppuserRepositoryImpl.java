package dev.felix2000jp.springapplicationtemplate.auth.infrastructure;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class AppuserRepositoryImpl implements AppuserRepository {

    private static final int PAGE_SIZE = 50;

    private final AppuserJpaRepository appuserJpaRepository;

    AppuserRepositoryImpl(AppuserJpaRepository appuserJpaRepository) {
        this.appuserJpaRepository = appuserJpaRepository;
    }

    @Override
    public List<Appuser> findAll(int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = appuserJpaRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public Optional<Appuser> findById(UUID id) {
        return appuserJpaRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return appuserJpaRepository.existsById(id);
    }

    @Override
    public Optional<Appuser> findByUsername(String username) {
        return appuserJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return appuserJpaRepository.existsByUsername(username);
    }

    @Override
    public void deleteById(UUID id) {
        appuserJpaRepository.deleteById(id);
    }

    @Override
    public void save(Appuser appuser) {
        appuserJpaRepository.save(appuser);
    }

}
