package dev.felix2000jp.springapplicationtemplate.auth.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
public class AppuserAuthority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    private UUID id;

    @Column(name = "scope")
    @NotBlank
    private String scope;

    public AppuserAuthority() {
    }

    public AppuserAuthority(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String getAuthority() {
        return "SCOPE_" + scope;
    }

}
