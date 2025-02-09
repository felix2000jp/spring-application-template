package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AuthService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    ResponseEntity<String> login() {
        var body = authService.login();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<Void> register(@Valid @RequestBody CreateAppuserDto createAppuserDto) {
        authService.register(createAppuserDto);
        var location = URI.create("/api/appusers/me");
        return ResponseEntity.created(location).build();
    }

}
