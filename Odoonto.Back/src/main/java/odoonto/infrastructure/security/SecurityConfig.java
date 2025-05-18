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
            .csrf().disable()
            // Configurar política de sesión sin estado
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // Configurar reglas de autorización
            .authorizeRequests()
                // Permitir acceso público a Swagger
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**").permitAll()
                // Permitir todas las peticiones (para desarrollo)
                .anyRequest().permitAll();
        
        return http.build();
    }
} 