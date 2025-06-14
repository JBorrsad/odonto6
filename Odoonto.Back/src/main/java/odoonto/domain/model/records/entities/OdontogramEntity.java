package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.OdontogramId;
import odoonto.domain.model.records.valueobjects.DentitionType;

import java.util.List;

@Entity
public class OdontogramEntity {
    private final OdontogramId odontogramId;
    private final DentitionType dentitionType;
    private final List<ToothEntity> teeth;

    public OdontogramEntity(final OdontogramId odontogramId,
                           final DentitionType dentitionType,
                           final List<ToothEntity> teeth) {
        this.odontogramId = odontogramId;
        this.dentitionType = dentitionType;
        this.teeth = List.copyOf(teeth);
    }

    public OdontogramId getOdontogramId() {
        return odontogramId;
    }

    public DentitionType getDentitionType() {
        return dentitionType;
    }

    public List<ToothEntity> getTeeth() {
        return teeth;
    }
} 