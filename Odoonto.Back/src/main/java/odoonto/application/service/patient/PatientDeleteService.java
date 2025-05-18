package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.out.ReactivePatientRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para eliminar pacientes
 */
@Service
public class PatientDeleteService implements PatientDeleteUseCase {

    private final ReactivePatientRepository patientRepository;

    @Autowired
    public PatientDeleteService(ReactivePatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Mono<Void> deletePatient(String id) {
        // Eliminar el paciente de forma reactiva
        return patientRepository.deleteById(id);
    }
} 