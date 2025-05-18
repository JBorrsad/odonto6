package odoonto.application.service.medicalrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import odoonto.application.dto.request.MedicalRecordCreateDTO;
import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.MedicalRecordMapper;
import odoonto.application.port.in.medicalrecord.MedicalRecordCreateUseCase;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.MedicalRecord;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Implementación reactiva del caso de uso para crear historiales médicos
 */
@Service
public class MedicalRecordCreateService implements MedicalRecordCreateUseCase {

    private final ReactiveMedicalRecordRepository medicalRecordRepository;
    private final ReactivePatientRepository patientRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public MedicalRecordCreateService(ReactiveMedicalRecordRepository medicalRecordRepository,
                                    ReactivePatientRepository patientRepository,
                                    MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    @Override
    public Mono<MedicalRecordDTO> createMedicalRecord(MedicalRecordCreateDTO createDTO) {
        // Validaciones básicas
        if (createDTO == null) {
            return Mono.error(new DomainException("Los datos del historial médico no pueden ser nulos"));
        }
        
        if (createDTO.getPatientId() == null || createDTO.getPatientId().trim().isEmpty()) {
            return Mono.error(new DomainException("El ID del paciente no puede ser nulo o vacío"));
        }
        
        // Verificar que el paciente existe
        return patientRepository.existsById(createDTO.getPatientId())
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new PatientNotFoundException("No existe un paciente con el ID: " + createDTO.getPatientId()));
                }
                
                // Verificar que no exista ya un historial para este paciente
                return medicalRecordRepository.findByPatientId(createDTO.getPatientId())
                    .hasElement()
                    .flatMap(hasRecord -> {
                        if (hasRecord) {
                            return Mono.error(new DomainException("Ya existe un historial médico para el paciente con ID: " + createDTO.getPatientId()));
                        }
                        
                        // Crear un nuevo historial médico
                        UUID patientUuid = UUID.fromString(createDTO.getPatientId());
                        MedicalRecord medicalRecord = new MedicalRecord(patientUuid);
                        
                        // Añadir alergias si existen
                        if (createDTO.getAllergies() != null && !createDTO.getAllergies().isEmpty()) {
                            // En este ejemplo, las alergias se manejarían en un método diferente del historial médico
                            // La implementación dependerá de cómo esté definido el agregado MedicalRecord
                        }
                        
                        // Añadir condiciones médicas si existen
                        if (createDTO.getMedicalConditions() != null && !createDTO.getMedicalConditions().isEmpty()) {
                            // Similar a las alergias, esto dependerá de la implementación del agregado
                        }
                        
                        // Añadir entradas iniciales si existen
                        if (createDTO.getEntries() != null && !createDTO.getEntries().isEmpty()) {
                            for (MedicalRecordCreateDTO.MedicalEntryCreateDTO entryDTO : createDTO.getEntries()) {
                                UUID doctorId = entryDTO.getDoctorId() != null ? 
                                    UUID.fromString(entryDTO.getDoctorId()) : UUID.randomUUID();
                                
                                // Añadir la entrada según su tipo
                                if (entryDTO.getType().equalsIgnoreCase("nota")) {
                                    medicalRecord.addNote(new MedicalRecord.MedicalNote(
                                        entryDTO.getDescription(), doctorId));
                                } else if (entryDTO.getType().equalsIgnoreCase("diagnóstico") || 
                                           entryDTO.getType().equalsIgnoreCase("diagnostico")) {
                                    medicalRecord.addDiagnosis(new MedicalRecord.Diagnosis(
                                        entryDTO.getDescription(), doctorId));
                                } else {
                                    medicalRecord.addTreatment(new MedicalRecord.Treatment(
                                        entryDTO.getDescription(), doctorId));
                                }
                            }
                        }
                        
                        // Guardar el historial médico
                        return medicalRecordRepository.save(medicalRecord)
                            .map(medicalRecordMapper::toDTO);
                    });
            });
    }
} 