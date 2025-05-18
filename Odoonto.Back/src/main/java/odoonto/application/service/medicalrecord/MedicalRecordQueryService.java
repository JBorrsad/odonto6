package odoonto.application.service.medicalrecord;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.mapper.MedicalRecordMapper;
import odoonto.application.port.in.medicalrecord.MedicalRecordQueryUseCase;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<MedicalRecordDTO> findById(String medicalRecordId) {
        return medicalRecordRepository.findById(medicalRecordId)
                .map(medicalRecordMapper::toDTO)
                .blockOptional();
    }

    @Override
    public Optional<MedicalRecordDTO> findByPatientId(String patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .map(medicalRecordMapper::toDTO)
                .blockOptional();
    }

    @Override
    public List<MedicalRecordDTO> findAll() {
        return medicalRecordRepository.findAll()
                .map(medicalRecordMapper::toDTO)
                .collectList()
                .block();
    }

    @Override
    public boolean existsById(String medicalRecordId) {
        return medicalRecordRepository.findById(medicalRecordId)
                .hasElement()
                .block();
    }

    @Override
    public boolean existsByPatientId(String patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .hasElement()
                .block();
    }
} 