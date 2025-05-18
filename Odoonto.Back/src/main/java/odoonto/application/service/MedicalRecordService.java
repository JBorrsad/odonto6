package odoonto.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.exceptions.MedicalRecordNotFoundException;
import odoonto.application.mapper.MedicalRecordMapper;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.aggregates.MedicalRecord.MedicalNote;
import odoonto.domain.model.aggregates.MedicalRecord.Diagnosis;
import odoonto.domain.model.aggregates.MedicalRecord.Treatment;

import java.time.LocalDateTime;
import java.util.UUID;

import reactor.core.publisher.Mono;

@Service
public class MedicalRecordService {

    private final ReactiveMedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public MedicalRecordService(ReactiveMedicalRecordRepository medicalRecordRepository, 
                                MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    /**
     * Obtiene un historial médico por su ID
     * @param id ID del historial médico
     * @return Mono con el DTO del historial médico
     */
    public Mono<MedicalRecordDTO> getMedicalRecordById(String id) {
        return medicalRecordRepository.findById(id)
                .switchIfEmpty(Mono.error(new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id)))
                .map(medicalRecordMapper::toDTO);
    }

    /**
     * Añade una entrada al historial médico
     * @param id ID del historial médico
     * @param entryDescription Descripción de la entrada
     * @return Mono con historial médico actualizado
     */
    public Mono<MedicalRecordDTO> addMedicalEntry(String id, String entryDescription) {
        return medicalRecordRepository.findById(id)
                .switchIfEmpty(Mono.error(new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id)))
                .flatMap(medicalRecord -> {
                    // Crear nueva nota médica
                    MedicalNote note = new MedicalNote(
                            entryDescription,
                            UUID.randomUUID() // En un entorno real, obtendríamos el ID del doctor actual
                    );
                    
                    // Añadir nota al historial
                    medicalRecord.addNote(note);
                    
                    // Guardar historial actualizado
                    return medicalRecordRepository.save(medicalRecord);
                })
                .map(medicalRecordMapper::toDTO);
    }

    /**
     * Añade un diagnóstico al historial médico
     * @param id ID del historial médico
     * @param diagnosisDescription Descripción del diagnóstico
     * @return Mono con historial médico actualizado
     */
    public Mono<MedicalRecordDTO> addDiagnosis(String id, String diagnosisDescription) {
        return medicalRecordRepository.findById(id)
                .switchIfEmpty(Mono.error(new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id)))
                .flatMap(medicalRecord -> {
                    // Crear nuevo diagnóstico
                    Diagnosis diagnosis = new Diagnosis(
                            diagnosisDescription,
                            UUID.randomUUID() // En un entorno real, obtendríamos el ID del doctor actual
                    );
                    
                    // Añadir diagnóstico al historial
                    medicalRecord.addDiagnosis(diagnosis);
                    
                    // Guardar historial actualizado
                    return medicalRecordRepository.save(medicalRecord);
                })
                .map(medicalRecordMapper::toDTO);
    }

    /**
     * Añade un tratamiento al historial médico
     * @param id ID del historial médico
     * @param treatmentDescription Descripción del tratamiento
     * @return Mono con historial médico actualizado
     */
    public Mono<MedicalRecordDTO> addTreatment(String id, String treatmentDescription) {
        return medicalRecordRepository.findById(id)
                .switchIfEmpty(Mono.error(new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id)))
                .flatMap(medicalRecord -> {
                    // Crear nuevo tratamiento
                    Treatment treatment = new Treatment(
                            treatmentDescription,
                            UUID.randomUUID() // En un entorno real, obtendríamos el ID del doctor actual
                    );
                    
                    // Añadir tratamiento al historial
                    medicalRecord.addTreatment(treatment);
                    
                    // Guardar historial actualizado
                    return medicalRecordRepository.save(medicalRecord);
                })
                .map(medicalRecordMapper::toDTO);
    }

    /**
     * Crea un nuevo historial médico para un paciente
     * @param patientId ID del paciente
     * @return Mono con DTO del historial médico creado
     */
    public Mono<MedicalRecordDTO> createMedicalRecord(String patientId) {
        // Convertir el ID a UUID
        UUID patientUUID = UUID.fromString(patientId);
        
        // Crear nuevo historial médico
        MedicalRecord medicalRecord = new MedicalRecord(patientUUID);
        
        // Guardar historial
        return medicalRecordRepository.save(medicalRecord)
                .map(medicalRecordMapper::toDTO);
    }
} 