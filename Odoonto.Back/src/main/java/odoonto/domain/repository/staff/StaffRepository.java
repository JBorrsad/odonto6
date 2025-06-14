package odoonto.domain.repository.staff;

import odoonto.domain.model.staff.aggregates.StaffAggregate;
import odoonto.domain.model.staff.valueobjects.StaffRole;
import odoonto.domain.model.staff.valueobjects.BusinessHours;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface StaffRepository {
    
    void save(StaffAggregate staff);
    
    Optional<StaffAggregate> findById(String staffId);
    
    Optional<StaffAggregate> findByEmail(String email);
    
    Optional<StaffAggregate> findByEmployeeNumber(String employeeNumber);
    
    List<StaffAggregate> findByRole(StaffRole role);
    
    List<StaffAggregate> findByLastName(String lastName);
    
    List<StaffAggregate> findActiveStaff();
    
    List<StaffAggregate> findInactiveStaff();
    
    List<StaffAggregate> findDentists();
    
    List<StaffAggregate> findHygienists();
    
    List<StaffAggregate> findReceptionists();
    
    List<StaffAggregate> findStaffWorkingOnDay(DayOfWeek dayOfWeek);
    
    List<StaffAggregate> findAvailableStaff(LocalDate date, LocalTime time);
    
    List<StaffAggregate> findStaffWithExtendedHours();
    
    List<StaffAggregate> findPartTimeStaff();
    
    List<StaffAggregate> findFullTimeStaff();
    
    List<StaffAggregate> findRecentlyHiredStaff(int months);
    
    List<StaffAggregate> findStaffByExperienceYears(int minYears);
    
    BusinessHours findStaffSchedule(String staffId);
    
    List<LocalTime> findAvailableSlots(String staffId, LocalDate date);
    
    boolean isStaffAvailable(String staffId, LocalDate date, LocalTime time);
    
    boolean isStaffActive(String staffId);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeNumber(String employeeNumber);
    
    long countTotalStaff();
    
    long countStaffByRole(StaffRole role);
    
    long countActiveStaff();
    
    long countAvailableStaff(LocalDate date);
    
    long countStaffWorkingOnDay(DayOfWeek dayOfWeek);
    
    void delete(String staffId);
} 