// src/main/java/odoonto/api/presentation/dto/OdontogramDto.java
package odoonto.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class OdontogramDto {
    /** Map<Integer, Map<String, String>> - diente: (cara: tipo lesión) */
    private Map<Integer, Map<String, String>> teeth;
}
