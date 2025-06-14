package odoonto.domain.model.staff.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class StaffRole {
    
    public static final StaffRole DENTIST = new StaffRole("DENTIST");
    public static final StaffRole HYGIENIST = new StaffRole("HYGIENIST");
    public static final StaffRole RECEPTIONIST = new StaffRole("RECEPTIONIST");
    
    private final String roleName;

    private StaffRole(final String roleName) {
        validateRoleName(roleName);
        this.roleName = roleName;
    }

    public static StaffRole of(final String roleName) {
        validateRoleName(roleName);
        
        return switch (roleName.toUpperCase()) {
            case "DENTIST" -> DENTIST;
            case "HYGIENIST" -> HYGIENIST;
            case "RECEPTIONIST" -> RECEPTIONIST;
            default -> throw new IllegalArgumentException("Unknown staff role: " + roleName);
        };
    }

    public boolean isDentist() {
        return "DENTIST".equals(roleName);
    }

    public boolean isHygienist() {
        return "HYGIENIST".equals(roleName);
    }

    public boolean isReceptionist() {
        return "RECEPTIONIST".equals(roleName);
    }

    private static void validateRoleName(final String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final StaffRole staffRole = (StaffRole) other;
        return Objects.equals(roleName, staffRole.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    @Override
    public String toString() {
        return roleName;
    }
} 