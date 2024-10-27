package dev.felix2000jp.springapplicationtemplate.shared;

import java.util.Collection;
import java.util.UUID;

public record AppuserPrincipal(UUID id, String username, Collection<AuthorityValue> authorities) {
}
