// src/main/java/odoonto/api/domain/models/Odontogram.java
package odoonto.api.domain.models;

import lombok.*;
import odoonto.api.domain.core.valueobjects.LesionType;
import odoonto.api.domain.core.valueobjects.ToothFace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Odontogram {

    /** Ahora es solo un VO, sin @Document ni id propio */
    @Builder.Default
    private Map<String, ToothRecord> teeth = new HashMap<>();

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
    public Odontogram(int age) {
        this();
        int[] ids = age <= 12 ? TEMPORARY_IDS : PERMANENT_IDS;
        for (int toothId : ids) {
            this.teeth.put(String.valueOf(toothId), new ToothRecord());
        }
    }

    /** Añade o actualiza una lesión en la cara dada */
    public void addLesion(String toothId, ToothFace face, LesionType lesion) {
        teeth
                .computeIfAbsent(toothId, k -> new ToothRecord())
                .addLesion(face.name(), lesion);
    }

    /** Elimina la lesión de una cara específica de un diente */
    public void removeLesion(String toothId, ToothFace face) {
        ToothRecord rec = teeth.get(toothId);
        if (rec != null) rec.removeLesion(face.name());
    }

    /** Devuelve un mapa inmutable de todos los registros por diente */
    public Map<String, ToothRecord> getTeeth() {
        return Collections.unmodifiableMap(teeth);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToothRecord {
        @Builder.Default
        private Map<String, LesionType> faces = new HashMap<>();

        public void addLesion(String face, LesionType lesion) {
            faces.put(face, lesion);
        }

        public void removeLesion(String face) {
            faces.remove(face);
        }

        public Map<String, LesionType> getFaces() {
            return Collections.unmodifiableMap(faces);
        }
    }
}
