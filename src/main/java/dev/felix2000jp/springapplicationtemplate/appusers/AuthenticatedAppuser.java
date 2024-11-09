package dev.felix2000jp.springapplicationtemplate.appusers;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedAppuser(UUID id, String username, Set<String> authorities) {
}
