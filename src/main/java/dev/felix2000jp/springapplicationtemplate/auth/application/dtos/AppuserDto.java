package dev.felix2000jp.springapplicationtemplate.auth.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record AppuserDto(
        @NotNull UUID id,
        @NotBlank
        @Size(min = 5, max = 500)
        String username,
        @NotNull
        Set<String> scopes
) {
}
