package dev.felix2000jp.springapplicationtemplate.shared.security;

import dev.felix2000jp.springapplicationtemplate.shared.AuthenticatedUser;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
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
class SecurityServiceImpl implements SecurityService {

    private static final String ID_CLAIM_NAME = "id";
    private static final String SCOPE_CLAIM_NAME = "scope";

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    SecurityServiceImpl(final JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
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

    @Override
    public String generateEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return new AuthenticatedUser(
                    UUID.fromString(jwt.getClaimAsString(ID_CLAIM_NAME)),
                    jwt.getSubject(),
                    Arrays.stream(jwt.getClaimAsString(SCOPE_CLAIM_NAME).split(" ")).collect(Collectors.toSet())
            );
        }

        throw new InvalidAuthenticationTypeException();
    }

}
