package odoonto.application.service.odontogram;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.exceptions.OdontogramNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.application.port.in.odontogram.OdontogramQueryUseCase;
import odoonto.application.port.out.ReactiveOdontogramRepository;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.PatientId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementación del caso de uso para consultar odontogramas
 */
@Service
public class OdontogramQueryService implements OdontogramQueryUseCase {
    
    private final ReactiveOdontogramRepository odontogramRepository;
    private final OdontogramMapper odontogramMapper;
    
    @Autowired
    public OdontogramQueryService(ReactiveOdontogramRepository odontogramRepository,
                                 OdontogramMapper odontogramMapper) {
        this.odontogramRepository = odontogramRepository;
        this.odontogramMapper = odontogramMapper;
    }
    
    @Override
    public Mono<OdontogramDTO> findById(String odontogramId) {
        return odontogramRepository.findById(odontogramId)
                .map(odontogramMapper::toDTO);
    }
    
    @Override
    public Mono<OdontogramDTO> findByPatientId(String patientId) {
        return odontogramRepository.findByPatientId(patientId)
                .map(odontogramMapper::toDTO);
    }
    
    @Override
    public Flux<OdontogramDTO> findAll() {
        return odontogramRepository.findAll()
                .map(odontogramMapper::toDTO);
    }
    
    @Override
    public Mono<Boolean> existsById(String odontogramId) {
        return odontogramRepository.findById(odontogramId)
                .hasElement();
    }
    
    @Override
    public Mono<Boolean> existsByPatientId(String patientId) {
        return odontogramRepository.existsByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método auxiliar para obtener un odontograma por ID
     * lanzando excepción si no existe
     * 
     * @param odontogramId ID del odontograma
     * @return Mono con el odontograma
     */
    public Mono<Odontogram> getOdontogramOrThrow(String odontogramId) {
        return odontogramRepository.findById(odontogramId)
                .switchIfEmpty(Mono.error(new OdontogramNotFoundException("No se encontró el odontograma con ID: " + odontogramId)));
    }
    
    /**
     * Método auxiliar para obtener un odontograma por ID de paciente
     * lanzando excepción si no existe
     * 
     * @param patientId ID del paciente
     * @return Mono con el odontograma
     */
    public Mono<Odontogram> getOdontogramByPatientIdOrThrow(String patientId) {
        return odontogramRepository.findByPatientId(patientId)
                .switchIfEmpty(Mono.error(new OdontogramNotFoundException("No se encontró odontograma para el paciente con ID: " + patientId)));
    }
} 