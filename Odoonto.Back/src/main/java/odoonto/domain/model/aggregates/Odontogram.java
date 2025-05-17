// src/main/java/odoonto/domain/model/aggregates/Odontogram.java
package odoonto.domain.model.aggregates;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.DuplicateLesionException;
import odoonto.domain.exceptions.InvalidToothFaceException;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Odontograma - Representa el registro dental completo de un paciente.
 * Es un agregado independiente pero relacionado con Patient mediante identidad derivada.
 * El ID de un odontograma se deriva del ID del paciente.
 */
public class Odontogram {

    /**
     * Identificador único del odontograma, derivado del ID del paciente
     */
    private OdontogramId id;

    /** 
     * Mapa de dientes indexado por su número.
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
     * Constructor por defecto. 
     * Para uso interno o frameworks.
     */
    public Odontogram() {
        this.teeth = new HashMap<>();
    }

    /**
     * Constructor para crear un nuevo odontograma asociado a un paciente.
     * @param patientId ID del paciente al que pertenece este odontograma
     */
    public Odontogram(PatientId patientId) {
        if (patientId == null) {
            throw new DomainException("El ID del paciente no puede ser nulo");
        }
        this.id = OdontogramId.fromPatientId(patientId);
        this.teeth = new HashMap<>();
    }

    /**
     * Constructor completo para reconstrucción
     * @param id ID del odontograma
     * @param teeth Mapa de dientes con sus registros
     */
    public Odontogram(OdontogramId id, Map<String, ToothRecord> teeth) {
        if (id == null) {
            throw new DomainException("El ID del odontograma no puede ser nulo");
        }
        this.id = id;
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
     * Obtiene el ID del odontograma
     * @return ID del odontograma
     */
    public OdontogramId getId() {
        return id;
    }

    /**
     * Establece el ID del odontograma
     * @param id ID del odontograma
     */
    public void setId(OdontogramId id) {
        this.id = id;
    }

    /**
     * Extrae el ID del paciente asociado con este odontograma
     * @return ID del paciente o null si el ID no es derivado
     */
    public PatientId extractPatientId() {
        return id != null ? id.extractPatientId() : null;
    }

    /**
     * Obtiene el valor del ID como String
     * @return Valor del ID como String o null si es nulo
     */
    public String getIdValue() {
        return id != null ? id.getValue() : null;
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
