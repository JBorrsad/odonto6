package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.PatientMapper;
import odoonto.application.port.in.patient.PatientUpdateUseCase;
import odoonto.application.port.out.PatientRepositoryPort;
import odoonto.domain.model.aggregates.Patient;

/**
 * Implementación del caso de uso para actualizar pacientes
 */
@Service
public class PatientUpdateService implements PatientUpdateUseCase {

    private final PatientRepositoryPort patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientUpdateService(PatientRepositoryPort patientRepository, 
                              PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientDTO updatePatient(String id, PatientCreateDTO patientDTO) {
        // Verificar que el paciente existe
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + id));
        
        // Mapear los datos actualizados al paciente existente
        Patient updatedPatient = patientMapper.updateEntityFromDTO(patientDTO, existingPatient);
        
        // Persistir los cambios
        Patient savedPatient = patientRepository.save(updatedPatient);
        
        // Convertir a DTO
        return patientMapper.toDTO(savedPatient);
    }
} 