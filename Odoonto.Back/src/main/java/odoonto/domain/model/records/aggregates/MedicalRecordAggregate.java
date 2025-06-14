package odoonto.domain.model.records.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.events.records.MedicalRecordCreatedEvent;
import odoonto.domain.events.records.TreatmentAddedEvent;
import odoonto.domain.events.shared.DomainEvents;
import odoonto.domain.model.records.entities.OdontogramEntity;
import odoonto.domain.model.records.entities.TreatmentEntity;
import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.model.records.valueobjects.TimestampValue;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.patients.valueobjects.PatientId;

import java.util.List;
import java.util.ArrayList;

@AggregateRoot
public class MedicalRecordAggregate {
    private final MedicalRecordId recordId;
    private final PatientId patientId;
    private final TimestampValue createdAt;
    private final OdontogramEntity odontogram;
    private final List<TreatmentEntity> treatments;

    public MedicalRecordAggregate(final MedicalRecordId recordId, 
                                 final PatientId patientId,
                                 final TimestampValue createdAt,
                                 final OdontogramEntity odontogram,
                                 final List<TreatmentEntity> treatments) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.createdAt = createdAt;
        this.odontogram = odontogram;
        this.treatments = new ArrayList<>(treatments);
    }

    public void createRecord() {
        DomainEvents.raise(MedicalRecordCreatedEvent.create(
            this.recordId,
            this.patientId
        ));
    }

    public void addTreatment(final TreatmentEntity treatment) {
        this.treatments.add(treatment);
        DomainEvents.raise(TreatmentAddedEvent.create(
            this.recordId,
            treatment.getTreatmentId()
        ));
    }

    public MedicalRecordId getRecordId() {
        return recordId;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public TimestampValue getCreatedAt() {
        return createdAt;
    }

    public OdontogramEntity getOdontogram() {
        return odontogram;
    }

    public List<TreatmentEntity> getTreatments() {
        return treatments;
    }
} 