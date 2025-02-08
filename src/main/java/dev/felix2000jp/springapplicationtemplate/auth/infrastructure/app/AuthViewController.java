package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class AuthViewController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

}
