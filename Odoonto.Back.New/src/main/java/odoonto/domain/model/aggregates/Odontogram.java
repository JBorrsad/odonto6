// src/main/java/odoonto/domain/model/aggregates/Odontogram.java
package odoonto.domain.model.aggregates;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.DuplicateLesionException;
import odoonto.domain.exceptions.InvalidToothFaceException;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.LesionType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Odontograma - Objeto de valor complejo que representa el estado dental del paciente.
 * Forma parte del agregado Patient y no tiene una identidad independiente.
 * 
 * IMPORTANTE: Este objeto de valor se gestiona exclusivamente a través del agregado
 * Patient. No debe tener un identificador propio ni persistirse de forma independiente.
 */
public class Odontogram {

    /** 
     * Mapa de dientes indexado por su número, sin identificador propio.
     * El ciclo de vida del odontograma está ligado al del paciente.
     */
    private Map<String, ToothRecord> teeth;

    private static final int[] PERMANENT_IDS = {
            11,12,13,14,15,16,17,18,
            21,22,23,24,25,26,27,28,
            31,32,33,34,35,36,37,38,
            41,42,43,44,45,46,47,48
    };

    private static final int[] TEMPORARY_IDS = {
            51,52,53,54,55,
            61,62,63,64,65,
            71,72,73,74,75,
            81,82,83,84,85
    };

    /**
     * Constructor de conveniencia.
     * Inicializa el mapa de dientes usando claves String.
     */
    public Odontogram() {
        this.teeth = new HashMap<>();
    }

    /**
     * Constructor para reconstrucción
     * @param teeth Mapa de dientes con sus registros
     */
    public Odontogram(Map<String, ToothRecord> teeth) {
        this.teeth = teeth != null ? teeth : new HashMap<>();
    }

    /**
     * Añade una lesión a un diente específico
     * @param toothId ID del diente (ej: "11", "48")
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @throws InvalidToothFaceException Si la cara no es válida
     * @throws DuplicateLesionException Si ya existe una lesión en esa cara
     */
    public void addLesion(String toothId, ToothFace face, LesionType lesionType) {
        validateToothId(toothId);
        
        // Obtener o crear el registro del diente
        ToothRecord toothRecord = teeth.computeIfAbsent(toothId, k -> new ToothRecord());
        
        // Verificar si ya existe una lesión en esa cara
        if (toothRecord.getFaces().containsKey(face.getCodigo())) {
            throw new DuplicateLesionException(toothId, face.getCodigo());
        }
        
        // Añadir la lesión
        toothRecord.getFaces().put(face.getCodigo(), lesionType);
    }

    /**
     * Elimina una lesión de un diente específico
     * @param toothId ID del diente
     * @param face Cara del diente
     * @throws InvalidToothFaceException Si la cara no es válida
     */
    public void removeLesion(String toothId, ToothFace face) {
        validateToothId(toothId);
        
        // Si existe el diente en el mapa
        if (teeth.containsKey(toothId)) {
            ToothRecord toothRecord = teeth.get(toothId);
            // Eliminar la lesión (si existe)
            toothRecord.getFaces().remove(face.getCodigo());
            
            // Si el diente ya no tiene lesiones, se elimina del mapa
            if (toothRecord.getFaces().isEmpty()) {
                teeth.remove(toothId);
            }
        }
    }

    /**
     * Obtiene todas las lesiones de un diente
     * @param toothId ID del diente
     * @return Mapa de caras con sus lesiones, o mapa vacío si no tiene lesiones
     */
    public Map<String, LesionType> getLesionesForTooth(String toothId) {
        validateToothId(toothId);
        
        if (teeth.containsKey(toothId)) {
            return teeth.get(toothId).getFaces();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Valida que el ID del diente sea válido
     * @param toothId ID del diente
     * @throws DomainException Si el ID no es válido
     */
    private void validateToothId(String toothId) {
        if (toothId == null || toothId.trim().isEmpty()) {
            throw new DomainException("El ID del diente no puede estar vacío");
        }
        
        // Los ID de dientes deben ser números entre 11-48
        try {
            int id = Integer.parseInt(toothId);
            if (id < 11 || id > 48) {
                throw new DomainException("ID de diente fuera de rango: " + toothId);
            }
        } catch (NumberFormatException e) {
            throw new DomainException("ID de diente debe ser un número: " + toothId);
        }
    }

    /**
     * Obtiene el mapa completo de dientes
     * @return Mapa de dientes con sus registros
     */
    public Map<String, ToothRecord> getTeeth() {
        return Collections.unmodifiableMap(teeth);
    }

    /**
     * Establece el mapa completo de dientes
     * @param teeth Mapa de dientes con sus registros
     */
    public void setTeeth(Map<String, ToothRecord> teeth) {
        this.teeth = teeth != null ? teeth : new HashMap<>();
    }

    /**
     * Clase interna que representa el registro de un diente individual
     */
    public static class ToothRecord {
        private Map<String, LesionType> faces;
        
        public ToothRecord() {
            this.faces = new HashMap<>();
        }
        
        public ToothRecord(Map<String, LesionType> faces) {
            this.faces = faces != null ? faces : new HashMap<>();
        }
        
        public Map<String, LesionType> getFaces() {
            return Collections.unmodifiableMap(faces);
        }
        
        public void setFaces(Map<String, LesionType> faces) {
            this.faces = faces;
        }
    }
}
