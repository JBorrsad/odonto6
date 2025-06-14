package odoonto.domain.model.records.policy;

import odoonto.domain.model.records.valueobjects.TreatmentType;
import odoonto.domain.model.records.valueobjects.LesionType;
import odoonto.domain.model.records.valueobjects.DentitionType;
import odoonto.domain.model.records.valueobjects.ToothNumber;

import java.util.List;

public final class MedicalRecordPolicy {

    private MedicalRecordPolicy() {}

    public static boolean canAddTreatment(final ToothNumber toothNumber, 
                                         final TreatmentType treatmentType,
                                         final DentitionType dentitionType) {
        return isValidToothForDentition(toothNumber, dentitionType) &&
               isValidTreatmentForTooth(treatmentType, toothNumber);
    }

    public static boolean requiresParentalConsent(final DentitionType dentitionType,
                                                 final TreatmentType treatmentType) {
        return dentitionType == DentitionType.TEMPORARY ||
               (dentitionType == DentitionType.MIXED && requiresConsentForMixedDentition(treatmentType));
    }

    public static boolean isValidToothForDentition(final ToothNumber toothNumber,
                                                  final DentitionType dentitionType) {
        final int number = toothNumber.getValue();
        
        return switch (dentitionType) {
            case TEMPORARY -> number >= 51 && number <= 85;
            case PERMANENT -> number >= 11 && number <= 48;
            case MIXED -> (number >= 11 && number <= 48) || (number >= 51 && number <= 85);
        };
    }

    public static boolean isValidTreatmentForTooth(final TreatmentType treatmentType,
                                                  final ToothNumber toothNumber) {
        final int number = toothNumber.getValue();
        final boolean isTemporary = number >= 51 && number <= 85;
        
        if (isTemporary && treatmentType == TreatmentType.IMPLANT) {
            return false; // No implants in temporary teeth
        }
        
        if (isTemporary && treatmentType == TreatmentType.CROWN) {
            return false; // No crowns in temporary teeth typically
        }
        
        return true;
    }

    public static List<TreatmentType> getRecommendedTreatmentsFor(final LesionType lesionType) {
        return switch (lesionType) {
            case CARIES -> List.of(TreatmentType.FILLING, TreatmentType.CROWN);
            case PULPITIS -> List.of(TreatmentType.ROOT_CANAL, TreatmentType.EXTRACTION);
            case FRACTURE -> List.of(TreatmentType.FILLING, TreatmentType.CROWN, TreatmentType.EXTRACTION);
            case ABSCESS -> List.of(TreatmentType.ROOT_CANAL, TreatmentType.EXTRACTION);
            case GINGIVITIS -> List.of(TreatmentType.CLEANING, TreatmentType.PERIODONTAL);
            case PERIODONTITIS -> List.of(TreatmentType.PERIODONTAL, TreatmentType.EXTRACTION);
        };
    }

    public static boolean requiresSpecialistReferral(final TreatmentType treatmentType) {
        return treatmentType == TreatmentType.IMPLANT ||
               treatmentType == TreatmentType.ORTHODONTICS ||
               treatmentType == TreatmentType.PERIODONTAL;
    }

    public static int getMaximumTreatmentsPerVisit() {
        return 3; // Maximum 3 treatments per appointment
    }

    private static boolean requiresConsentForMixedDentition(final TreatmentType treatmentType) {
        return treatmentType == TreatmentType.EXTRACTION ||
               treatmentType == TreatmentType.ROOT_CANAL ||
               treatmentType == TreatmentType.ORTHODONTICS;
    }
} 