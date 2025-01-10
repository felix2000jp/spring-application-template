package dev.felix2000jp.springapplicationtemplate.appusers.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record AppuserDTO(
        @NotNull
        UUID id,
        @NotBlank
        @Size(min = 5, max = 500)
        String username,
        @NotNull
        Set<String> authorities) {
}
