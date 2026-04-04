package model;

import java.time.LocalDate;

/**
 * WorkoutSession — an immutable record (OOP2: Records).
 *
 * Record components auto-generate:
 *   - final private fields
 *   - canonical constructor
 *   - accessor methods: sessionId(), userId(), date(), workoutType(),
 *                       durationMinutes(), caloriesBurned(), notes()
 *   - equals(), hashCode(), toString()
 */
public record WorkoutSession(
        String sessionId,
        String userId,
        LocalDate date,
        WorkoutType workoutType,
        int durationMinutes,
        int caloriesBurned,
        String notes
) {

    // Compact constructor — OOP2: flexible constructor bodies (JEP 513 style)
    public WorkoutSession {
        if (durationMinutes <= 0)
            throw new IllegalArgumentException("Duration must be positive");
        if (caloriesBurned < 0)
            throw new IllegalArgumentException("Calories cannot be negative");
        if (sessionId == null || sessionId.isBlank())
            throw new IllegalArgumentException("Session ID cannot be blank");
        notes = (notes == null) ? "" : notes.trim();
    }

    /** Custom method: calories as double for Comparator.comparingDouble */
    public double calculateTotalCalories() {
        return caloriesBurned;
    }

    /** Convenience: total duration as long */
    public long totalDuration() {
        return durationMinutes;
    }

    @Override
    public String toString() {
        return String.format("Session %s on %s: %s - %d min, %d cal%s",
                sessionId, date, workoutType.getDisplayName(),
                durationMinutes, caloriesBurned,
                notes.isEmpty() ? "" : " (" + notes + ")");
    }
}
