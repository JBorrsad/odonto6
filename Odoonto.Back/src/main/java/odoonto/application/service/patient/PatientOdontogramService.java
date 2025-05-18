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
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.application.port.out.ReactiveOdontogramRepository;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import odoonto.domain.model.valueobjects.PatientId;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para obtener información relacionada con el paciente
 */
@Service
public class PatientOdontogramService implements PatientOdontogramUseCase {

    private final ReactivePatientRepository patientRepository;
    private final ReactiveOdontogramRepository odontogramRepository;
    private final ReactiveMedicalRecordRepository medicalRecordRepository;
    private final OdontogramMapper odontogramMapper;
    private final MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public PatientOdontogramService(
            ReactivePatientRepository patientRepository,
            ReactiveOdontogramRepository odontogramRepository,
            ReactiveMedicalRecordRepository medicalRecordRepository,
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
        return patientRepository.findById(patientId)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("Paciente no encontrado con ID: " + patientId)))
                .then(odontogramRepository.findByPatientId(patientId)
                     .switchIfEmpty(Mono.error(new OdontogramNotFoundException("Odontograma no encontrado para el paciente con ID: " + patientId))));
    }

    @Override
    public Mono<OdontogramDTO> getPatientOdontogramDTO(String patientId) {
        return getPatientOdontogram(patientId)
                .map(odontogramMapper::toDTO);
    }

    @Override
    public Mono<MedicalRecordDTO> getPatientMedicalRecordDTO(String patientId) {
        return patientRepository.findById(patientId)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("Paciente no encontrado con ID: " + patientId)))
                .then(medicalRecordRepository.findByPatientId(patientId)
                     .switchIfEmpty(Mono.error(new MedicalRecordNotFoundException("Historial médico no encontrado para el paciente con ID: " + patientId))))
                .map(medicalRecordMapper::toDTO);
    }

    @Override
    public Mono<MedicalRecordId> getPatientMedicalRecord(String patientId) {
        return patientRepository.findById(patientId)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("Paciente no encontrado con ID: " + patientId)))
                .then(medicalRecordRepository.findByPatientId(patientId)
                     .switchIfEmpty(Mono.error(new MedicalRecordNotFoundException("Historial médico no encontrado para el paciente con ID: " + patientId))))
                .map(medicalRecord -> {
                    if (medicalRecord.getId() != null) {
                        return MedicalRecordId.of(medicalRecord.getId().toString());
                    } else {
                        return MedicalRecordId.fromPatientId(PatientId.of(patientId));
                    }
                });
    }
} 