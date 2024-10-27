package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/app/appusers")
public class AppuserController {

    private final AppuserService appuserService;
    private final JwtEncoder jwtEncoder;

    public AppuserController(AppuserService appuserService, JwtEncoder jwtEncoder) {
        this.appuserService = appuserService;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping
    ResponseEntity<AppuserDto> create(@Valid @RequestBody CreateAppuserDto createAppuserDto) {
        var body = appuserService.create(createAppuserDto);
        var location = URI.create("/app/appusers");
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping
    ResponseEntity<AppuserDto> find(Authentication authentication) {
        var principal = (Appuser) authentication.getPrincipal();
        var body = appuserService.find(principal);
        return ResponseEntity.ok(body);
    }

    @PutMapping
    ResponseEntity<Void> update(Authentication authentication, @Valid @RequestBody UpdateAppuserDto updateAppuserDto) {
        var principal = (Appuser) authentication.getPrincipal();
        appuserService.update(principal, updateAppuserDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    ResponseEntity<Void> delete(Authentication authentication) {
        var principal = (Appuser) authentication.getPrincipal();
        appuserService.delete(principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    ResponseEntity<String> generateToken(Authentication authentication) {
        var principal = (Appuser) authentication.getPrincipal();

        var id = principal.getId().toString();
        var username = principal.getUsername();
        var authorities = principal.getAuthorities().stream().map(AppuserAuthority::getAuthority).toList();
        var now = Instant.now();
        var expiration = now.plus(12, ChronoUnit.HOURS);

        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(id)
                .claim("username", username)
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiresAt(expiration)
                .build();

        var token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(token);
    }

}
