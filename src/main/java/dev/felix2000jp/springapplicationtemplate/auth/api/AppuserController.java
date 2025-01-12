package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
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
    ResponseEntity<AppuserListDto> getAll(@RequestParam(defaultValue = "0") @Min(0) int page) {
        var body = appuserService.getAll(page);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    ResponseEntity<AppuserDto> getCurrent() {
        var body = appuserService.getCurrent();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/me")
    ResponseEntity<Void> updateCurrent(@RequestBody @Valid UpdateAppuserDto updateAppuserDTO) {
        appuserService.updateCurrent(updateAppuserDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    ResponseEntity<Void> deleteCurrent() {
        appuserService.deleteCurrent();
        return ResponseEntity.noContent().build();
    }
}
