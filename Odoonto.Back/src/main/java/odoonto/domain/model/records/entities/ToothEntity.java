package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.records.valueobjects.DentitionType;

import java.util.List;

@Entity
public class ToothEntity {
    private final ToothNumber toothNumber;
    private final DentitionType dentitionType;
    private final List<LesionEntity> lesions;

    public ToothEntity(final ToothNumber toothNumber,
                      final DentitionType dentitionType,
                      final List<LesionEntity> lesions) {
        this.toothNumber = toothNumber;
        this.dentitionType = dentitionType;
        this.lesions = List.copyOf(lesions);
    }

    public ToothNumber getToothNumber() {
        return toothNumber;
    }

    public DentitionType getDentitionType() {
        return dentitionType;
    }

    public List<LesionEntity> getLesions() {
        return lesions;
    }
} 