package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/appusers")
class AppuserController {

    private final AppuserService appuserService;

    AppuserController(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    @GetMapping
    ResponseEntity<AppuserDTO> find() {
        var body = appuserService.find();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<AppuserDTO> create(@Valid @RequestBody CreateAppuserDTO createAppuserDTO) {
        var body = appuserService.create(createAppuserDTO);
        var location = URI.create("/app/appusers");
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping
    ResponseEntity<Void> update(@Valid @RequestBody UpdateAppuserDTO updateAppuserDTO) {
        appuserService.update(updateAppuserDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    ResponseEntity<Void> delete() {
        appuserService.delete();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    ResponseEntity<String> token() {
        var body = appuserService.generateToken();
        return ResponseEntity.ok(body);
    }

}
