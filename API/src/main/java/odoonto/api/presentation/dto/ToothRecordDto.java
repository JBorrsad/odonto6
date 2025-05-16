// src/main/java/odoonto/api/presentation/dto/ToothRecordDto.java
package odoonto.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ToothRecordDto {
    /** Map<String,String> porque serializamos todo como String */
    private Map<String, String> faces;
}
