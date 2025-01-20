package dev.felix2000jp.springapplicationtemplate.auth.application.events;

import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface
public record AppuserDeletedEvent(UUID appuserId) {
}
