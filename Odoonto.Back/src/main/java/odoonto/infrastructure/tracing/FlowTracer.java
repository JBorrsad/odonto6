package odoonto.infrastructure.tracing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sistema de trazado que intercepta automáticamente todos los métodos
 * de las diferentes capas DDD y muestra el flujo de ejecución
 */
@Aspect
@Component
public class FlowTracer {

    private static final Logger logger = LoggerFactory.getLogger(FlowTracer.class);
    
    @Autowired
    private TracingConfiguration config;
    
    // Para mantener el nivel de indentación por thread
    private static final ThreadLocal<AtomicInteger> indentLevel = ThreadLocal.withInitial(() -> new AtomicInteger(0));
    
    // Para mapear nombres de request únicos
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();
    private static final AtomicInteger requestCounter = new AtomicInteger(0);

    /**
     * Intercepta métodos de controladores REST
     */
    @Around("execution(* odoonto.presentation.rest..*(..))")
    public Object traceRestControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceControllers()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "🌐 CONTROLLER", "\u001B[34m"); // Azul
    }

    /**
     * Intercepta métodos de servicios de aplicación
     */
    @Around("execution(* odoonto.application.service..*(..))")
    public Object traceApplicationServices(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceServices()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "⚙️ SERVICE", "\u001B[32m"); // Verde
    }

    /**
     * Intercepta métodos de DTOs y mappers
     */
    @Around("execution(* odoonto.application.dto..*(..)) || execution(* odoonto.application.mapper..*(..))")
    public Object traceDtosAndMappers(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceDtosMappers()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "🔄 DTO/MAPPER", "\u001B[36m"); // Cian
    }

    /**
     * Intercepta métodos de repositorios
     */
    @Around("execution(* odoonto.infrastructure.persistence..*(..))")
    public Object traceRepositories(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceRepositories()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "🗄️ REPOSITORY", "\u001B[35m"); // Magenta
    }

    /**
     * Intercepta métodos de agregados y entidades de dominio
     */
    @Around("execution(* odoonto.domain.model.aggregates..*(..)) || execution(* odoonto.domain.model.entities..*(..))")
    public Object traceDomainAggregates(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceDomain()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "🏛️ DOMAIN", "\u001B[33m"); // Amarillo
    }

    /**
     * Intercepta métodos de value objects (pero solo constructores y métodos importantes)
     */
    @Around("execution(public odoonto.domain.model.valueobjects..new(..)) || execution(public * odoonto.domain.model.valueobjects..validate(..))")
    public Object traceValueObjects(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceDomain()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "💎 VALUE_OBJECT", "\u001B[37m"); // Blanco
    }

    /**
     * Intercepta métodos de casos de uso
     */
    @Around("execution(* odoonto.application.usecase..*(..))")
    public Object traceUseCases(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!config.isEnabled() || !config.isTraceUseCases()) {
            return joinPoint.proceed();
        }
        return traceMethod(joinPoint, "🎯 USE_CASE", "\u001B[31m"); // Rojo
    }

    /**
     * Método genérico para trazar la ejecución de cualquier método
     */
    private Object traceMethod(ProceedingJoinPoint joinPoint, String layerName, String color) throws Throwable {
        // Verificar profundidad máxima
        if (indentLevel.get().get() >= config.getMaxDepth()) {
            return joinPoint.proceed();
        }

        // Inicializar requestId si es el primer método interceptado en este thread
        boolean isNewRequest = false;
        if (requestId.get() == null) {
            requestId.set("REQ-" + requestCounter.incrementAndGet());
            indentLevel.get().set(0);
            isNewRequest = true;
            
            // Header del nuevo request
            logger.info("{}════════════════════════════════════════════════════════════════\u001B[0m", color);
            logger.info("{}🚀 NUEVA REQUEST: {} - Thread: {}\u001B[0m", color, requestId.get(), Thread.currentThread().getName());
            logger.info("{}════════════════════════════════════════════════════════════════\u001B[0m", color);
        }

        // Obtener información del método
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // Generar indentación
        int level = indentLevel.get().getAndIncrement();
        String indent = "  ".repeat(level);
        
        // Log de entrada
        String argsStr = formatArguments(args);
        logger.info("{}{}┌─ {} {} → {}.{}({})\u001B[0m", 
                   color, indent, layerName, requestId.get(), className, methodName, argsStr);

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            // Ejecutar el método original
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            exception = throwable;
            throw throwable;
        } finally {
            // Decrementar nivel de indentación
            indentLevel.get().decrementAndGet();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Determinar si es un método lento
            String timeIndicator = executionTime >= config.getSlowMethodThreshold() ? "🐌" : "⚡";
            
            // Log de salida
            if (exception != null) {
                logger.error("{}{}└─ {} {} ← {}.{}() ❌ ERROR: {} ({}ms) {}\u001B[0m", 
                           color, indent, layerName, requestId.get(), className, methodName, 
                           exception.getClass().getSimpleName(), executionTime, timeIndicator);
            } else {
                String resultStr = formatResult(result);
                logger.info("{}{}└─ {} {} ← {}.{}() ✅ {} ({}ms) {}\u001B[0m", 
                           color, indent, layerName, requestId.get(), className, methodName, 
                           resultStr, executionTime, timeIndicator);
            }
            
            // Si volvemos al nivel 0 Y es la request original, limpiar el thread local
            if (indentLevel.get().get() == 0 && isNewRequest) {
                logger.info("{}🏁 FIN REQUEST: {} - Tiempo total: {}ms {}\u001B[0m", 
                           color, requestId.get(), executionTime, timeIndicator);
                logger.info("{}════════════════════════════════════════════════════════════════\u001B[0m", color);
                requestId.remove();
                indentLevel.remove();
            }
        }
    }

    /**
     * Formatea los argumentos del método para mostrarlos en el log
     */
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length && i < 3; i++) { // Máximo 3 argumentos
            if (i > 0) sb.append(", ");
            
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String) {
                String str = (String) arg;
                sb.append("\"").append(str.length() > 30 ? str.substring(0, 27) + "..." : str).append("\"");
            } else if (arg instanceof Number || arg instanceof Boolean) {
                sb.append(arg.toString());
            } else if (arg.getClass().getPackage() != null && arg.getClass().getPackage().getName().startsWith("odoonto")) {
                // Es una clase del dominio, mostrar solo el tipo y algún identificador
                sb.append(arg.getClass().getSimpleName());
                try {
                    // Intentar obtener ID si existe
                    var idMethod = arg.getClass().getMethod("getId");
                    Object id = idMethod.invoke(arg);
                    if (id != null) {
                        String idStr = id.toString();
                        sb.append("[").append(idStr.length() > 8 ? idStr.substring(0, 8) + "..." : idStr).append("]");
                    }
                } catch (Exception e) {
                    // Si no tiene getId, solo mostrar el tipo
                }
            } else {
                sb.append(arg.getClass().getSimpleName());
            }
        }
        
        if (args.length > 3) {
            sb.append(", ...").append(args.length - 3).append(" más");
        }
        
        return sb.toString();
    }

    /**
     * Formatea el resultado del método para mostrarlo en el log
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        if (result instanceof String) {
            String str = (String) result;
            return "\"" + (str.length() > 20 ? str.substring(0, 17) + "..." : str) + "\"";
        }
        
        if (result instanceof Number || result instanceof Boolean) {
            return result.toString();
        }
        
        if (result.getClass().getPackage() != null && result.getClass().getPackage().getName().startsWith("odoonto")) {
            // Es una clase del dominio
            StringBuilder sb = new StringBuilder(result.getClass().getSimpleName());
            try {
                // Intentar obtener ID si existe
                var idMethod = result.getClass().getMethod("getId");
                Object id = idMethod.invoke(result);
                if (id != null) {
                    String idStr = id.toString();
                    sb.append("[").append(idStr.length() > 8 ? idStr.substring(0, 8) + "..." : idStr).append("]");
                }
            } catch (Exception e) {
                // Si no tiene getId, solo mostrar el tipo
            }
            return sb.toString();
        }
        
        return result.getClass().getSimpleName();
    }
} 