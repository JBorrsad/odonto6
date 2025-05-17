// src/main/java/odoonto/api/presentation/dto/LesionCommandDto.java
package odoonto.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LesionCommandDto {
    /** Usamos String porque Firestore sólo admite claves de mapa como String */
    private String toothId;
    private String face;
    private String lesion;
}
