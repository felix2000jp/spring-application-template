package dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAppuserDTO(
        @Size(min = 5, max = 500)
        @NotBlank
        String username,
        @Size(min = 5, max = 500)
        @NotBlank
        String password
) {
}
