package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity(name = "appuser")
public class Appuser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Size(min = 5, max = 500)
    @NotBlank
    @Column(name = "username")
    private String username;

    @Size(min = 5, max = 500)
    @NotBlank
    @Column(name = "password")
    private String password;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "appuser_id")
    private Set<AppuserAuthority> authorities = new HashSet<>();

    public Appuser() {
    }

    Appuser(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public UUID getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Set<AppuserAuthority> getAuthorities() {
        return authorities;
    }

    public Set<String> getAuthoritiesScopeValues() {
        return authorities.stream().map(AppuserAuthority::getScopeValue).collect(Collectors.toSet());
    }

    void updateCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    void addApplicationAuthority() {
        authorities.add(new AppuserAuthority("APPLICATION"));
    }

    void removeApplicationAuthority() {
        authorities.removeIf(x -> x.getScopeValue().equals("APPLICATION"));
    }

}
