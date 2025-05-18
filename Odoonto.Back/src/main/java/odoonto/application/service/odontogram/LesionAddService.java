package odoonto.application.service.odontogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.exceptions.OdontogramNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.application.port.in.odontogram.LesionAddUseCase;
import odoonto.application.port.out.ReactiveOdontogramRepository;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para añadir lesiones a un odontograma
 */
@Service
public class LesionAddService implements LesionAddUseCase {

    private final ReactiveOdontogramRepository odontogramRepository;
    private final OdontogramMapper odontogramMapper;

    @Autowired
    public LesionAddService(ReactiveOdontogramRepository odontogramRepository,
                          OdontogramMapper odontogramMapper) {
        this.odontogramRepository = odontogramRepository;
        this.odontogramMapper = odontogramMapper;
    }

    @Override
    public OdontogramDTO addLesion(String odontogramId, int toothNumber, String face, String lesionType) {
        // Obtener el odontograma
        return odontogramRepository.findById(odontogramId)
                .switchIfEmpty(Mono.error(new OdontogramNotFoundException("Odontograma no encontrado con ID: " + odontogramId)))
                .flatMap(odontogram -> {
                    // Validar y convertir los datos
                    ToothFace toothFace = ToothFace.fromCodigo(face); // Usar el método correcto
                    LesionType lesion = LesionType.valueOf(lesionType); // Usar valueOf para enum
                    
                    // Añadir la lesión (la lógica de dominio puede lanzar DuplicateLesionException)
                    odontogram.addLesion(String.valueOf(toothNumber), toothFace, lesion);
                    
                    // Persistir los cambios
                    return odontogramRepository.save(odontogram);
                })
                .map(odontogramMapper::toDTO)
                .block(); // Bloqueamos para mantener compatibilidad con el caso de uso síncrono
    }
} 