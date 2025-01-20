package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.database;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<Appuser> findAll(int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = appuserJpaRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appuser> findById(UUID id) {
        return appuserJpaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return appuserJpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appuser> findByUsername(String username) {
        return appuserJpaRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return appuserJpaRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        appuserJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(Appuser appuser) {
        appuserJpaRepository.save(appuser);
    }

}
