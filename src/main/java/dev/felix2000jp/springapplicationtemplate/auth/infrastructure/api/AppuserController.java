package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/appusers")
class AppuserController {

    private final AppuserService appuserService;

    AppuserController(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    @GetMapping("/admin")
    ResponseEntity<AppuserListDto> getAppusers(@RequestParam(defaultValue = "0") @Min(0) int page) {
        var body = appuserService.getAppusers(page);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    ResponseEntity<AppuserDto> getAppuserForCurrentUser() {
        var body = appuserService.getAppuserForCurrentUser();
        return ResponseEntity.ok(body);
    }

}
