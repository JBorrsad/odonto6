package odoonto.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.PatientMapper;
import odoonto.application.port.in.patient.PatientCreateUseCase;
import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.port.in.patient.PatientQueryUseCase;
import odoonto.application.port.in.patient.PatientUpdateUseCase;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.MedicalRecordId;

/**
 * Servicio de aplicación para gestionar pacientes
 * Implementación completamente reactiva
 */
@Service
public class PatientService implements 
        PatientQueryUseCase, 
        PatientCreateUseCase, 
        PatientUpdateUseCase, 
        PatientDeleteUseCase, 
        PatientOdontogramUseCase {

    private final ReactivePatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientService(
            ReactivePatientRepository patientRepository,
            PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    // Implementación de PatientQueryUseCase

    @Override
    public Flux<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .map(patientMapper::toDTO);
    }

    @Override
    public Mono<PatientDTO> getPatientById(String id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDTO)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(id)));
    }

    @Override
    public Flux<PatientDTO> searchPatients(String searchQuery) {
        return patientRepository.findByNombreContainingOrApellidoContaining(searchQuery, searchQuery)
                .map(patientMapper::toDTO);
    }

    // Implementación de PatientCreateUseCase

    @Override
    public Mono<PatientDTO> createPatient(PatientCreateDTO createDTO) {
        Patient patient = patientMapper.toEntity(createDTO);
        return patientRepository.save(patient)
                .map(patientMapper::toDTO);
    }

    // Implementación de PatientUpdateUseCase

    @Override
    public Mono<PatientDTO> updatePatient(String id, PatientCreateDTO updateDTO) {
        return patientRepository.findById(id)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(id)))
                .map(existingPatient -> {
                    patientMapper.updateEntityFromDTO(updateDTO, existingPatient);
                    return existingPatient;
                })
                .flatMap(patientRepository::save)
                .map(patientMapper::toDTO);
    }

    // Implementación de PatientDeleteUseCase

    @Override
    public Mono<Void> deletePatient(String id) {
        return patientRepository.deleteById(id);
    }

    // Implementación de PatientOdontogramUseCase

    @Override
    public Mono<Odontogram> getPatientOdontogram(String patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getOdontogram)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(patientId)));
    }

    @Override
    public Mono<MedicalRecordId> getPatientMedicalRecord(String patientId) {
        return patientRepository.findById(patientId)
                .map(patient -> patient.deriveMedicalRecordId())
                .switchIfEmpty(Mono.error(new PatientNotFoundException(patientId)));
    }

    /**
     * Actualiza el odontograma de un paciente
     * @param patientId ID del paciente
     * @param odontogram Odontograma actualizado
     * @return Odontograma actualizado
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public Mono<Odontogram> updateOdontogram(String patientId, Odontogram odontogram) {
        return patientRepository.findById(patientId)
                .flatMap(patient -> {
                    patient.setOdontogram(odontogram);
                    return patientRepository.save(patient);
                })
                .map(Patient::getOdontogram)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(patientId)));
    }

    /**
     * Versión reactiva para buscar pacientes por nombre o apellido
     * @param nombre Texto a buscar en el nombre
     * @param apellido Texto a buscar en el apellido
     * @return Flux con los pacientes que coinciden con la búsqueda
     */
    public Flux<PatientDTO> findByNombreContainingOrApellidoContaining(String nombre, String apellido) {
        return patientRepository.findByNombreContainingOrApellidoContaining(nombre, apellido)
                .map(patientMapper::toDTO);
    }
} 