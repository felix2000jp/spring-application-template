package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AuthenticatedAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserBadRequestException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserConflictException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppuserService implements UserDetailsService {

    private final AppuserMapper appuserMapper;
    private final AppuserRepository appuserRepository;
    private final ApplicationEventPublisher events;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    private static final String ID_CLAIM_NAME = "id";
    private static final String SCOPE_CLAIM_NAME = "scope";

    AppuserService(
            AppuserMapper appuserMapper,
            AppuserRepository appuserRepository,
            ApplicationEventPublisher events,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {
        this.appuserMapper = appuserMapper;
        this.appuserRepository = appuserRepository;
        this.events = events;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public Appuser loadUserByUsername(String username) {
        return appuserRepository
                .findByUsername(username)
                .orElseThrow(AppuserNotFoundException::new);
    }

    public AuthenticatedAppuserDTO getAuthenticatedAppuserDTO() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        if (principal instanceof Appuser appuser) {
            return appuserMapper.toAuthenticatedDTO(appuser);
        }

        if (principal instanceof Jwt jwt) {
            return new AuthenticatedAppuserDTO(
                    UUID.fromString(jwt.getClaimAsString(ID_CLAIM_NAME)),
                    jwt.getSubject(),
                    Arrays.stream(jwt.getClaimAsString(SCOPE_CLAIM_NAME).split(" ")).collect(Collectors.toSet())
            );
        }

        throw new AppuserBadRequestException("Invalid authentication format");
    }

    public AuthenticatedAppuserDTO verifyAuthenticatedAppuserDTO() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        if (principal instanceof Appuser appuser) {
            return appuserMapper.toAuthenticatedDTO(appuser);
        }

        if (principal instanceof Jwt jwt) {
            var id = UUID.fromString(jwt.getClaimAsString(ID_CLAIM_NAME));

            var appuser = appuserRepository
                    .findById(id)
                    .orElseThrow(() -> new AppuserBadRequestException("Token is not valid"));

            var username = jwt.getSubject();
            var scope = Arrays.stream(jwt.getClaimAsString(SCOPE_CLAIM_NAME).split(" ")).collect(Collectors.toSet());

            if (!appuser.getUsername().equals(username) || !appuser.getAuthoritiesScopeValues().equals(scope)) {
                throw new AppuserBadRequestException("Token is not valid");
            }

            return new AuthenticatedAppuserDTO(id, username, scope);
        }

        throw new AppuserBadRequestException("Invalid authentication format");
    }

    AppuserDTO find() {
        var appuserDTO = getAuthenticatedAppuserDTO();

        var appuser = appuserRepository
                .findById(appuserDTO.id())
                .orElseThrow(AppuserNotFoundException::new);

        return appuserMapper.toDTO(appuser);
    }

    AppuserDTO create(CreateAppuserDTO createAppuserDTO) {
        var doesUsernameExist = appuserRepository.existsByUsername(createAppuserDTO.username());

        if (doesUsernameExist) {
            throw new AppuserConflictException();
        }

        var appuserCreated = new Appuser(
                createAppuserDTO.username(),
                passwordEncoder.encode(createAppuserDTO.password())
        );
        appuserCreated.addApplicationAuthority();

        var appuserSaved = appuserRepository.save(appuserCreated);
        return appuserMapper.toDTO(appuserSaved);
    }

    AppuserDTO update(UpdateAppuserDTO updateAppuserDTO) {
        var appuserDTO = verifyAuthenticatedAppuserDTO();

        var appuserToUpdate = appuserRepository
                .findById(appuserDTO.id())
                .orElseThrow(AppuserNotFoundException::new);

        var newUsername = updateAppuserDTO.username();
        var oldUsername = appuserToUpdate.getUsername();

        var isUsernameNew = !newUsername.equals(oldUsername);
        var doesUsernameExist = appuserRepository.existsByUsername(newUsername);

        if (isUsernameNew && doesUsernameExist) {
            throw new AppuserConflictException();
        }

        appuserToUpdate.updateCredentials(
                updateAppuserDTO.username(),
                passwordEncoder.encode(updateAppuserDTO.password())
        );
        var appuserSaved = appuserRepository.save(appuserToUpdate);
        return appuserMapper.toDTO(appuserSaved);
    }

    AppuserDTO delete() {
        var appuserDTO = verifyAuthenticatedAppuserDTO();

        var userToDelete = appuserRepository
                .findById(appuserDTO.id())
                .orElseThrow(AppuserNotFoundException::new);

        appuserRepository.delete(userToDelete);

        var appuserDeletedEvent = new AppuserDeletedEvent(userToDelete.getId());
        events.publishEvent(appuserDeletedEvent);

        return appuserMapper.toDTO(userToDelete);
    }

    String generateToken() {
        var appuserDTO = getAuthenticatedAppuserDTO();

        var now = Instant.now();
        var expiration = now.plus(12, ChronoUnit.HOURS);

        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(appuserDTO.username())
                .claim(ID_CLAIM_NAME, appuserDTO.id().toString())
                .claim(SCOPE_CLAIM_NAME, String.join(" ", appuserDTO.authorities()))
                .issuedAt(now)
                .expiresAt(expiration)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
