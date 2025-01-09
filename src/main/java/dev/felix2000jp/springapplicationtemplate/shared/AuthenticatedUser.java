package dev.felix2000jp.springapplicationtemplate.shared;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String username, Set<String> authorities) {
}
