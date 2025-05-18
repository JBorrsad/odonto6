package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.out.PatientRepositoryPort;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para eliminar pacientes
 */
@Service
public class PatientDeleteService implements PatientDeleteUseCase {

    private final PatientRepositoryPort patientRepository;

    @Autowired
    public PatientDeleteService(PatientRepositoryPort patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Mono<Void> deletePatient(String id) {
        // Eliminar el paciente y retornar un Mono vacío
        return Mono.fromRunnable(() -> {
            patientRepository.deleteById(id);
        }).then();
    }
} 