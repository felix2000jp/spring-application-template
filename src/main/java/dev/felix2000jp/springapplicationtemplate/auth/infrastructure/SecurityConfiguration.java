package dev.felix2000jp.springapplicationtemplate.auth.infrastructure;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import dev.felix2000jp.springapplicationtemplate.auth.domain.SecurityScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    SecurityFilterChain apiAuthFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/appusers/register").permitAll()
                        .requestMatchers("/api/appusers/admin/**").hasAuthority(
                                SecurityScope.ADMIN.toAuthority()
                        )
                        .anyRequest().hasAnyAuthority(
                                SecurityScope.ADMIN.toAuthority(),
                                SecurityScope.APPLICATION.toAuthority()
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    SecurityFilterChain appAuthFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/app/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().hasAnyAuthority(
                                SecurityScope.ADMIN.toAuthority(),
                                SecurityScope.APPLICATION.toAuthority()
                        )
                )
                .formLogin(form -> form.loginPage("/app/login").defaultSuccessUrl("/app", true).permitAll())
                .logout(form -> form.logoutUrl("/app/logout").logoutSuccessUrl("/app/login?logout").permitAll())
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
