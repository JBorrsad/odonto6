package odoonto.infrastructure.tracing;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Configuración para el sistema de trazado
 */
@Configuration
@ConfigurationProperties(prefix = "odoonto.tracing")
@Data
public class TracingConfiguration {
    
    /**
     * Activa/desactiva el trazado completo
     */
    private boolean enabled = true;
    
    /**
     * Activa/desactiva trazado de controladores
     */
    private boolean traceControllers = true;
    
    /**
     * Activa/desactiva trazado de servicios
     */
    private boolean traceServices = true;
    
    /**
     * Activa/desactiva trazado de repositorios
     */
    private boolean traceRepositories = true;
    
    /**
     * Activa/desactiva trazado de dominio
     */
    private boolean traceDomain = true;
    
    /**
     * Activa/desactiva trazado de DTOs y mappers
     */
    private boolean traceDtosMappers = true;
    
    /**
     * Activa/desactiva trazado de casos de uso
     */
    private boolean traceUseCases = true;
    
    /**
     * Nivel máximo de profundidad de indentación
     */
    private int maxDepth = 10;
    
    /**
     * Tiempo mínimo en ms para considerar un método como "lento"
     */
    private long slowMethodThreshold = 100;
} 