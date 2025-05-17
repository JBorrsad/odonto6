package odoonto.domain.specifications;

/**
 * Interfaz base para el patrón de especificación.
 * Permite encapsular reglas de negocio para consultas complejas.
 * 
 * @param <T> Tipo de entidad sobre la que aplica la especificación
 */
public interface Specification<T> {
    
    /**
     * Evalúa si la entidad cumple con la especificación
     * @param entity Entidad a evaluar
     * @return true si la entidad cumple con la especificación
     */
    boolean isSatisfiedBy(T entity);
    
    /**
     * Combina esta especificación con otra usando el operador lógico AND
     * @param other Otra especificación
     * @return Nueva especificación que representa la operación AND
     */
    default Specification<T> and(Specification<T> other) {
        return entity -> this.isSatisfiedBy(entity) && other.isSatisfiedBy(entity);
    }
    
    /**
     * Combina esta especificación con otra usando el operador lógico OR
     * @param other Otra especificación
     * @return Nueva especificación que representa la operación OR
     */
    default Specification<T> or(Specification<T> other) {
        return entity -> this.isSatisfiedBy(entity) || other.isSatisfiedBy(entity);
    }
    
    /**
     * Niega esta especificación
     * @return Nueva especificación que representa la operación NOT
     */
    default Specification<T> not() {
        return entity -> !this.isSatisfiedBy(entity);
    }
} 