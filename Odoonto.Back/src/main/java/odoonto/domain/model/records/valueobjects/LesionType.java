package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class LesionType {
    
    public static final LesionType CARIES = new LesionType("CARIES", "Caries", "Lesión por bacterias que destruye el esmalte");
    public static final LesionType FRACTURE = new LesionType("FRACTURE", "Fractura", "Ruptura o rotura del diente");
    public static final LesionType WEAR = new LesionType("WEAR", "Desgaste", "Pérdida de estructura dental por uso");
    public static final LesionType EROSION = new LesionType("EROSION", "Erosión", "Pérdida de esmalte por ácidos");
    public static final LesionType ABSCESS = new LesionType("ABSCESS", "Absceso", "Infección con acumulación de pus");
    public static final LesionType PULPITIS = new LesionType("PULPITIS", "Pulpitis", "Inflamación de la pulpa dental");
    public static final LesionType PERIAPICAL = new LesionType("PERIAPICAL", "Lesión Periapical", "Infección en la raíz del diente");
    public static final LesionType GINGIVITIS = new LesionType("GINGIVITIS", "Gingivitis", "Inflamación de las encías");
    public static final LesionType PERIODONTITIS = new LesionType("PERIODONTITIS", "Periodontitis", "Enfermedad periodontal avanzada");
    public static final LesionType STAIN = new LesionType("STAIN", "Mancha", "Decoloración del diente");
    
    private final String code;
    private final String name;
    private final String description;

    private LesionType(final String code, final String name, final String description) {
        validateParameters(code, name, description);
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static LesionType of(final String code, final String name, final String description) {
        return new LesionType(code, name, description);
    }

    public static LesionType fromCode(final String code) {
        validateCode(code);
        
        return switch (code.toUpperCase()) {
            case "CARIES" -> CARIES;
            case "FRACTURE" -> FRACTURE;
            case "WEAR" -> WEAR;
            case "EROSION" -> EROSION;
            case "ABSCESS" -> ABSCESS;
            case "PULPITIS" -> PULPITIS;
            case "PERIAPICAL" -> PERIAPICAL;
            case "GINGIVITIS" -> GINGIVITIS;
            case "PERIODONTITIS" -> PERIODONTITIS;
            case "STAIN" -> STAIN;
            default -> throw new IllegalArgumentException("Unknown lesion type code: " + code);
        };
    }

    public boolean isCaries() {
        return this.equals(CARIES);
    }

    public boolean isFracture() {
        return this.equals(FRACTURE);
    }

    public boolean isInfection() {
        return this.equals(ABSCESS) || this.equals(PULPITIS) || this.equals(PERIAPICAL);
    }

    public boolean isPeriodontal() {
        return this.equals(GINGIVITIS) || this.equals(PERIODONTITIS);
    }

    public boolean requiresUrgentTreatment() {
        return this.equals(ABSCESS) || this.equals(PULPITIS) || this.equals(PERIODONTITIS);
    }

    public boolean isPreventable() {
        return this.equals(CARIES) || this.equals(GINGIVITIS) || this.equals(EROSION);
    }

    public boolean affectsEnamel() {
        return this.equals(CARIES) || this.equals(EROSION) || this.equals(WEAR) || this.equals(STAIN);
    }

    public boolean affectsPulp() {
        return this.equals(PULPITIS) || this.equals(ABSCESS);
    }

    public boolean affectsGums() {
        return this.equals(GINGIVITIS) || this.equals(PERIODONTITIS);
    }

    private static void validateParameters(final String code, final String name, final String description) {
        validateCode(code);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lesion type name cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Lesion type description cannot be null or empty");
        }
    }

    private static void validateCode(final String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Lesion type code cannot be null or empty");
        }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final LesionType that = (LesionType) other;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
} 