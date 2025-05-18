package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.exceptions.OdontogramNotFoundException;
import odoonto.application.exceptions.MedicalRecordNotFoundException;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.application.mapper.MedicalRecordMapper;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.port.out.PatientRepositoryPort;
import odoonto.application.port.out.OdontogramRepositoryPort;
import odoonto.application.port.out.MedicalRecordRepositoryPort;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import odoonto.domain.model.valueobjects.PatientId;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para obtener información relacionada con el paciente
 */
@Service
public class PatientOdontogramService implements PatientOdontogramUseCase {

    private final PatientRepositoryPort patientRepository;
    private final OdontogramRepositoryPort odontogramRepository;
    private final MedicalRecordRepositoryPort medicalRecordRepository;
    private final OdontogramMapper odontogramMapper;
    private final MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public PatientOdontogramService(
            PatientRepositoryPort patientRepository,
            OdontogramRepositoryPort odontogramRepository,
            MedicalRecordRepositoryPort medicalRecordRepository,
            OdontogramMapper odontogramMapper,
            MedicalRecordMapper medicalRecordMapper) {
        this.patientRepository = patientRepository;
        this.odontogramRepository = odontogramRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.odontogramMapper = odontogramMapper;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    @Override
    public Mono<Odontogram> getPatientOdontogram(String patientId) {
        // Convertir a operación reactiva
        return Mono.fromCallable(() -> {
            // Verificar que el paciente existe
            patientRepository.findById(patientId)
                    .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + patientId));
            
            // Buscar el odontograma asociado al paciente
            return odontogramRepository.findByPatientId(patientId)
                    .orElseThrow(() -> new OdontogramNotFoundException("Odontograma no encontrado para el paciente con ID: " + patientId));
        });
    }

    @Override
    public Mono<MedicalRecordId> getPatientMedicalRecord(String patientId) {
        // Convertir a operación reactiva
        return Mono.fromCallable(() -> {
            // Verificar que el paciente existe
            patientRepository.findById(patientId)
                    .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + patientId));
            
            // Buscar el historial médico asociado al paciente
            MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                    .orElseThrow(() -> new MedicalRecordNotFoundException("Historial médico no encontrado para el paciente con ID: " + patientId));
            
            // Crear MedicalRecordId a partir del ID del historial o del patientId
            if (medicalRecord.getId() != null) {
                return MedicalRecordId.of(medicalRecord.getId().toString());
            } else {
                return MedicalRecordId.fromPatientId(PatientId.of(patientId));
            }
        });
    }
} 