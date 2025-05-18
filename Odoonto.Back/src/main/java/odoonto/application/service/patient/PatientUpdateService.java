package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.PatientMapper;
import odoonto.application.port.in.patient.PatientUpdateUseCase;
import odoonto.application.port.out.ReactivePatientRepository;

import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para actualizar pacientes
 */
@Service
public class PatientUpdateService implements PatientUpdateUseCase {

    private final ReactivePatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientUpdateService(ReactivePatientRepository patientRepository, 
                              PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public Mono<PatientDTO> updatePatient(String id, PatientCreateDTO patientDTO) {
        // Verificar que el paciente existe y actualizarlo
        return patientRepository.findById(id)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("Paciente no encontrado con ID: " + id)))
                .map(existingPatient -> patientMapper.updateEntityFromDTO(patientDTO, existingPatient))
                .flatMap(patientRepository::save)
                .map(patientMapper::toDTO);
    }
} 