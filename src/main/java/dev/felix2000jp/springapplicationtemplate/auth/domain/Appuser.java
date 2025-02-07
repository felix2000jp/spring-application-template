package dev.felix2000jp.springapplicationtemplate.auth.domain;

import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityScope;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Appuser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    private UUID id;

    @Column(name = "username")
    @NotBlank
    @Size(min = 5, max = 500)
    private String username;

    @Column(name = "password")
    @NotBlank
    @Size(min = 5, max = 500)
    private String password;

    @JoinColumn(name = "appuser_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppuserAuthority> authorities;

    public Appuser() {
    }

    public Appuser(String username, String password) {
        this.username = username;
        this.password = password;
        this.authorities = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Set<AppuserAuthority> getAuthorities() {
        return authorities;
    }

    public Set<String> getAuthoritiesScopes() {
        return authorities.stream().map(AppuserAuthority::getScope).collect(Collectors.toSet());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addScopeAdmin() {
        var adminScopeValue = SecurityScope.ADMIN.name();
        authorities.add(new AppuserAuthority(adminScopeValue));
    }

    public void addScopeApplication() {
        var applicationScopeValue = SecurityScope.APPLICATION.name();
        authorities.add(new AppuserAuthority(applicationScopeValue));
    }

}
