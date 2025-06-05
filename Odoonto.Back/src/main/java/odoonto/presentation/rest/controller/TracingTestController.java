package odoonto.presentation.rest.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import odoonto.infrastructure.tracing.TracingConfiguration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador específico para probar el sistema de trazado.
 * Permite activar/desactivar el trazado y probar operaciones simples.
 */
@RestController
@RequestMapping("/api/tracing")
@CrossOrigin(origins = "*")
public class TracingTestController {

    @Autowired
    private TracingConfiguration tracingConfig;

    /**
     * Endpoint para probar el trazado con una operación simple
     */
    @GetMapping("/test-simple")
    public ResponseEntity<Map<String, Object>> testSimpleTrace() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Operación simple de prueba");
        response.put("timestamp", LocalDateTime.now());
        response.put("tracingEnabled", tracingConfig.isEnabled());
        
        // Simular algunas operaciones para generar trazado
        simulateBusinessLogic();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para activar/desactivar el trazado dinámicamente
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleTracing(@RequestParam boolean enabled) {
        tracingConfig.setEnabled(enabled);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tracingEnabled", enabled);
        response.put("message", enabled ? "Trazado ACTIVADO" : "Trazado DESACTIVADO");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para configurar selectivamente qué capas trazar
     */
    @PostMapping("/configure")
    public ResponseEntity<Map<String, Object>> configureTracing(
            @RequestParam(required = false) Boolean controllers,
            @RequestParam(required = false) Boolean services,
            @RequestParam(required = false) Boolean repositories,
            @RequestParam(required = false) Boolean domain,
            @RequestParam(required = false) Boolean dtosMappers,
            @RequestParam(required = false) Boolean useCases) {
        
        if (controllers != null) tracingConfig.setTraceControllers(controllers);
        if (services != null) tracingConfig.setTraceServices(services);
        if (repositories != null) tracingConfig.setTraceRepositories(repositories);
        if (domain != null) tracingConfig.setTraceDomain(domain);
        if (dtosMappers != null) tracingConfig.setTraceDtosMappers(dtosMappers);
        if (useCases != null) tracingConfig.setTraceUseCases(useCases);
        
        Map<String, Object> response = new HashMap<>();
        response.put("configuration", getCurrentConfiguration());
        response.put("message", "Configuración de trazado actualizada");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener la configuración actual del trazado
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTracingStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", tracingConfig.isEnabled());
        response.put("configuration", getCurrentConfiguration());
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para probar un flujo complejo con múltiples llamadas
     */
    @PostMapping("/test-complex")
    public ResponseEntity<Map<String, Object>> testComplexTrace() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Simular múltiples operaciones para mostrar un flujo más complejo
            Map<String, Object> results = new HashMap<>();
            
            // Operación 1: Validación
            results.put("validation", performValidation());
            
            // Operación 2: Procesamiento
            results.put("processing", performProcessing());
            
            // Operación 3: Finalización
            results.put("finalization", performFinalization());
            
            response.put("success", true);
            response.put("results", results);
            response.put("message", "Operación compleja completada - revisa los logs para ver el trazado completo");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Error en operación compleja - revisa los logs para ver el trazado del error");
        }
        
        response.put("timestamp", LocalDateTime.now());
        response.put("tracingEnabled", tracingConfig.isEnabled());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para generar un error intencionalmente y ver el trazado de errores
     */
    @GetMapping("/test-error")
    public ResponseEntity<Map<String, Object>> testErrorTrace() {
        try {
            // Simular un error para ver cómo se traza
            simulateError();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No debería llegar aquí");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Error capturado correctamente - revisa los logs para ver el trazado del error");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        }
    }

    // Métodos privados para simular operaciones de negocio
    
    private void simulateBusinessLogic() {
        // Simular algo de lógica de negocio
        processData("datos de ejemplo");
    }
    
    private String processData(String data) {
        // Simular procesamiento de datos
        return "Procesado: " + data;
    }
    
    private String performValidation() {
        validateInput("input data");
        return "Validación completada";
    }
    
    private void validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input no válido");
        }
    }
    
    private String performProcessing() {
        String result = processBusinessRule("business data");
        return "Procesamiento: " + result;
    }
    
    private String processBusinessRule(String data) {
        // Simular regla de negocio
        return data.toUpperCase();
    }
    
    private String performFinalization() {
        finalizeOperation();
        return "Finalización completada";
    }
    
    private void finalizeOperation() {
        // Simular finalización
    }
    
    private void simulateError() {
        // Método que intencionalmente lanza una excepción
        performRiskyOperation();
    }
    
    private void performRiskyOperation() {
        throw new RuntimeException("Error simulado para probar el trazado de excepciones");
    }

    private Map<String, Object> getCurrentConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("controllers", tracingConfig.isTraceControllers());
        config.put("services", tracingConfig.isTraceServices());
        config.put("repositories", tracingConfig.isTraceRepositories());
        config.put("domain", tracingConfig.isTraceDomain());
        config.put("dtosMappers", tracingConfig.isTraceDtosMappers());
        config.put("useCases", tracingConfig.isTraceUseCases());
        config.put("maxDepth", tracingConfig.getMaxDepth());
        config.put("slowMethodThreshold", tracingConfig.getSlowMethodThreshold());
        return config;
    }
} 