package odoonto.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.model.valueobjects.MedicalRecordId;

/**
 * Servicio de aplicación para gestionar pacientes
 * Implementa casos de uso síncronos y proporciona métodos reactivos
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
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .map(patientMapper::toDTO)
                .collectList()
                .block();
    }

    @Override
    public PatientDTO getPatientById(String id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    @Override
    public List<PatientDTO> searchPatients(String searchQuery) {
        return patientRepository.findByNombreContainingOrApellidoContaining(searchQuery, searchQuery)
                .map(patientMapper::toDTO)
                .collectList()
                .block();
    }

    // Implementación de PatientCreateUseCase

    @Override
    public PatientDTO createPatient(PatientCreateDTO createDTO) {
        Patient patient = patientMapper.toEntity(createDTO);
        return patientRepository.save(patient)
                .map(patientMapper::toDTO)
                .block();
    }

    // Implementación de PatientUpdateUseCase

    @Override
    public PatientDTO updatePatient(String id, PatientCreateDTO updateDTO) {
        return patientRepository.findById(id)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(id)))
                .map(existingPatient -> {
                    patientMapper.updatePatientFromDTO(updateDTO, existingPatient);
                    return existingPatient;
                })
                .flatMap(patientRepository::save)
                .map(patientMapper::toDTO)
                .block();
    }

    // Implementación de PatientDeleteUseCase

    @Override
    public void deletePatient(String id) {
        patientRepository.deleteById(id).block();
    }

    // Implementación de PatientOdontogramUseCase

    @Override
    public Odontogram getPatientOdontogram(String patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getOdontogram)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    @Override
    public Object getPatientMedicalRecord(String patientId) {
        return patientRepository.findById(patientId)
                .map(patient -> patient.deriveMedicalRecordId())
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    /**
     * Actualiza el odontograma de un paciente
     * @param patientId ID del paciente
     * @param odontogram Odontograma actualizado
     * @return Odontograma actualizado
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public Odontogram updateOdontogram(String patientId, Odontogram odontogram) {
        return patientRepository.findById(patientId)
                .flatMap(patient -> {
                    patient.setOdontogram(odontogram);
                    return patientRepository.save(patient);
                })
                .map(Patient::getOdontogram)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    // Métodos reactivos puros (para uso interno o API reactiva)

    /**
     * Versión reactiva de getAllPatients
     * @return Flux de DTOs de pacientes
     */
    public Flux<PatientDTO> getAllPatientsReactive() {
        return patientRepository.findAll()
                .map(patientMapper::toDTO);
    }

    /**
     * Versión reactiva de getPatientById
     * @param id ID del paciente
     * @return Mono con el DTO del paciente
     */
    public Mono<PatientDTO> getPatientByIdReactive(String id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDTO)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(id)));
    }

    /**
     * Versión reactiva de createPatient
     * @param createDTO DTO con los datos del paciente
     * @return Mono con el DTO del paciente creado
     */
    public Mono<PatientDTO> createPatientReactive(PatientCreateDTO createDTO) {
        Patient patient = patientMapper.toEntity(createDTO);
        return patientRepository.save(patient)
                .map(patientMapper::toDTO);
    }

    /**
     * Versión reactiva de updatePatient
     * @param id ID del paciente a actualizar
     * @param updateDTO DTO con los datos actualizados
     * @return Mono con el DTO del paciente actualizado
     */
    public Mono<PatientDTO> updatePatientReactive(String id, PatientCreateDTO updateDTO) {
        return patientRepository.findById(id)
                .switchIfEmpty(Mono.error(new PatientNotFoundException(id)))
                .map(existingPatient -> {
                    patientMapper.updatePatientFromDTO(updateDTO, existingPatient);
                    return existingPatient;
                })
                .flatMap(patientRepository::save)
                .map(patientMapper::toDTO);
    }

    /**
     * Versión reactiva de deletePatient
     * @param id ID del paciente a eliminar
     * @return Mono que completa cuando se elimina el paciente
     */
    public Mono<Void> deletePatientReactive(String id) {
        return patientRepository.deleteById(id);
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