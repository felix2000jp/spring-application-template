package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.SecurityScope;
import dev.felix2000jp.springapplicationtemplate.auth.domain.SecurityUser;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
@NamedInterface
public class SecurityService {

    private static final String ID_CLAIM_NAME = "id";
    private static final String SCOPE_CLAIM_NAME = "scope";

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    SecurityService(JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    public SecurityUser getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        if (principal instanceof Appuser appuser) {
            return appuser.toSecurityUser();
        }

        if (principal instanceof Jwt jwt) {
            var id = UUID.fromString(jwt.getClaimAsString(ID_CLAIM_NAME));
            var scopes = Arrays
                    .stream(jwt.getClaimAsString(SCOPE_CLAIM_NAME).split(" "))
                    .map(SecurityScope::valueOf)
                    .collect(Collectors.toSet());

            return new SecurityUser(id, jwt.getSubject(), scopes);
        }

        throw new AccessDeniedException("User authentication is not valid");
    }

    public String generateToken(String subject, String idClaimValue, String scopeClaimValue) {
        var now = Instant.now();
        var expiration = now.plus(12, ChronoUnit.HOURS);

        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(subject)
                .claim(ID_CLAIM_NAME, idClaimValue)
                .claim(SCOPE_CLAIM_NAME, scopeClaimValue)
                .issuedAt(now)
                .expiresAt(expiration)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
