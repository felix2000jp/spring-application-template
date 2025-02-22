package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.app;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping("/app")
class AppusersViewController {

    private final AppuserService appuserService;

    AppusersViewController(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    @GetMapping
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/appusers/register")
    public ResponseEntity<String> register(@Valid @ModelAttribute CreateAppuserDto createAppuserDto) {
        try {
            appuserService.register(createAppuserDto);
            return ResponseEntity.ok().header("Hx-Redirect", "/app/login").build();
        } catch (RuntimeException ex) {
            return ResponseEntity.ok("Ups... " + ex.getMessage());
        }
    }

}
