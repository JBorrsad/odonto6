package odoonto.domain.model.records.factory;

import org.jmolecules.ddd.annotation.Factory;

import odoonto.domain.model.records.entities.OdontogramEntity;
import odoonto.domain.model.records.entities.ToothEntity;
import odoonto.domain.model.records.valueobjects.OdontogramId;
import odoonto.domain.model.records.valueobjects.DentitionType;
import odoonto.domain.model.records.valueobjects.ToothNumber;

import java.util.ArrayList;
import java.util.List;

@Factory
public final class OdontogramFactory {
    
    private OdontogramFactory() {}

    public static OdontogramEntity createForDentitionType(final DentitionType dentitionType) {
        final OdontogramId odontogramId = new OdontogramId(java.util.UUID.randomUUID().toString());
        final List<ToothEntity> teeth = createTeethForDentition(dentitionType);
        
        return new OdontogramEntity(odontogramId, dentitionType, teeth);
    }

    private static List<ToothEntity> createTeethForDentition(final DentitionType dentitionType) {
        final List<ToothEntity> teeth = new ArrayList<>();
        
        switch (dentitionType) {
            case TEMPORARY:
                createTemporaryTeeth(teeth);
                break;
            case PERMANENT:
                createPermanentTeeth(teeth);
                break;
            case MIXED:
                createMixedTeeth(teeth);
                break;
        }
        
        return teeth;
    }

    private static void createTemporaryTeeth(final List<ToothEntity> teeth) {
        for (int i = 51; i <= 85; i++) {
            if (isValidTemporaryTooth(i)) {
                teeth.add(new ToothEntity(
                    new ToothNumber(i), 
                    DentitionType.TEMPORARY, 
                    new ArrayList<>()
                ));
            }
        }
    }

    private static void createPermanentTeeth(final List<ToothEntity> teeth) {
        for (int i = 11; i <= 48; i++) {
            if (isValidPermanentTooth(i)) {
                teeth.add(new ToothEntity(
                    new ToothNumber(i), 
                    DentitionType.PERMANENT, 
                    new ArrayList<>()
                ));
            }
        }
    }

    private static void createMixedTeeth(final List<ToothEntity> teeth) {
        createPermanentTeeth(teeth);
        createTemporaryTeeth(teeth);
    }

    private static boolean isValidTemporaryTooth(final int number) {
        return (number >= 51 && number <= 55) || 
               (number >= 61 && number <= 65) ||
               (number >= 71 && number <= 75) || 
               (number >= 81 && number <= 85);
    }

    private static boolean isValidPermanentTooth(final int number) {
        return (number >= 11 && number <= 18) || 
               (number >= 21 && number <= 28) ||
               (number >= 31 && number <= 38) || 
               (number >= 41 && number <= 48);
    }
} 