package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.PatientDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.PatientMapper;
import odoonto.application.port.in.patient.PatientQueryUseCase;
import odoonto.application.port.out.PatientRepositoryPort;
import odoonto.domain.model.aggregates.Patient;

import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para consultar pacientes
 */
@Service
public class PatientQueryService implements PatientQueryUseCase {

    private final PatientRepositoryPort patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientQueryService(PatientRepositoryPort patientRepository, 
                              PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public Flux<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return Flux.fromIterable(
            patients.stream()
                .map(patientMapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Mono<PatientDTO> getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + id));
        
        return Mono.just(patientMapper.toDTO(patient));
    }

    @Override
    public Flux<PatientDTO> searchPatients(String searchQuery) {
        List<Patient> patients = patientRepository.findByNameOrLastName(searchQuery);
        return Flux.fromIterable(
            patients.stream()
                .map(patientMapper::toDTO)
                .collect(Collectors.toList())
        );
    }
} 