package dev.felix2000jp.springapplicationtemplate.appusers.internal;

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
    @Column(name = "scope_value")
    private String scopeValue;

    public AppuserAuthority() {
    }

    AppuserAuthority(String scopeValue) {
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
