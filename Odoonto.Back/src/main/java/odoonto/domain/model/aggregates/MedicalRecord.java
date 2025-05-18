package odoonto.domain.model.aggregates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Agregado MedicalRecord (Historial Médico)
 * Representa el historial médico de un paciente, incluyendo sus tratamientos,
 * diagnósticos y otras anotaciones clínicas.
 */
public class MedicalRecord {
    private UUID id;
    private UUID patientId;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<MedicalNote> notes;
    private List<Diagnosis> diagnoses;
    private List<Treatment> treatments;

    /**
     * Constructor para crear un nuevo historial médico
     * @param patientId ID del paciente al que pertenece este historial
     */
    public MedicalRecord(UUID patientId) {
        this.id = UUID.randomUUID();
        this.patientId = patientId;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = this.createdAt;
        this.notes = new ArrayList<>();
        this.diagnoses = new ArrayList<>();
        this.treatments = new ArrayList<>();
    }
    
    /**
     * Constructor para reconstruir un historial médico desde el repositorio
     */
    public MedicalRecord(UUID id, UUID patientId, LocalDateTime createdAt, 
                         LocalDateTime lastUpdatedAt, List<MedicalNote> notes,
                         List<Diagnosis> diagnoses, List<Treatment> treatments) {
        this.id = id;
        this.patientId = patientId;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.notes = notes != null ? notes : new ArrayList<>();
        this.diagnoses = diagnoses != null ? diagnoses : new ArrayList<>();
        this.treatments = treatments != null ? treatments : new ArrayList<>();
    }

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public List<MedicalNote> getNotes() {
        return notes;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    // Métodos para modificar el historial
    public void addNote(MedicalNote note) {
        this.notes.add(note);
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void addDiagnosis(Diagnosis diagnosis) {
        this.diagnoses.add(diagnosis);
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void addTreatment(Treatment treatment) {
        this.treatments.add(treatment);
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Clase interna para notas médicas
     */
    public static class MedicalNote {
        private UUID id;
        private String content;
        private LocalDateTime createdAt;
        private UUID doctorId;

        public MedicalNote(String content, UUID doctorId) {
            this.id = UUID.randomUUID();
            this.content = content;
            this.createdAt = LocalDateTime.now();
            this.doctorId = doctorId;
        }

        // Getters
        public UUID getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public UUID getDoctorId() {
            return doctorId;
        }
    }

    /**
     * Clase interna para diagnósticos
     */
    public static class Diagnosis {
        private UUID id;
        private String description;
        private LocalDateTime createdAt;
        private UUID doctorId;

        public Diagnosis(String description, UUID doctorId) {
            this.id = UUID.randomUUID();
            this.description = description;
            this.createdAt = LocalDateTime.now();
            this.doctorId = doctorId;
        }

        // Getters
        public UUID getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public UUID getDoctorId() {
            return doctorId;
        }
    }

    /**
     * Clase interna para tratamientos
     */
    public static class Treatment {
        private UUID id;
        private String description;
        private LocalDateTime prescriptionDate;
        private UUID doctorId;
        private boolean completed;

        public Treatment(String description, UUID doctorId) {
            this.id = UUID.randomUUID();
            this.description = description;
            this.prescriptionDate = LocalDateTime.now();
            this.doctorId = doctorId;
            this.completed = false;
        }

        // Getters y setters
        public UUID getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getPrescriptionDate() {
            return prescriptionDate;
        }

        public UUID getDoctorId() {
            return doctorId;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    // Método toString para facilitar la depuración
    @Override
    public String toString() {
        return "MedicalRecord{" +
               "id=" + id +
               ", patientId=" + patientId +
               ", notes=" + notes.size() +
               ", diagnoses=" + diagnoses.size() +
               ", treatments=" + treatments.size() +
               '}';
    }
} 