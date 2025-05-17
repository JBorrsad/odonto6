package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Doctor;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de doctores
 * Abstrae el acceso a datos para la entidad Doctor
 */
public interface DoctorRepositoryPort {
    
    /**
     * Guarda un doctor en el repositorio
     * @param doctor Doctor a guardar
     * @return Doctor con posibles cambios (como ID asignado)
     */
    Doctor save(Doctor doctor);
    
    /**
     * Busca un doctor por su ID
     * @param id ID del doctor
     * @return Optional con el doctor si existe
     */
    Optional<Doctor> findById(String id);
    
    /**
     * Obtiene todos los doctores
     * @return Lista de doctores
     */
    List<Doctor> findAll();
    
    /**
     * Elimina un doctor por su ID
     * @param id ID del doctor a eliminar
     */
    void deleteById(String id);
    
    /**
     * Busca doctores por nombre
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de doctores que coinciden
     */
    List<Doctor> findByNombre(String nombre);
    
    /**
     * Busca doctores por especialidad
     * @param especialidad Especialidad a buscar
     * @return Lista de doctores con esa especialidad
     */
    List<Doctor> findByEspecialidad(String especialidad);
} 