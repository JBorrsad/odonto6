package odoonto.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Configuración de seguridad para la aplicación
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura los filtros de seguridad HTTP
     * Esta es una configuración básica que permite todas las peticiones
     * En un entorno real, se debería implementar autenticación y autorización
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF para APIs RESTful
            .csrf(csrf -> csrf.disable())
            // Configurar política de sesión sin estado
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configurar reglas de autorización
            .authorizeHttpRequests(authorize -> authorize
                // Permitir acceso público a Swagger
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                // Permitir todas las peticiones (para desarrollo)
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
} 