package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Record demonstrating Java 16+ record feature
 * Immutable data carrier for workout sessions
 */
public record WorkoutSession(
    String sessionId,
    LocalDate date,
    WorkoutType type,
    List<Exercise> exercises,
    int totalDuration,
    String notes
) {
    // Compact constructor for validation
    public WorkoutSession {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (totalDuration < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        // Defensive copying for mutable list
        exercises = List.copyOf(exercises);
    }

    // Additional methods
    public double calculateTotalCalories() {
        return exercises.stream()
            .mapToDouble(Exercise::calculateCaloriesBurned)
            .sum();
    }

    public int getExerciseCount() {
        return exercises.size();
    }

    public String getSummary() {
        return String.format("Session %s on %s: %s - %d exercises, %d min, %.0f cal", 
            sessionId, date, type, getExerciseCount(), totalDuration, calculateTotalCalories());
    }
}


