package model;

import java.time.LocalDate;

/**
 * WorkoutSummary — a record used as an immutable summary DTO.
 *
 * OOP2: Records (JEP 395)
 * - Auto-generates: private final fields, canonical constructor,
 *   accessor methods, equals(), hashCode(), toString()
 */
public record WorkoutSummary(
        String sessionId,
        LocalDate date,
        WorkoutType workoutType,
        int durationMinutes,
        int caloriesBurned,
        String notes
) {
    // Compact constructor for validation
    public WorkoutSummary {
        if (durationMinutes < 0)
            throw new IllegalArgumentException("Duration cannot be negative");
        if (caloriesBurned < 0)
            throw new IllegalArgumentException("Calories cannot be negative");
        notes = (notes == null) ? "" : notes.trim();
    }

    /** Convenience: calories as double */
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
