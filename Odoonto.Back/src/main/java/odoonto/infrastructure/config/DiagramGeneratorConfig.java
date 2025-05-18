package odoonto.infrastructure.config;

import odoonto.infrastructure.tools.DDDDiagramGenerator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuración para generar automáticamente los diagramas DDD al iniciar la aplicación.
 * Esta configuración ha sido modificada para ser compatible con el menú de inicio.
 */
@Configuration
public class DiagramGeneratorConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DiagramGeneratorConfig.class);
    
    /**
     * Este bean ha sido modificado para ser ejecutado sólo si la aplicación se inicia en modo
     * automático, sin pasar por el menú interactivo.
     */
    @Bean
    @Order(20) // Prioridad baja, para que se ejecute después del menú
    public ApplicationRunner diagramGeneratorRunner(Environment env) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                // Verificar si estamos en modo automático (sin menú interactivo)
                if (args.containsOption("auto-generate-diagrams")) {
                    try {
                        // Verificar si estamos en entorno de desarrollo o testing
                        String[] activeProfiles = env.getActiveProfiles();
                        boolean isProd = false;
                        
                        for (String profile : activeProfiles) {
                            if (profile.equals("prod") || profile.equals("production")) {
                                isProd = true;
                                break;
                            }
                        }
                        
                        if (!isProd) {
                            logger.info("Iniciando generación automática de diagramas DDD...");
                            DDDDiagramGenerator.generateDiagramForEntity("Patient");
                            logger.info("Generación de diagramas DDD completada.");
                        } else {
                            logger.info("Saltando generación de diagramas en entorno de producción.");
                        }
                    } catch (Exception e) {
                        // No interrumpir el inicio de la aplicación si falla la generación de diagramas
                        logger.error("Error al generar diagramas DDD: " + e.getMessage(), e);
                    }
                }
            }
        };
    }
} 