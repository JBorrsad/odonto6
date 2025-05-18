package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.PatientDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.PatientMapper;
import odoonto.application.port.in.patient.PatientQueryUseCase;
import odoonto.application.port.out.ReactivePatientRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para consultar pacientes
 */
@Service
public class PatientQueryService implements PatientQueryUseCase {

    private final ReactivePatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientQueryService(ReactivePatientRepository patientRepository, 
                              PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public Flux<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .map(patientMapper::toDTO);
    }

    @Override
    public Mono<PatientDTO> getPatientById(String id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDTO)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("Paciente no encontrado con ID: " + id)));
    }

    @Override
    public Flux<PatientDTO> searchPatients(String searchQuery) {
        return patientRepository.findByNameContaining(searchQuery)
                .map(patientMapper::toDTO);
    }
} 