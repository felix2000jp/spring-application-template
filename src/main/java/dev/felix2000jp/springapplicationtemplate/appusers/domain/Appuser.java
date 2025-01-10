package dev.felix2000jp.springapplicationtemplate.appusers.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Appuser implements UserDetails {

    @Id
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
        this.id = UUID.randomUUID();
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addApplicationScope() {
        authorities.add(new AppuserAuthority("APPLICATION"));
    }

}
