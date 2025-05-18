package odoonto.domain.model.entities;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.DuplicateLesionException;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.ToothNumber;
import odoonto.domain.model.valueobjects.ToothPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa un diente individual en el odontograma.
 * Mantiene el control de las lesiones por cara y los tratamientos aplicados.
 */
public class Tooth {
    private final String id;
    private final ToothPosition position;
    private final Map<String, Lesion> lesions; // Mapa de lesiones por cara dental
    private final List<Treatment> treatments; // Tratamientos aplicados al diente
    
    /**
     * Constructor para un diente
     * @param id Identificador del diente según la nomenclatura universal (por ejemplo: "11", "36")
     */
    public Tooth(String id) {
        validateToothId(id);
        this.id = id;
        this.position = calculatePosition(id);
        this.lesions = new HashMap<>();
        this.treatments = new ArrayList<>();
    }
    
    /**
     * Determina la posición del diente basado en su número
     */
    private ToothPosition calculatePosition(String id) {
        int toothNumber = Integer.parseInt(id);
        
        // Primer dígito indica el cuadrante
        int quadrant = toothNumber / 10;
        
        switch (quadrant) {
            case 1: return ToothPosition.UPPER_RIGHT;
            case 2: return ToothPosition.UPPER_LEFT;
            case 3: return ToothPosition.LOWER_LEFT;
            case 4: return ToothPosition.LOWER_RIGHT;
            case 5: return ToothPosition.UPPER_RIGHT_TEMPORARY; // Dentición temporal
            case 6: return ToothPosition.UPPER_LEFT_TEMPORARY;
            case 7: return ToothPosition.LOWER_LEFT_TEMPORARY;
            case 8: return ToothPosition.LOWER_RIGHT_TEMPORARY;
            default: throw new DomainException("Cuadrante dental no válido: " + quadrant);
        }
    }
    
    /**
     * Valida que el ID del diente sea válido
     */
    private void validateToothId(String toothId) {
        if (toothId == null || toothId.trim().isEmpty()) {
            throw new DomainException("El ID del diente no puede estar vacío");
        }
        
        try {
            int id = Integer.parseInt(toothId);
            
            // Validar dentición permanente (11-48)
            boolean validPermanent = id >= 11 && id <= 48;
            
            // Validar dentición temporal (51-85)
            boolean validTemporary = id >= 51 && id <= 85;
            
            if (!validPermanent && !validTemporary) {
                throw new DomainException("ID de diente fuera de rango: " + toothId);
            }
        } catch (NumberFormatException e) {
            throw new DomainException("ID de diente debe ser un número: " + toothId);
        }
    }
    
    /**
     * Añade una lesión a una cara específica del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @throws DuplicateLesionException Si ya existe una lesión en esa cara
     */
    public void addLesion(ToothFace face, LesionType lesionType) {
        String faceCode = face.getCodigo();
        
        // Validar que la cara sea apropiada para el tipo de diente
        validateToothFace(face);
        
        // Verificar si ya existe una lesión en esa cara
        if (lesions.containsKey(faceCode)) {
            throw new DuplicateLesionException(id, faceCode);
        }
        
        // Crear y añadir la lesión
        Lesion lesion = new Lesion(face, lesionType);
        lesions.put(faceCode, lesion);
    }
    
    /**
     * Valida que la cara dental sea apropiada para este diente específico
     * @param face Cara dental a validar
     * @throws DomainException Si la cara no es válida para este tipo de diente
     */
    private void validateToothFace(ToothFace face) {
        ToothNumber toothNumber = new ToothNumber(id);
        boolean isAnterior = toothNumber.isAnterior();
        
        // Utilizamos el método isApplicable de ToothFace que ya tiene la lógica
        if (!face.isApplicable(isAnterior)) {
            if (isAnterior) {
                throw new DomainException("La cara " + face.name() + " no es aplicable a dientes anteriores (incisivos y caninos)");
            } else {
                throw new DomainException("La cara " + face.name() + " no es aplicable a dientes posteriores (premolares y molares)");
            }
        }
    }
    
    /**
     * Elimina la lesión de una cara específica
     * @param face Cara del diente
     */
    public void removeLesion(ToothFace face) {
        // También validamos aquí para mantener la consistencia
        validateToothFace(face);
        lesions.remove(face.getCodigo());
    }
    
    /**
     * Añade un tratamiento al diente
     * @param treatment Tratamiento a añadir
     */
    public void addTreatment(Treatment treatment) {
        treatments.add(treatment);
    }
    
    /**
     * Obtiene el ID del diente
     * @return ID del diente
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtiene la posición del diente
     * @return Posición del diente
     */
    public ToothPosition getPosition() {
        return position;
    }
    
    /**
     * Obtiene las lesiones por cara
     * @return Mapa de lesiones por cara
     */
    public Map<String, Lesion> getLesions() {
        return lesions;
    }
    
    /**
     * Obtiene los tratamientos aplicados al diente
     * @return Lista de tratamientos
     */
    public List<Treatment> getTreatments() {
        return treatments;
    }
    
    /**
     * Verifica si el diente tiene lesiones
     * @return true si tiene al menos una lesión
     */
    public boolean hasLesions() {
        return !lesions.isEmpty();
    }
    
    /**
     * Verifica si el diente ha recibido tratamiento
     * @return true si tiene al menos un tratamiento
     */
    public boolean hasTreatments() {
        return !treatments.isEmpty();
    }
} 