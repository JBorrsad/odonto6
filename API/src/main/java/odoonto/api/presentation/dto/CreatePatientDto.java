// src/main/java/odoonto/api/presentation/dto/CreatePatientDto.java
package odoonto.api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odoonto.api.domain.core.valueobjects.Sexo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientDto {
    private String nombre;
    private String apellido;
    private String fechaNacimiento; // formato ISO8601: "1990-05-12T00:00:00Z"
    private Sexo sexo;
    private String telefono;
    private String email;
    private int edad;
}
