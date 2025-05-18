package odoonto.application.port.out;

import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.valueobjects.MedicalRecordId;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Puerto de salida reactivo para el repositorio de historiales médicos.
 * Esta interfaz adapta el repositorio de dominio a una interfaz reactiva
 * para su uso en la capa de aplicación.
 */
public interface ReactiveMedicalRecordRepository {
    
    /**
     * Busca todos los historiales médicos
     * @return Flux con todos los historiales médicos
     */
    Flux<MedicalRecord> findAll();
    
    /**
     * Busca un historial médico por su identificador
     * @param id Identificador único del historial médico
     * @return Mono con el historial médico encontrado o empty si no existe
     */
    Mono<MedicalRecord> findById(UUID id);
    
    /**
     * Busca un historial médico por el identificador del paciente
     * @param patientId Identificador único del paciente
     * @return Mono con el historial médico encontrado o empty si no existe
     */
    Mono<MedicalRecord> findByPatientId(UUID patientId);
    
    /**
     * Guarda un historial médico en el repositorio
     * @param medicalRecord Historial médico a guardar
     * @return Mono con el historial médico guardado
     */
    Mono<MedicalRecord> save(MedicalRecord medicalRecord);
    
    /**
     * Elimina un historial médico por su identificador
     * @param id Identificador único del historial médico
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> deleteById(UUID id);
    
    /**
     * Añade una entrada al historial médico
     * @param medicalRecordId ID del historial médico
     * @param entry Entrada a añadir
     * @return Mono con la entrada añadida con su ID
     */
    Mono<MedicalEntry> addEntry(MedicalRecordId medicalRecordId, MedicalEntry entry);
    
    /**
     * Obtiene todas las entradas de un historial médico
     * @param medicalRecordId ID del historial médico
     * @return Flux con todas las entradas
     */
    Flux<MedicalEntry> findAllEntries(MedicalRecordId medicalRecordId);
    
    /**
     * Busca entradas por fecha
     * @param medicalRecordId ID del historial médico
     * @param date Fecha a buscar
     * @return Flux con las entradas en esa fecha
     */
    Flux<MedicalEntry> findEntriesByDate(MedicalRecordId medicalRecordId, LocalDate date);
    
    /**
     * Busca entradas por doctor
     * @param medicalRecordId ID del historial médico
     * @param doctorId ID del doctor
     * @return Flux con las entradas realizadas por ese doctor
     */
    Flux<MedicalEntry> findEntriesByDoctor(MedicalRecordId medicalRecordId, String doctorId);
    
    /**
     * Actualiza una entrada específica
     * @param medicalRecordId ID del historial médico
     * @param entryId ID de la entrada
     * @param entry Entrada actualizada
     * @return Mono que completa con true si se actualizó correctamente
     */
    Mono<Boolean> updateEntry(MedicalRecordId medicalRecordId, String entryId, MedicalEntry entry);
    
    /**
     * Elimina una entrada
     * @param medicalRecordId ID del historial médico
     * @param entryId ID de la entrada
     * @return Mono que completa con true si se eliminó correctamente
     */
    Mono<Boolean> deleteEntry(MedicalRecordId medicalRecordId, String entryId);
    
    /**
     * Añade una alergia al historial médico
     * @param medicalRecordId ID del historial médico
     * @param allergy Alergia a añadir
     * @return Mono que completa con true si se añadió correctamente
     */
    Mono<Boolean> addAllergy(MedicalRecordId medicalRecordId, String allergy);
    
    /**
     * Añade una condición médica al historial
     * @param medicalRecordId ID del historial médico
     * @param condition Condición médica a añadir
     * @return Mono que completa con true si se añadió correctamente
     */
    Mono<Boolean> addMedicalCondition(MedicalRecordId medicalRecordId, String condition);
    
    /**
     * Método de compatibilidad para buscar por id usando String
     * @param id Identificador como String
     * @return Mono con el historial médico encontrado o empty si no existe
     */
    default Mono<MedicalRecord> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        return findById(UUID.fromString(id));
    }
    
    /**
     * Método de compatibilidad para buscar por id de paciente usando String
     * @param patientId Identificador como String
     * @return Mono con el historial médico encontrado o empty si no existe
     */
    default Mono<MedicalRecord> findByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.empty();
        }
        return findByPatientId(UUID.fromString(patientId));
    }
    
    /**
     * Método de compatibilidad para eliminar por id usando String
     * @param id Identificador como String
     * @return Mono que completa cuando la operación termina
     */
    default Mono<Void> deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        return deleteById(UUID.fromString(id));
    }
    
    /**
     * Método de compatibilidad para eliminar una entrada usando String como medicalRecordId
     * @param medicalRecordId Identificador del historial médico como String
     * @param entryId ID de la entrada
     * @return Mono que completa con true si se eliminó correctamente
     */
    default Mono<Boolean> deleteEntry(String medicalRecordId, String entryId) {
        if (medicalRecordId == null || medicalRecordId.trim().isEmpty()) {
            return Mono.just(false);
        }
        return deleteEntry(MedicalRecordId.of(medicalRecordId), entryId);
    }
} 