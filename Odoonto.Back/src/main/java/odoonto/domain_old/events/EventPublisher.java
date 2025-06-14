package odoonto.domain.events;

/**
 * Interfaz para publicar eventos de dominio.
 * Define el contrato para los componentes que publican eventos.
 */
public interface EventPublisher {
    
    /**
     * Publica un evento de dominio
     * @param event Evento a publicar
     */
    void publish(DomainEvent event);
    
    /**
     * Publica múltiples eventos de dominio
     * @param events Eventos a publicar
     */
    default void publishAll(Iterable<DomainEvent> events) {
        events.forEach(this::publish);
    }
} 