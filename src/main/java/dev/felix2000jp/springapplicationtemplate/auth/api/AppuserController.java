package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/appusers")
class AppuserController {

    private final AppuserService appuserService;

    AppuserController(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    @GetMapping("/admin")
    ResponseEntity<AppuserListDTO> getAll(@RequestParam @Min(0) int page) {
        var body = appuserService.getAll(page);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    ResponseEntity<AppuserDTO> getCurrent() {
        var body = appuserService.getAuthenticated();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/me")
    ResponseEntity<Void> updateCurrent(@RequestBody @Valid UpdateAppuserDTO updateAppuserDTO) {
        appuserService.updateAuthenticated(updateAppuserDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    ResponseEntity<Void> deleteCurrent() {
        appuserService.deleteAuthenticated();
        return ResponseEntity.noContent().build();
    }
}
