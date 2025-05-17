package odoonto.application.service.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.out.PatientRepositoryPort;

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
    public void deletePatient(String id) {
        // Si quisiéramos verificar que existe primero:
        // patientRepository.findById(id)
        //     .orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con ID: " + id));
        
        // Eliminar el paciente
        patientRepository.deleteById(id);
    }
} 