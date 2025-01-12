package dev.felix2000jp.springapplicationtemplate.auth.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAppuserDto(@NotBlank @Size(min = 5, max = 500) String username) {
}
