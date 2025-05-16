package odoonto.api.domain.models;

import com.google.cloud.spring.data.firestore.Document;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;
import odoonto.api.domain.core.valueobjects.EmailAddress;
import odoonto.api.domain.core.valueobjects.PhoneNumber;
import odoonto.api.domain.core.valueobjects.Sexo;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collectionName = "patients")
public class Patient {
    /**
     * 1) @Builder.Default para que el builder auto-genere un UUID si no lo pasas.
     * 2) @DocumentId para que Firestore lo reconozca como clave.
     */
    @Builder.Default
    @DocumentId
    private String id = UUID.randomUUID().toString();

    private String nombre;
    private String apellido;
    private String fechaNacimiento; // formato ISO8601: "1990-05-12T00:00:00Z"
    private Sexo sexo;
    private PhoneNumber telefono;
    private EmailAddress email;
    
    // Añadido campo age para que Firestore no genere warnings
    private Integer age;

    /** Tu valor-objeto anidado (sin @Document ni @Id, porque vive dentro del paciente) */
    private Odontogram odontogram;

    /** Constructor de conveniencia si no quieres usar el builder */
    public Patient(String nombre,
                   String apellido,
                   Instant fechaNacimiento,
                   Sexo sexo,
                   PhoneNumber telefono,
                   EmailAddress email,
                   int edadParaOdontograma) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento.toString(); // conversión explícita aquí
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
        this.odontogram = new Odontogram(edadParaOdontograma);
        this.age = edadParaOdontograma;
    }

    public int getAge() {
        if (this.age != null) {
            return this.age;
        }
        return java.time.Period.between(
                java.time.LocalDate.ofInstant(Instant.parse(this.fechaNacimiento), java.time.ZoneId.systemDefault()),
                java.time.LocalDate.now()
        ).getYears();
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
}
