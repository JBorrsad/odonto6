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
    public OdontogramDTO getPatientOdontogram(String patientId) {
        // Verificar que el paciente existe
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + patientId));
        
        // Buscar el odontograma asociado al paciente
        Odontogram odontogram = odontogramRepository.findByPatientId(patientId)
                .orElseThrow(() -> new OdontogramNotFoundException("Odontograma no encontrado para el paciente con ID: " + patientId));
        
        // Convertir a DTO
        return odontogramMapper.toDTO(odontogram);
    }

    @Override
    public MedicalRecordDTO getPatientMedicalRecord(String patientId) {
        // Verificar que el paciente existe
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + patientId));
        
        // Buscar el historial médico asociado al paciente
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new MedicalRecordNotFoundException("Historial médico no encontrado para el paciente con ID: " + patientId));
        
        // Convertir a DTO
        return medicalRecordMapper.toDTO(medicalRecord);
    }
} 