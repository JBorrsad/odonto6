package odoonto.domain.events;

/**
 * Evento que se dispara cuando se registra un nuevo paciente en el sistema.
 */
public class PatientRegisteredEvent extends DomainEvent {
    private final String patientId;
    private final String name;
    private final String lastName;
    private final String documentNumber;
    private final String email;
    
    public PatientRegisteredEvent(String patientId, String name, String lastName, 
                                 String documentNumber, String email) {
        super();
        this.patientId = patientId;
        this.name = name;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.email = email;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return "PatientRegisteredEvent{" +
                "patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
} 