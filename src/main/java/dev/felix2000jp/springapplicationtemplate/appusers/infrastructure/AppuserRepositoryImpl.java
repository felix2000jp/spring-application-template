package dev.felix2000jp.springapplicationtemplate.appusers.infrastructure;

import dev.felix2000jp.springapplicationtemplate.appusers.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.appusers.domain.AppuserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class AppuserRepositoryImpl implements AppuserRepository {

    private static final int PAGE_SIZE = 50;

    private final AppuserJPARepository appuserJPARepository;

    AppuserRepositoryImpl(AppuserJPARepository appuserJPARepository) {
        this.appuserJPARepository = appuserJPARepository;
    }

    @Override
    public List<Appuser> getAll(int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = appuserJPARepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public Appuser getById(UUID id) {
        var optionalAppuser = appuserJPARepository.findById(id);
        return optionalAppuser.orElse(null);
    }

    @Override
    public boolean existsById(UUID id) {
        return appuserJPARepository.existsById(id);
    }

    @Override
    public Appuser getByUsername(String username) {
        var optionalAppuser = appuserJPARepository.findByUsername(username);
        return optionalAppuser.orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return appuserJPARepository.existsByUsername(username);
    }

    @Override
    public void deleteById(UUID id) {
        appuserJPARepository.deleteById(id);
    }

    @Override
    public void save(Appuser appuser) {
        appuserJPARepository.save(appuser);
    }

}
