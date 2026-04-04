package model;

import java.time.LocalDate;

/**
 * WorkoutSummary — immutable summary DTO record.
 * OOP2: Records (JEP 395)
 */
public record WorkoutSummary(
        String sessionId,
        LocalDate date,
        WorkoutType workoutType,
        int durationMinutes,
        int caloriesBurned,
        String notes
) {
    public WorkoutSummary {
        if (durationMinutes < 0)
            throw new IllegalArgumentException("Duration cannot be negative");
        if (caloriesBurned < 0)
            throw new IllegalArgumentException("Calories cannot be negative");
        notes = (notes == null) ? "" : notes.trim();
    }

    /** Convenience factory — creates a summary from date + totals (used in WorkoutService.showRecords) */
    public static WorkoutSummary of(LocalDate date, int totalCalories, int totalMinutes) {
        return new WorkoutSummary("SUMMARY", date, WorkoutType.CARDIO, totalMinutes, totalCalories, "");
    }

    /** Calories as double */
    public double totalCalories() {
        return caloriesBurned;
    }

    @Override
    public String toString() {
        return String.format("Session %s on %s: %s - %d min, %d cal%s",
                sessionId, date, workoutType.getDisplayName(),
                durationMinutes, caloriesBurned,
                notes.isEmpty() ? "" : " (" + notes + ")");
    }
}
