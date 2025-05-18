package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.Specialty;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para el agregado Doctor.
 * Define operaciones puras de dominio sin dependencias externas.
 */
public interface DoctorRepository {
    
    /**
     * Busca un doctor por su ID
     * @param id ID del doctor
     * @return Doctor encontrado o Empty si no existe
     */
    Optional<Doctor> findById(String id);
    
    /**
     * Guarda un doctor
     * @param doctor Doctor a guardar
     * @return Doctor guardado con su ID asignado
     */
    Doctor save(Doctor doctor);
    
    /**
     * Elimina un doctor por su ID
     * @param id ID del doctor a eliminar
     */
    void deleteById(String id);
    
    /**
     * Obtiene todos los doctores
     * @return Lista de doctores
     */
    List<Doctor> findAll();
    
    /**
     * Busca doctores por especialidad
     * @param especialidad Especialidad a buscar
     * @return Lista de doctores con la especialidad indicada
     */
    List<Doctor> findByEspecialidad(Specialty especialidad);
    
    /**
     * Busca doctores cuyo nombre contiene la cadena especificada
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de doctores que coinciden con el criterio
     */
    List<Doctor> findByNombreCompletoContaining(String nombre);
}