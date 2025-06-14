package odoonto.domain.service.staff;

import org.jmolecules.ddd.annotation.Service;

import odoonto.domain.model.staff.valueobjects.ScheduleValue;
import odoonto.domain.model.staff.valueobjects.SpecialtyValue;
import odoonto.domain.model.scheduling.valueobjects.TimeSlotValue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleGeneratorService {

    public ScheduleValue generateScheduleForSpecialty(final SpecialtyValue specialty) {
        return switch (specialty) {
            case GENERAL_DENTISTRY -> generateGeneralSchedule();
            case ORTHODONTICS -> generateOrthodonticsSchedule();
            case ORAL_SURGERY -> generateSurgerySchedule();
            case PEDIATRIC_DENTISTRY -> generatePediatricSchedule();
            case ENDODONTICS, PERIODONTICS, PROSTHODONTICS, ORAL_PATHOLOGY -> generateSpecialtySchedule();
        };
    }

    public ScheduleValue generateCustomSchedule(final List<DayOfWeek> workingDays,
                                               final LocalTime startTime,
                                               final LocalTime endTime,
                                               final LocalTime lunchStart,
                                               final LocalTime lunchEnd) {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        for (DayOfWeek day : workingDays) {
            // Morning session
            if (startTime.isBefore(lunchStart)) {
                slots.add(new TimeSlotValue(day, startTime, lunchStart));
            }
            
            // Afternoon session
            if (lunchEnd.isBefore(endTime)) {
                slots.add(new TimeSlotValue(day, lunchEnd, endTime));
            }
        }
        
        return new ScheduleValue(slots);
    }

    private ScheduleValue generateGeneralSchedule() {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        // Monday to Friday, 8:00 AM to 6:00 PM
        for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, 
                                    DayOfWeek.FRIDAY)) {
            slots.add(new TimeSlotValue(day, LocalTime.of(8, 0), LocalTime.of(12, 0)));
            slots.add(new TimeSlotValue(day, LocalTime.of(13, 0), LocalTime.of(18, 0)));
        }
        
        return new ScheduleValue(slots);
    }

    private ScheduleValue generateOrthodonticsSchedule() {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        // Extended hours for orthodontics
        for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, 
                                    DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)) {
            slots.add(new TimeSlotValue(day, LocalTime.of(9, 0), LocalTime.of(13, 0)));
            slots.add(new TimeSlotValue(day, LocalTime.of(14, 0), LocalTime.of(19, 0)));
        }
        
        return new ScheduleValue(slots);
    }

    private ScheduleValue generateSurgerySchedule() {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        // Surgery typically morning hours
        for (DayOfWeek day : List.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
                                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            slots.add(new TimeSlotValue(day, LocalTime.of(7, 0), LocalTime.of(12, 0)));
        }
        
        return new ScheduleValue(slots);
    }

    private ScheduleValue generatePediatricSchedule() {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        // Child-friendly hours
        for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, 
                                    DayOfWeek.FRIDAY)) {
            slots.add(new TimeSlotValue(day, LocalTime.of(9, 0), LocalTime.of(12, 0)));
            slots.add(new TimeSlotValue(day, LocalTime.of(15, 0), LocalTime.of(18, 0)));
        }
        
        return new ScheduleValue(slots);
    }

    private ScheduleValue generateSpecialtySchedule() {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        // Standard specialty hours
        for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)) {
            slots.add(new TimeSlotValue(day, LocalTime.of(8, 0), LocalTime.of(12, 0)));
            slots.add(new TimeSlotValue(day, LocalTime.of(14, 0), LocalTime.of(17, 0)));
        }
        
        return new ScheduleValue(slots);
    }
} 