package dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAppuserDTO(
        @Size(min = 5, max = 500)
        @NotBlank
        String username,
        @Size(min = 5, max = 500)
        @NotBlank
        String password
) {
}
