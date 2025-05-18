package odoonto.infrastructure.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuración que se ejecuta al iniciar la aplicación.
 * El menú interactivo se ha movido a la clase principal OdoontoApplication.
 */
@Configuration
public class StartupMenuConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(StartupMenuConfig.class);
    
    /**
     * Define un ApplicationRunner que se ejecutará durante la inicialización de Spring
     * para realizar cualquier configuración adicional necesaria.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationRunner startupConfigRunner(ApplicationContext appContext) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                try {
                    logger.info("Inicializando configuración de la aplicación...");
                    // Aquí cualquier inicialización adicional necesaria
                    
                    logger.info("Configuración de la aplicación inicializada correctamente");
                } catch (Exception e) {
                    logger.error("Error al inicializar la configuración: " + e.getMessage(), e);
                }
            }
        };
    }
} 