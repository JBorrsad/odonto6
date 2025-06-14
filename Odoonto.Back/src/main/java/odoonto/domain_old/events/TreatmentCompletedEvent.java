package odoonto.domain.events;

import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;

import java.util.List;

/**
 * Evento que se dispara cuando se completa un tratamiento dental.
 */
public class TreatmentCompletedEvent extends DomainEvent {
    private final String treatmentId;
    private final String patientId;
    private final String doctorId;
    private final String toothId;
    private final List<ToothFace> treatedFaces;
    private final LesionType lesionTreated;
    private final String treatmentDescription;
    private final String observations;
    
    public TreatmentCompletedEvent(String treatmentId, String patientId, String doctorId,
                                  String toothId, List<ToothFace> treatedFaces,
                                  LesionType lesionTreated, String treatmentDescription,
                                  String observations) {
        super();
        this.treatmentId = treatmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.toothId = toothId;
        this.treatedFaces = treatedFaces;
        this.lesionTreated = lesionTreated;
        this.treatmentDescription = treatmentDescription;
        this.observations = observations;
    }
    
    public String getTreatmentId() {
        return treatmentId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public String getToothId() {
        return toothId;
    }
    
    public List<ToothFace> getTreatedFaces() {
        return treatedFaces;
    }
    
    public LesionType getLesionTreated() {
        return lesionTreated;
    }
    
    public String getTreatmentDescription() {
        return treatmentDescription;
    }
    
    public String getObservations() {
        return observations;
    }
    
    @Override
    public String toString() {
        return "TreatmentCompletedEvent{" +
                "treatmentId='" + treatmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", toothId='" + toothId + '\'' +
                ", treatedFaces=" + treatedFaces +
                ", lesionTreated=" + lesionTreated +
                ", treatmentDescription='" + treatmentDescription + '\'' +
                ", observations='" + observations + '\'' +
                '}';
    }
} 