package odoonto.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLesionsDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LesionUpdate {
        private String toothId;
        private String face;
        private String lesion;
    }
    /** lista de actualizaciones: cada una pone/modifica una lesión */
    private List<LesionUpdate> updates;
}
