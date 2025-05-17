package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.mapper.PatientMapper;
import odoonto.application.port.in.patient.PatientCreateUseCase;
import odoonto.application.port.out.PatientRepositoryPort;
import odoonto.domain.model.aggregates.Patient;

/**
 * Implementación del caso de uso para crear pacientes
 */
@Service
public class PatientCreateService implements PatientCreateUseCase {

    private final PatientRepositoryPort patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientCreateService(PatientRepositoryPort patientRepository, 
                              PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientDTO createPatient(PatientCreateDTO patientDTO) {
        // Validar datos del paciente (podría lanzar excepciones de dominio)
        
        // Convertir DTO a entidad de dominio
        Patient patient = patientMapper.toEntity(patientDTO);
        
        // Persistir la entidad
        Patient savedPatient = patientRepository.save(patient);
        
        // Convertir entidad guardada a DTO de respuesta
        return patientMapper.toDTO(savedPatient);
    }
} 