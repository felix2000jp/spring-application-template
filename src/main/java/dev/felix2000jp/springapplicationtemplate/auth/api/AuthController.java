package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AuthService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/auth")
class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/csrf")
    ResponseEntity<CsrfToken> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(csrfToken);
    }

    @PostMapping("/login")
    ResponseEntity<String> generateToken() {
        var body = authService.generateToken();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    ResponseEntity<Void> createAppuser(@Valid @RequestBody CreateAppuserDto createAppuserDto) {
        authService.createAppuser(createAppuserDto);
        var location = URI.create("/api/appusers/me");
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/password")
    ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        authService.updatePassword(updatePasswordDto);
        return ResponseEntity.noContent().build();
    }

}
