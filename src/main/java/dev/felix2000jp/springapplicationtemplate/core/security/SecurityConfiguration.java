package dev.felix2000jp.springapplicationtemplate.core.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import dev.felix2000jp.springapplicationtemplate.core.SecurityClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Value("${jwt.rsa.public-key}")
    private RSAPublicKey publicKey;

    @Value("${jwt.rsa.private-key}")
    private RSAPrivateKey privateKey;

    @Bean
    SecurityFilterChain basicAuthFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/auth/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/csrf", "/user").permitAll()
                        .anyRequest().hasAnyAuthority(
                                SecurityClient.ScopeValues.ADMIN.name(),
                                SecurityClient.ScopeValues.APPLICATION.name()
                        )
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    SecurityFilterChain tokenAuthFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("**/admin/**").hasAuthority(
                                SecurityClient.ScopeValues.ADMIN.name()
                        )
                        .anyRequest().hasAnyAuthority(
                                SecurityClient.ScopeValues.ADMIN.name(),
                                SecurityClient.ScopeValues.APPLICATION.name()
                        )
                )
                .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwkImmutableSet = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkImmutableSet);
    }

}
