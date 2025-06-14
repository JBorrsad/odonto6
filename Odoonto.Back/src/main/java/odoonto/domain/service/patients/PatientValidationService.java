package odoonto.domain.service.patients;

import org.jmolecules.ddd.annotation.Service;

import odoonto.domain.model.patients.valueobjects.EmailAddress;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.repository.patients.PatientRepository;

@Service
public class PatientValidationService {
    private final PatientRepository patientRepository;

    public PatientValidationService(final PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public boolean isEmailUnique(final EmailAddress email) {
        return patientRepository.findByEmail(email).isEmpty();
    }

    public boolean isEmailUniqueForPatient(final EmailAddress email, final PatientId patientId) {
        return patientRepository.findByEmail(email)
            .map(patient -> patient.getPatientId().equals(patientId))
            .orElse(true);
    }

    public boolean patientExists(final PatientId patientId) {
        return patientRepository.findById(patientId).isPresent();
    }
} 