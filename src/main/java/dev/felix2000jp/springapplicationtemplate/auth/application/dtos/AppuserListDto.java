package dev.felix2000jp.springapplicationtemplate.auth.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AppuserListDto(@NotNull List<AppuserDto> appusers) {
}