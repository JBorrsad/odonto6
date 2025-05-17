package odoonto.application.service.odontogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.exceptions.OdontogramNotFoundException;
import odoonto.application.mapper.OdontogramMapper;
import odoonto.application.port.in.odontogram.LesionAddUseCase;
import odoonto.application.port.out.OdontogramRepositoryPort;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;

/**
 * Implementación del caso de uso para añadir lesiones a un odontograma
 */
@Service
public class LesionAddService implements LesionAddUseCase {

    private final OdontogramRepositoryPort odontogramRepository;
    private final OdontogramMapper odontogramMapper;

    @Autowired
    public LesionAddService(OdontogramRepositoryPort odontogramRepository,
                          OdontogramMapper odontogramMapper) {
        this.odontogramRepository = odontogramRepository;
        this.odontogramMapper = odontogramMapper;
    }

    @Override
    public OdontogramDTO addLesion(String odontogramId, int toothNumber, String face, String lesionType) {
        // Obtener el odontograma
        Odontogram odontogram = odontogramRepository.findById(odontogramId)
                .orElseThrow(() -> new OdontogramNotFoundException("Odontograma no encontrado con ID: " + odontogramId));
        
        // Validar y convertir los datos
        ToothFace toothFace = ToothFace.fromString(face); // Puede lanzar InvalidToothFaceException
        LesionType lesion = LesionType.fromString(lesionType); // Puede lanzar excepciones de dominio
        
        // Añadir la lesión (la lógica de dominio puede lanzar DuplicateLesionException)
        odontogram.addLesion(toothNumber, toothFace, lesion);
        
        // Persistir los cambios
        Odontogram savedOdontogram = odontogramRepository.save(odontogram);
        
        // Convertir a DTO
        return odontogramMapper.toDTO(savedOdontogram);
    }
} 