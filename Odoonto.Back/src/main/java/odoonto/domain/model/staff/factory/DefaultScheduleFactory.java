package odoonto.domain.model.staff.factory;

import org.jmolecules.ddd.annotation.Factory;

import odoonto.domain.model.staff.valueobjects.ScheduleValue;
import odoonto.domain.model.scheduling.valueobjects.TimeSlotValue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Factory
public final class DefaultScheduleFactory {

    private DefaultScheduleFactory() {}

    public static ScheduleValue createDefaultWeeklySchedule() {
        final List<TimeSlotValue> slots = new ArrayList<>();
        
        // Monday to Friday, 9:00 AM to 6:00 PM
        for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, 
                                    DayOfWeek.FRIDAY)) {
            
            // Morning session: 9:00 - 12:00
            slots.add(new TimeSlotValue(day, LocalTime.of(9, 0), LocalTime.of(12, 0)));
            
            // Afternoon session: 2:00 - 6:00
            slots.add(new TimeSlotValue(day, LocalTime.of(14, 0), LocalTime.of(18, 0)));
        }
        
        return new ScheduleValue(slots);
    }
} 