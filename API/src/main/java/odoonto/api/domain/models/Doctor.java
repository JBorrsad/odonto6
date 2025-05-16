// src/main/java/odoonto/api/domain/models/Doctor.java
package odoonto.api.domain.models;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.*;
import java.util.UUID;

@Document(collectionName = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    /** 1) @Builder.Default para que el builder auto-genere un UUID si no lo pasas.
     2) @DocumentId para que Firestore lo reconozca como clave. */
    @Builder.Default
    @DocumentId
    private String id = UUID.randomUUID().toString();

    private String nombreCompleto;
    private String especialidad;

    /** Constructor de conveniencia, también genera un UUID */
    public Doctor(String nombreCompleto, String especialidad) {
        this.id = UUID.randomUUID().toString();
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }
}
