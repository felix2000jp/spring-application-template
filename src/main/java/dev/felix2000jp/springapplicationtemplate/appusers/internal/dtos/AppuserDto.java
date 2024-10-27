package dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos;

import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;

import java.util.Collection;
import java.util.UUID;

public record AppuserDto(UUID id, String username, Collection<AuthorityValue> authorities) {
}
