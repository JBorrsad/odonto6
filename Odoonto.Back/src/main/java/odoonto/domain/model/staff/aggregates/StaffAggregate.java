package odoonto.domain.model.staff.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.model.staff.valueobjects.StaffId;
import odoonto.domain.model.staff.valueobjects.StaffRole;
import odoonto.domain.model.staff.valueobjects.BusinessHours;
import odoonto.domain.model.shared.valueobjects.PersonName;
import odoonto.domain.model.shared.valueobjects.EmailAddress;
import odoonto.domain.model.shared.valueobjects.PhoneNumber;
import odoonto.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.time.LocalTime;

@AggregateRoot
public class StaffAggregate {
    
    private final StaffId staffId;
    private PersonName name;
    private EmailAddress emailAddress;
    private PhoneNumber phoneNumber;
    private StaffRole primaryRole;
    private final Set<StaffRole> secondaryRoles;
    private BusinessHours businessHours;
    private final Set<String> permissions;
    private boolean isActive;

    public StaffAggregate(final PersonName name,
                         final EmailAddress emailAddress,
                         final PhoneNumber phoneNumber,
                         final StaffRole primaryRole) {
        validateConstructorParameters(name, emailAddress, phoneNumber, primaryRole);
        
        this.staffId = StaffId.generate();
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.primaryRole = primaryRole;
        this.secondaryRoles = new HashSet<>();
        this.businessHours = BusinessHours.standardHours();
        this.permissions = new HashSet<>();
        this.isActive = true;
        
        assignDefaultPermissions();
    }

    private StaffAggregate(final StaffId staffId,
                          final PersonName name,
                          final EmailAddress emailAddress,
                          final PhoneNumber phoneNumber,
                          final StaffRole primaryRole,
                          final Set<StaffRole> secondaryRoles,
                          final BusinessHours businessHours,
                          final Set<String> permissions,
                          final boolean isActive) {
        this.staffId = staffId;
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.primaryRole = primaryRole;
        this.secondaryRoles = new HashSet<>(secondaryRoles);
        this.businessHours = businessHours;
        this.permissions = new HashSet<>(permissions);
        this.isActive = isActive;
    }

    public static StaffAggregate reconstituteFromPersistence(
            final StaffId staffId,
            final PersonName name,
            final EmailAddress emailAddress,
            final PhoneNumber phoneNumber,
            final StaffRole primaryRole,
            final Set<StaffRole> secondaryRoles,
            final BusinessHours businessHours,
            final Set<String> permissions,
            final boolean isActive) {
        
        validateReconstitutionParameters(staffId, name, emailAddress, phoneNumber, primaryRole, businessHours);
        
        return new StaffAggregate(staffId, name, emailAddress, phoneNumber, primaryRole,
                                 secondaryRoles != null ? secondaryRoles : new HashSet<>(),
                                 businessHours, 
                                 permissions != null ? permissions : new HashSet<>(),
                                 isActive);
    }

    public void assignRole(final StaffRole newRole) {
        if (newRole == null) {
            throw new DomainException("Role cannot be null");
        }
        
        if (newRole.equals(primaryRole)) {
            return;
        }
        
        secondaryRoles.add(newRole);
        updatePermissionsForRole(newRole);
    }

    public void removeSecondaryRole(final StaffRole roleToRemove) {
        if (roleToRemove != null && !roleToRemove.equals(primaryRole)) {
            secondaryRoles.remove(roleToRemove);
            removePermissionsForRole(roleToRemove);
        }
    }

    public void updateBusinessHours(final BusinessHours newBusinessHours) {
        if (newBusinessHours == null) {
            throw new DomainException("Business hours cannot be null");
        }
        this.businessHours = newBusinessHours;
    }

    public void updateContactInfo(final EmailAddress newEmailAddress,
                                 final PhoneNumber newPhoneNumber) {
        if (newEmailAddress != null) {
            this.emailAddress = newEmailAddress;
        }
        if (newPhoneNumber != null) {
            this.phoneNumber = newPhoneNumber;
        }
    }

    public void updatePersonalInfo(final PersonName newName) {
        if (newName != null) {
            this.name = newName;
        }
    }



    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean hasRole(final StaffRole role) {
        return primaryRole.equals(role) || secondaryRoles.contains(role);
    }



    public boolean isWorkingAt(final LocalTime time) {
        return isActive && businessHours.isWorkingTime(time);
    }

    private void assignDefaultPermissions() {
        // Simplified - no permissions logic
    }

    private void updatePermissionsForRole(final StaffRole role) {
        // Simplified - no permissions logic  
    }

    private void removePermissionsForRole(final StaffRole role) {
        // Simplified - no permissions logic
    }

    private static void validateConstructorParameters(final PersonName name,
                                                     final EmailAddress emailAddress,
                                                     final PhoneNumber phoneNumber,
                                                     final StaffRole primaryRole) {
        if (name == null) {
            throw new DomainException("Staff name cannot be null");
        }
        if (emailAddress == null) {
            throw new DomainException("Staff email cannot be null");
        }
        if (phoneNumber == null) {
            throw new DomainException("Staff phone number cannot be null");
        }
        if (primaryRole == null) {
            throw new DomainException("Primary role cannot be null");
        }
    }

    private static void validateReconstitutionParameters(final StaffId staffId,
                                                        final PersonName name,
                                                        final EmailAddress emailAddress,
                                                        final PhoneNumber phoneNumber,
                                                        final StaffRole primaryRole,
                                                        final BusinessHours businessHours) {
        if (staffId == null) {
            throw new DomainException("Staff ID cannot be null");
        }
        if (businessHours == null) {
            throw new DomainException("Business hours cannot be null");
        }
        validateConstructorParameters(name, emailAddress, phoneNumber, primaryRole);
    }

    public StaffId getStaffId() {
        return staffId;
    }

    public PersonName getName() {
        return name;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public StaffRole getPrimaryRole() {
        return primaryRole;
    }

    public Set<StaffRole> getSecondaryRoles() {
        return Collections.unmodifiableSet(secondaryRoles);
    }

    public BusinessHours getBusinessHours() {
        return businessHours;
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final StaffAggregate that = (StaffAggregate) other;
        return Objects.equals(staffId, that.staffId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId);
    }
} 