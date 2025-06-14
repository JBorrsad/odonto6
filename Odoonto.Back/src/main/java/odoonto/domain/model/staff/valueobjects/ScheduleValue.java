package odoonto.domain.model.staff.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import odoonto.domain.model.scheduling.valueobjects.TimeSlotValue;

import java.util.List;
import java.util.Objects;

@ValueObject
public final class ScheduleValue {
    private final List<TimeSlotValue> weeklySlots;

    public ScheduleValue(final List<TimeSlotValue> weeklySlots) {
        if (weeklySlots == null || weeklySlots.isEmpty()) {
            throw new IllegalArgumentException("Schedule must have at least one time slot");
        }
        this.weeklySlots = List.copyOf(weeklySlots);
    }

    public List<TimeSlotValue> getWeeklySlots() {
        return weeklySlots;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ScheduleValue that = (ScheduleValue) obj;
        return Objects.equals(weeklySlots, that.weeklySlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weeklySlots);
    }
} 