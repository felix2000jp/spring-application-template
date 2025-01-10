package dev.felix2000jp.springapplicationtemplate.appusers.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AppuserListDTO(@NotNull List<AppuserDTO> appusers) {
}