package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.app;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping("/app")
class AuthViewController {

    @GetMapping
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping
    public String registerPage() {
        return "index";
    }

}
