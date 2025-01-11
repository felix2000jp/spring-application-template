package dev.felix2000jp.springapplicationtemplate.auth.infrastructure;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class AppuserRepositoryImpl implements AppuserRepository {

    private static final int PAGE_SIZE = 50;

    private final AppuserJpaRepository appuserJpaRepository;

    AppuserRepositoryImpl(AppuserJpaRepository appuserJpaRepository) {
        this.appuserJpaRepository = appuserJpaRepository;
    }

    @Override
    public List<Appuser> getAll(int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = appuserJpaRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public Appuser getById(UUID id) {
        var optionalAppuser = appuserJpaRepository.findById(id);
        return optionalAppuser.orElse(null);
    }

    @Override
    public boolean existsById(UUID id) {
        return appuserJpaRepository.existsById(id);
    }

    @Override
    public Appuser getByUsername(String username) {
        var optionalAppuser = appuserJpaRepository.findByUsername(username);
        return optionalAppuser.orElse(null);
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
