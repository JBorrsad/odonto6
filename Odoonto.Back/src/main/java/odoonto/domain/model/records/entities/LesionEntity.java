package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.LesionType;
import odoonto.domain.model.records.valueobjects.ToothFaceValue;

@Entity
public class LesionEntity {
    private final LesionType lesionType;
    private final ToothFaceValue toothFace;
    private final String description;

    public LesionEntity(final LesionType lesionType,
                       final ToothFaceValue toothFace,
                       final String description) {
        this.lesionType = lesionType;
        this.toothFace = toothFace;
        this.description = description;
    }

    public LesionType getLesionType() {
        return lesionType;
    }

    public ToothFaceValue getToothFace() {
        return toothFace;
    }

    public String getDescription() {
        return description;
    }
} 