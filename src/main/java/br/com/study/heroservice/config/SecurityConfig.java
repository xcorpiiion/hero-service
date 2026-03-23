package br.com.study.heroservice.config;

import br.com.study.genericauthorization.configuration.SecurityRegistryCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Os atributos internos já possuem esses defaults no Spring Boot 3
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityRegistryCustomizer securityCustomizer() {
        return auth -> auth
                .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/registered-users/**").permitAll()
                .requestMatchers("/premium/**").hasAuthority("ULTRA_USER")
                .requestMatchers(HttpMethod.POST, "/internal/**").denyAll();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}