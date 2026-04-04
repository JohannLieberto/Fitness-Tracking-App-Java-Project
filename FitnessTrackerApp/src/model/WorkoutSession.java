package model;

import java.time.LocalDate;
import java.util.List;

/**
 * WorkoutSession — an immutable record (OOP2: Records).
 *
 * Record components generate:
 *   - final private fields
 *   - canonical constructor
 *   - accessor methods: sessionId(), userId(), date(), workoutType(),
 *                       durationMinutes(), caloriesBurned(), notes()
 *   - equals(), hashCode(), toString()
 *
 * calculateTotalCalories() is a custom instance method added on top.
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
    // Validation runs before field assignment is complete.
    public WorkoutSession {
        if (durationMinutes <= 0)
            throw new IllegalArgumentException("Duration must be positive");
        if (caloriesBurned < 0)
            throw new IllegalArgumentException("Calories cannot be negative");
        if (sessionId == null || sessionId.isBlank())
            throw new IllegalArgumentException("Session ID cannot be blank");
        // Normalise notes: treat null as empty string
        notes = (notes == null) ? "" : notes.trim();
    }

    /**
     * Custom method: returns calories as a double for compatibility
     * with Comparator.comparingDouble and stream operations.
     */
    public double calculateTotalCalories() {
        return caloriesBurned;
    }

    /**
     * Convenience: total duration as long (for mapToLong stream ops).
     */
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
