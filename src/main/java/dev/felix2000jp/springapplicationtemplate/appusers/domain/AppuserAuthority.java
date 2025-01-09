package dev.felix2000jp.springapplicationtemplate.appusers.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
public class AppuserAuthority implements GrantedAuthority {

    @Id
    @NotNull
    private UUID id;

    @Column(name = "scope_value")
    @NotBlank
    private String scopeValue;

    public AppuserAuthority() {
    }

    public AppuserAuthority(String scopeValue) {
        this.id = UUID.randomUUID();
        this.scopeValue = scopeValue;
    }

    public String getScopeValue() {
        return scopeValue;
    }

    @Override
    public String getAuthority() {
        return "SCOPE_" + scopeValue;
    }

}
