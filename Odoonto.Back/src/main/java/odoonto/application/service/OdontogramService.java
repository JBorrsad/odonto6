package odoonto.application.service;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.repository.OdontogramRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestionar odontogramas.
 */
@Service
public class OdontogramService {

    private final OdontogramRepository odontogramRepository;
    private final OdontogramMapper odontogramMapper;
    
    @Autowired
    public OdontogramService(OdontogramRepository odontogramRepository, OdontogramMapper odontogramMapper) {
        this.odontogramRepository = odontogramRepository;
        this.odontogramMapper = odontogramMapper;
    }
    
    /**
     * Obtiene todos los odontogramas
     * @return Lista de DTOs de odontogramas
     */
    public Flux<OdontogramDTO> findAll() {
        return odontogramRepository.findAll()
                .map(odontogramMapper::toDTO);
    }
    
    /**
     * Obtiene un odontograma por su ID
     * @param id ID del odontograma
     * @return DTO del odontograma o error si no existe
     */
    public Mono<OdontogramDTO> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del odontograma no puede estar vacío"));
        }
        
        OdontogramId odontogramId = OdontogramId.of(id);
        return odontogramRepository.findById(odontogramId)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("No se encontró el odontograma con ID " + id)))
                .map(odontogramMapper::toDTO);
    }
    
    /**
     * Obtiene un odontograma por el ID del paciente
     * @param patientId ID del paciente
     * @return DTO del odontograma o error si no existe
     */
    public Mono<OdontogramDTO> findByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del paciente no puede estar vacío"));
        }
        
        PatientId id = PatientId.of(patientId);
        return odontogramRepository.findByPatientId(id)
                .switchIfEmpty(Mono.error(new PatientNotFoundException("No se encontró el odontograma para el paciente con ID " + patientId)))
                .map(odontogramMapper::toDTO);
    }
    
    /**
     * Crea un nuevo odontograma para un paciente
     * @param patientId ID del paciente
     * @return DTO del odontograma creado
     */
    public Mono<OdontogramDTO> createForPatient(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del paciente no puede estar vacío"));
        }
        
        PatientId id = PatientId.of(patientId);
        
        // Verificar si ya existe un odontograma para este paciente
        return odontogramRepository.existsByPatientId(id)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalStateException("Ya existe un odontograma para el paciente con ID " + patientId));
                    } else {
                        // Crear un nuevo odontograma
                        Odontogram odontogram = new Odontogram(id);
                        return odontogramRepository.save(odontogram);
                    }
                })
                .map(odontogramMapper::toDTO);
    }
    
    /**
     * Añade una lesión al odontograma
     * @param patientId ID del paciente
     * @param toothId ID del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return DTO del odontograma actualizado
     */
    public Mono<OdontogramDTO> addLesion(String patientId, String toothId, String faceCode, String lesionTypeStr) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del paciente no puede estar vacío"));
        }
        
        if (toothId == null || toothId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del diente no puede estar vacío"));
        }
        
        if (faceCode == null || faceCode.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El código de cara no puede estar vacío"));
        }
        
        if (lesionTypeStr == null || lesionTypeStr.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El tipo de lesión no puede estar vacío"));
        }
        
        try {
            // Convertir código de cara a enumeración
            ToothFace face = ToothFace.fromCodigo(faceCode);
            
            // Convertir tipo de lesión a enumeración
            LesionType lesionType = LesionType.valueOf(lesionTypeStr);
            
            PatientId id = PatientId.of(patientId);
            
            // Obtener, modificar y guardar el odontograma
            return odontogramRepository.findByPatientId(id)
                    .switchIfEmpty(Mono.error(new PatientNotFoundException("No se encontró el odontograma para el paciente con ID " + patientId)))
                    .flatMap(odontogram -> {
                        // Crear copia histórica antes de modificar
                        return odontogramRepository.createHistoricalCopy(odontogram.getId())
                                .then(Mono.just(odontogram));
                    })
                    .flatMap(odontogram -> {
                        // Añadir la lesión
                        odontogram.addLesion(toothId, face, lesionType);
                        
                        // Guardar el odontograma actualizado
                        return odontogramRepository.save(odontogram);
                    })
                    .map(odontogramMapper::toDTO);
            
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Valor inválido: " + e.getMessage()));
        }
    }
    
    /**
     * Elimina una lesión del odontograma
     * @param patientId ID del paciente
     * @param toothId ID del diente
     * @param face Cara del diente
     * @return DTO del odontograma actualizado
     */
    public Mono<OdontogramDTO> removeLesion(String patientId, String toothId, String faceCode) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del paciente no puede estar vacío"));
        }
        
        if (toothId == null || toothId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del diente no puede estar vacío"));
        }
        
        if (faceCode == null || faceCode.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El código de cara no puede estar vacío"));
        }
        
        try {
            // Convertir código de cara a enumeración
            ToothFace face = ToothFace.fromCodigo(faceCode);
            PatientId id = PatientId.of(patientId);
            
            // Obtener, modificar y guardar el odontograma
            return odontogramRepository.findByPatientId(id)
                    .switchIfEmpty(Mono.error(new PatientNotFoundException("No se encontró el odontograma para el paciente con ID " + patientId)))
                    .flatMap(odontogram -> {
                        // Crear copia histórica antes de modificar
                        return odontogramRepository.createHistoricalCopy(odontogram.getId())
                                .then(Mono.just(odontogram));
                    })
                    .flatMap(odontogram -> {
                        // Eliminar la lesión
                        odontogram.removeLesion(toothId, face);
                        
                        // Guardar el odontograma actualizado
                        return odontogramRepository.save(odontogram);
                    })
                    .map(odontogramMapper::toDTO);
            
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Valor inválido: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene el historial de versiones de un odontograma
     * @param patientId ID del paciente
     * @return Lista de DTOs de versiones históricas del odontograma
     */
    public Flux<OdontogramDTO> getHistory(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("El ID del paciente no puede estar vacío"));
        }
        
        PatientId id = PatientId.of(patientId);
        return odontogramRepository.findHistoryByPatientId(id)
                .map(odontogramMapper::toDTO);
    }
    
    /**
     * Obtiene una versión histórica específica de un odontograma
     * @param patientId ID del paciente
     * @param version Versión histórica
     * @return DTO de la versión histórica del odontograma
     */
    public Mono<OdontogramDTO> getHistoricalVersion(String patientId, String version) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del paciente no puede estar vacío"));
        }
        
        if (version == null || version.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("La versión no puede estar vacía"));
        }
        
        PatientId id = PatientId.of(patientId);
        return odontogramRepository.findHistoricalByPatientIdAndVersion(id, version)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró la versión histórica " + version + " para el paciente con ID " + patientId)))
                .map(odontogramMapper::toDTO);
    }
} 