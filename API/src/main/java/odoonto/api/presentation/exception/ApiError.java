// src/main/java/odoonto/api/presentation/exception/ApiError.java
package odoonto.api.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String timestamp; // formato ISO8601: "2023-05-12T14:30:00Z"
    private String message;
    private String details;
}
