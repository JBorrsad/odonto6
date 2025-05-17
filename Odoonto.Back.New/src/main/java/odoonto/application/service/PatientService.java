package odoonto.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.PatientMapper;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.repository.PatientRepository;

/**
 * Servicio de aplicación para gestionar pacientes
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    /**
     * Obtiene todos los pacientes
     * @return Lista de DTOs de pacientes
     */
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .map(patientMapper::toDTO)
                .collectList()
                .block();
    }

    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return DTO del paciente
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public PatientDTO getPatientById(String id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    /**
     * Crea un nuevo paciente
     * @param createDTO DTO con los datos del paciente
     * @return DTO del paciente creado
     */
    public PatientDTO createPatient(PatientCreateDTO createDTO) {
        Patient patient = patientMapper.toEntity(createDTO);
        Patient savedPatient = patientRepository.save(patient).block();
        return patientMapper.toDTO(savedPatient);
    }

    /**
     * Actualiza un paciente existente
     * @param id ID del paciente a actualizar
     * @param updateDTO DTO con los datos actualizados
     * @return DTO del paciente actualizado
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public PatientDTO updatePatient(String id, PatientCreateDTO updateDTO) {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    patientMapper.updatePatientFromDTO(updateDTO, existingPatient);
                    return patientRepository.save(existingPatient).block();
                })
                .map(patientMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    /**
     * Elimina un paciente
     * @param id ID del paciente a eliminar
     */
    public void deletePatient(String id) {
        patientRepository.deleteById(id).block();
    }

    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Odontograma del paciente
     * @throws PatientNotFoundException si no se encuentra el paciente
     */
    public Odontogram getOdontogram(String patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getOdontogram)
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
} 