package odoonto.domain.model.staff.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@ValueObject
public final class BusinessHours {
    
    private final Map<DayOfWeek, WorkingDay> workingDays;

    private BusinessHours(final Map<DayOfWeek, WorkingDay> workingDays) {
        validateWorkingDays(workingDays);
        this.workingDays = new HashMap<>(workingDays);
    }

    public static BusinessHours standardHours() {
        final Map<DayOfWeek, WorkingDay> standard = new HashMap<>();
        
        // Lunes a Viernes 8:00 - 18:00
        for (final DayOfWeek day : DayOfWeek.values()) {
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                standard.put(day, WorkingDay.of(LocalTime.of(8, 0), LocalTime.of(18, 0)));
            }
        }
        
        return new BusinessHours(standard);
    }

    public static BusinessHours customHours(final Map<DayOfWeek, WorkingDay> workingDays) {
        return new BusinessHours(workingDays);
    }

    public static BusinessHours noWeekends() {
        return standardHours();
    }

    public boolean isWorkingTime(final LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        
        final DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());
        return isWorkingTime(today, time);
    }

    public boolean isWorkingTime(final DayOfWeek dayOfWeek, final LocalTime time) {
        if (dayOfWeek == null || time == null) {
            throw new IllegalArgumentException("Day of week and time cannot be null");
        }
        
        final WorkingDay workingDay = workingDays.get(dayOfWeek);
        return workingDay != null && workingDay.isWithinHours(time);
    }

    public boolean isWorkingDay(final DayOfWeek dayOfWeek) {
        return workingDays.containsKey(dayOfWeek);
    }

    public LocalTime getStartTime(final DayOfWeek dayOfWeek) {
        final WorkingDay workingDay = workingDays.get(dayOfWeek);
        return workingDay != null ? workingDay.getStartTime() : null;
    }

    public LocalTime getEndTime(final DayOfWeek dayOfWeek) {
        final WorkingDay workingDay = workingDays.get(dayOfWeek);
        return workingDay != null ? workingDay.getEndTime() : null;
    }

    private static void validateWorkingDays(final Map<DayOfWeek, WorkingDay> workingDays) {
        if (workingDays == null) {
            throw new IllegalArgumentException("Working days cannot be null");
        }
    }

    public Map<DayOfWeek, WorkingDay> getWorkingDays() {
        return Collections.unmodifiableMap(workingDays);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final BusinessHours that = (BusinessHours) other;
        return Objects.equals(workingDays, that.workingDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workingDays);
    }

    @Override
    public String toString() {
        return "BusinessHours{" + workingDays + "}";
    }

    @ValueObject
    public static final class WorkingDay {
        private final LocalTime startTime;
        private final LocalTime endTime;

        private WorkingDay(final LocalTime startTime, final LocalTime endTime) {
            validateTimes(startTime, endTime);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public static WorkingDay of(final LocalTime startTime, final LocalTime endTime) {
            return new WorkingDay(startTime, endTime);
        }

        public boolean isWithinHours(final LocalTime time) {
            if (time == null) {
                throw new IllegalArgumentException("Time cannot be null");
            }
            return !time.isBefore(startTime) && time.isBefore(endTime);
        }

        private static void validateTimes(final LocalTime startTime, final LocalTime endTime) {
            if (startTime == null || endTime == null) {
                throw new IllegalArgumentException("Start and end times cannot be null");
            }
            if (!startTime.isBefore(endTime)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            final WorkingDay that = (WorkingDay) other;
            return Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startTime, endTime);
        }

        @Override
        public String toString() {
            return startTime + "-" + endTime;
        }
    }
} 