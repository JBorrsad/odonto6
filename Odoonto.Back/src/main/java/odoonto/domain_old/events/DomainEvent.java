package odoonto.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interfaz base para todos los eventos de dominio.
 * Define las propiedades comunes que debe tener cualquier evento del dominio.
 */
public abstract class DomainEvent {
    private final UUID eventId;
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
    }
    
    /**
     * @return Identificador único del evento
     */
    public UUID getEventId() {
        return eventId;
    }
    
    /**
     * @return Fecha y hora en que ocurrió el evento
     */
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    /**
     * @return Tipo del evento (normalmente el nombre de la clase)
     */
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
} 