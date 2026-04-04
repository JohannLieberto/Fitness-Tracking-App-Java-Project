package model;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Immutable DTO summarising a single day's workout data.
 * Demonstrates: Records (OOP2 Fundamentals)
 */
public record WorkoutSummary(
        LocalDate date,
        int totalCalories,
        Duration totalDuration
) {
    /** Compact canonical constructor — validation before field assignment */
    public WorkoutSummary {
        if (totalCalories < 0) throw new IllegalArgumentException("Calories cannot be negative");
        if (totalDuration == null) throw new IllegalArgumentException("Duration must not be null");
    }

    /** Convenience factory */
    public static WorkoutSummary of(LocalDate date, int calories, long minutes) {
        return new WorkoutSummary(date, calories, Duration.ofMinutes(minutes));
    }

    @Override
    public String toString() {
        return String.format("WorkoutSummary[date=%s, calories=%d, duration=%dm]",
                date, totalCalories, totalDuration.toMinutes());
    }
}
