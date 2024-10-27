package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

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
    private Set<AppuserAuthority> authorities;

    public Appuser() {
    }

    public Appuser(String username, String password, AuthorityValue authorityValue) {
        this.username = username;
        this.password = password;
        this.authorities = Set.of(new AppuserAuthority(authorityValue));
    }

    public Appuser(UUID id, String username, String password, AuthorityValue authorityValue) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = Set.of(new AppuserAuthority(authorityValue));
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
    public Collection<AppuserAuthority> getAuthorities() {
        return authorities;
    }

    public Collection<AuthorityValue> getAuthorityValues() {
        return authorities.stream().map(AppuserAuthority::getAuthorityValue).toList();
    }

    public void updateCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addAuthority(AuthorityValue authorityValue) {
        var appuserAuthority = new AppuserAuthority(authorityValue);
        authorities.add(appuserAuthority);
    }

    public void removeAuthority(AuthorityValue authorityValue) {
        authorities.removeIf(x -> x.getAuthorityValue().equals(authorityValue));
    }

}
