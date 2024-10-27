package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity(name = "appuser_authority")
public class AppuserAuthority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "authority_value")
    private AuthorityValue authorityValue;

    public AppuserAuthority() {
    }

    public AppuserAuthority(AuthorityValue authorityValue) {
        this.authorityValue = authorityValue;
    }

    public AuthorityValue getAuthorityValue() {
        return authorityValue;
    }

    @Override
    public String getAuthority() {
        return authorityValue.name();
    }

}
