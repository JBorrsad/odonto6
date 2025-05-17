package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Odontogram;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de odontogramas
 * Abstrae el acceso a datos para la entidad Odontogram
 */
public interface OdontogramRepositoryPort {
    
    /**
     * Guarda un odontograma en el repositorio
     * @param odontogram Odontograma a guardar
     * @return Odontograma con posibles cambios (como ID asignado)
     */
    Odontogram save(Odontogram odontogram);
    
    /**
     * Busca un odontograma por su ID
     * @param id ID del odontograma
     * @return Optional con el odontograma si existe
     */
    Optional<Odontogram> findById(String id);
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Optional con el odontograma si existe
     */
    Optional<Odontogram> findByPatientId(String patientId);
    
    /**
     * Elimina un odontograma por su ID
     * @param id ID del odontograma a eliminar
     */
    void deleteById(String id);
} 