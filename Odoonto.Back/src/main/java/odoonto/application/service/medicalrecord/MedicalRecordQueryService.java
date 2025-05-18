package odoonto.application.service.medicalrecord;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.mapper.MedicalRecordMapper;
import odoonto.application.port.in.medicalrecord.MedicalRecordQueryUseCase;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación reactiva del caso de uso de consulta de historiales médicos.
 * Utiliza el repositorio reactivo para las operaciones de consulta.
 */
@Service
public class MedicalRecordQueryService implements MedicalRecordQueryUseCase {
    private final ReactiveMedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public MedicalRecordQueryService(ReactiveMedicalRecordRepository medicalRecordRepository,
                                     MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    @Override
    public Mono<MedicalRecordDTO> findById(String medicalRecordId) {
        return medicalRecordRepository.findById(medicalRecordId)
                .map(medicalRecordMapper::toDTO);
    }

    @Override
    public Mono<MedicalRecordDTO> findByPatientId(String patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .map(medicalRecordMapper::toDTO);
    }

    @Override
    public Flux<MedicalRecordDTO> findAll() {
        return medicalRecordRepository.findAll()
                .map(medicalRecordMapper::toDTO);
    }

    @Override
    public Mono<Boolean> existsById(String medicalRecordId) {
        return medicalRecordRepository.findById(medicalRecordId)
                .hasElement();
    }

    @Override
    public Mono<Boolean> existsByPatientId(String patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .hasElement();
    }
} 