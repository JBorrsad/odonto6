package odoonto.application.service.odontogram;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.exceptions.OdontogramNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.application.port.in.odontogram.OdontogramQueryUseCase;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.repository.OdontogramRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del caso de uso para consultar odontogramas
 */
@Service
public class OdontogramQueryService implements OdontogramQueryUseCase {
    
    private final OdontogramRepository odontogramRepository;
    private final OdontogramMapper odontogramMapper;
    
    @Autowired
    public OdontogramQueryService(OdontogramRepository odontogramRepository,
                                 OdontogramMapper odontogramMapper) {
        this.odontogramRepository = odontogramRepository;
        this.odontogramMapper = odontogramMapper;
    }
    
    @Override
    public Optional<OdontogramDTO> findById(String odontogramId) {
        return odontogramRepository.findById(odontogramId)
                .map(odontogramMapper::toDTO)
                .blockOptional();
    }
    
    @Override
    public Optional<OdontogramDTO> findByPatientId(String patientId) {
        return odontogramRepository.findByPatientId(patientId)
                .map(odontogramMapper::toDTO)
                .blockOptional();
    }
    
    @Override
    public List<OdontogramDTO> findAll() {
        return odontogramRepository.findAll()
                .map(odontogramMapper::toDTO)
                .collectList()
                .block();
    }
    
    @Override
    public boolean existsById(String odontogramId) {
        return odontogramRepository.findById(odontogramId)
                .hasElement()
                .block();
    }
    
    @Override
    public boolean existsByPatientId(String patientId) {
        return odontogramRepository.findByPatientId(patientId)
                .hasElement()
                .block();
    }
    
    /**
     * Método auxiliar para obtener un odontograma por ID
     * lanzando excepción si no existe
     * 
     * @param odontogramId ID del odontograma
     * @return Odontograma
     * @throws OdontogramNotFoundException si no existe
     */
    public Odontogram getOdontogramOrThrow(String odontogramId) {
        return odontogramRepository.findById(odontogramId)
                .switchIfEmpty(Mono.error(new OdontogramNotFoundException("No se encontró el odontograma con ID: " + odontogramId)))
                .block();
    }
    
    /**
     * Método auxiliar para obtener un odontograma por ID de paciente
     * lanzando excepción si no existe
     * 
     * @param patientId ID del paciente
     * @return Odontograma
     * @throws OdontogramNotFoundException si no existe
     */
    public Odontogram getOdontogramByPatientIdOrThrow(String patientId) {
        return odontogramRepository.findByPatientId(patientId)
                .switchIfEmpty(Mono.error(new OdontogramNotFoundException("No se encontró odontograma para el paciente con ID: " + patientId)))
                .block();
    }
} 