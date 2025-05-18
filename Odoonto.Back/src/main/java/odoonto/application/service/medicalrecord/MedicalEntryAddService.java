package odoonto.application.service.medicalrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.MedicalEntryCreateDTO;
import odoonto.application.port.in.medicalrecord.MedicalEntryAddUseCase;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.MedicalRecord;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementación reactiva del caso de uso para añadir entradas médicas a un historial
 */
@Service
public class MedicalEntryAddService implements MedicalEntryAddUseCase {

    private final ReactiveMedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalEntryAddService(ReactiveMedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public Mono<Void> addEntry(String medicalRecordId, MedicalEntryCreateDTO entryDTO) {
        // Validaciones básicas
        if (medicalRecordId == null || medicalRecordId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID del historial médico no puede ser nulo o vacío"));
        }
        
        if (entryDTO == null) {
            return Mono.error(new DomainException("Los datos de la entrada médica no pueden ser nulos"));
        }
        
        if (entryDTO.getDescription() == null || entryDTO.getDescription().trim().isEmpty()) {
            return Mono.error(new DomainException("La descripción de la entrada médica no puede ser nula o vacía"));
        }
        
        // Nota: La versión del agregado MedicalRecord tiene sus propias clases internas
        // para manejar notas, diagnósticos y tratamientos, en lugar de usar MedicalEntry
        
        // Buscar el historial médico, añadir la entrada y guardarlo
        return medicalRecordRepository.findById(medicalRecordId)
            .switchIfEmpty(Mono.error(new DomainException("No existe un historial médico con el ID: " + medicalRecordId)))
            .flatMap(medicalRecord -> {
                // Obtener UUID del doctor
                UUID doctorUuid = entryDTO.getDoctorId() != null ? 
                    UUID.fromString(entryDTO.getDoctorId()) : UUID.randomUUID();
                
                // Agregar la entrada según su tipo utilizando los métodos específicos de MedicalRecord
                if (entryDTO.getType().equalsIgnoreCase("nota")) {
                    medicalRecord.addNote(new MedicalRecord.MedicalNote(
                        entryDTO.getDescription(), doctorUuid));
                } else if (entryDTO.getType().equalsIgnoreCase("diagnóstico") || 
                           entryDTO.getType().equalsIgnoreCase("diagnostico")) {
                    medicalRecord.addDiagnosis(new MedicalRecord.Diagnosis(
                        entryDTO.getDescription(), doctorUuid));
                } else {
                    medicalRecord.addTreatment(new MedicalRecord.Treatment(
                        entryDTO.getDescription(), doctorUuid));
                }
                
                return medicalRecordRepository.save(medicalRecord);
            })
            .then();
    }
} 