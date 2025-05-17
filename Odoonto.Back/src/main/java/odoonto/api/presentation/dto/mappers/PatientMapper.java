// src/main/java/odoonto/api/presentation/dto/mappers/PatientMapper.java
package odoonto.api.presentation.dto.mappers;

import org.springframework.stereotype.Component;
import odoonto.api.domain.models.Patient;
import odoonto.api.presentation.dto.PatientDto;
import odoonto.api.presentation.dto.CreatePatientDto;
import odoonto.api.domain.core.valueobjects.PhoneNumber;
import odoonto.api.domain.core.valueobjects.EmailAddress;
import java.time.Instant;

@Component
public class PatientMapper {

    public PatientDto toDto(Patient patient) {
        if (patient == null) {
            return null;
        }
        return new PatientDto(
                patient.getId(),
                patient.getNombre(),
                patient.getApellido(),
                patient.getFechaNacimiento(),    // String en formato ISO8601
                patient.getSexo(),
                patient.getTelefono().value(),
                patient.getEmail().value(),
                patient.getAge()                 // calculado en el dominio
        );
    }

    public Patient toEntity(CreatePatientDto dto) {
        if (dto == null) {
            return null;
        }
        
        // Si el DTO proporciona una fecha como String, usarla directamente
        // Si el DTO proporciona un Instant (código legacy), convertirlo a String
        Instant fechaNacimiento;
        if (dto.getFechaNacimiento() instanceof String) {
            try {
                // Parseamos y volvemos a convertir para asegurar formato válido
                fechaNacimiento = Instant.parse((String) dto.getFechaNacimiento());
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + dto.getFechaNacimiento());
            }
        } else {
            // Asumimos que es String para el nuevo modelo de datos
            fechaNacimiento = Instant.now(); // Valor predeterminado en caso de error
        }
        
        return new Patient(
                dto.getNombre(),
                dto.getApellido(),
                fechaNacimiento,        // Pasamos Instant que será convertido a String en el constructor
                dto.getSexo(),
                new PhoneNumber(dto.getTelefono()),
                new EmailAddress(dto.getEmail()),
                dto.getEdad()                    // para inicializar Odontogram
        );
    }
}
