package dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos;

import java.util.Set;
import java.util.UUID;

public record AppuserDTO(UUID id, String username, Set<String> authorities) {
}
