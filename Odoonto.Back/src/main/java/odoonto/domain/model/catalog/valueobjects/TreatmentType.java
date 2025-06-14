package odoonto.domain.model.catalog.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class TreatmentType {
    
    public static final TreatmentType PREVENTIVE = new TreatmentType("PREVENTIVE", "Tratamientos Preventivos");
    public static final TreatmentType RESTORATIVE = new TreatmentType("RESTORATIVE", "Tratamientos Restauradores");
    public static final TreatmentType ENDODONTIC = new TreatmentType("ENDODONTIC", "Endodoncia");
    public static final TreatmentType PERIODONTIC = new TreatmentType("PERIODONTIC", "Periodoncia");
    public static final TreatmentType ORTHODONTIC = new TreatmentType("ORTHODONTIC", "Ortodoncia");
    public static final TreatmentType PROSTHETIC = new TreatmentType("PROSTHETIC", "Prótesis");
    public static final TreatmentType SURGICAL = new TreatmentType("SURGICAL", "Cirugía Oral");
    public static final TreatmentType PEDIATRIC = new TreatmentType("PEDIATRIC", "Odontología Pediátrica");
    public static final TreatmentType COSMETIC = new TreatmentType("COSMETIC", "Estética Dental");
    public static final TreatmentType DIAGNOSTIC = new TreatmentType("DIAGNOSTIC", "Diagnóstico");
    
    private final String code;
    private final String description;

    private TreatmentType(final String code, final String description) {
        validateParameters(code, description);
        this.code = code;
        this.description = description;
    }

    public static TreatmentType of(final String code, final String description) {
        return new TreatmentType(code, description);
    }

    public static TreatmentType fromCode(final String code) {
        validateCode(code);
        
        return switch (code.toUpperCase()) {
            case "PREVENTIVE" -> PREVENTIVE;
            case "RESTORATIVE" -> RESTORATIVE;
            case "ENDODONTIC" -> ENDODONTIC;
            case "PERIODONTIC" -> PERIODONTIC;
            case "ORTHODONTIC" -> ORTHODONTIC;
            case "PROSTHETIC" -> PROSTHETIC;
            case "SURGICAL" -> SURGICAL;
            case "PEDIATRIC" -> PEDIATRIC;
            case "COSMETIC" -> COSMETIC;
            case "DIAGNOSTIC" -> DIAGNOSTIC;
            default -> throw new IllegalArgumentException("Unknown treatment type code: " + code);
        };
    }

    public boolean isPreventive() {
        return this.equals(PREVENTIVE);
    }

    public boolean isRestorative() {
        return this.equals(RESTORATIVE);
    }

    public boolean isEndodontic() {
        return this.equals(ENDODONTIC);
    }

    public boolean isPeriodontic() {
        return this.equals(PERIODONTIC);
    }

    public boolean isOrthodontic() {
        return this.equals(ORTHODONTIC);
    }

    public boolean isProsthetic() {
        return this.equals(PROSTHETIC);
    }

    public boolean isSurgical() {
        return this.equals(SURGICAL);
    }

    public boolean isPediatric() {
        return this.equals(PEDIATRIC);
    }

    public boolean isCosmetic() {
        return this.equals(COSMETIC);
    }

    public boolean isDiagnostic() {
        return this.equals(DIAGNOSTIC);
    }

    public boolean requiresSpecialist() {
        return isEndodontic() || isPeriodontic() || isOrthodontic() || 
               isProsthetic() || isSurgical();
    }

    public boolean isBasicTreatment() {
        return isPreventive() || isDiagnostic();
    }

    private static void validateParameters(final String code, final String description) {
        validateCode(code);
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment type description cannot be null or empty");
        }
    }

    private static void validateCode(final String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment type code cannot be null or empty");
        }
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentType that = (TreatmentType) other;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return description + " (" + code + ")";
    }
} 