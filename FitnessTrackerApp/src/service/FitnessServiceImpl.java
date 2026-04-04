package service;

import model.*;
import exception.InvalidWorkoutException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of FitnessService demonstrating:
 * - Inheritance (implements interface)
 * - Polymorphism
 * - Lambdas and Predicate
 * - Method references
 * - Comparator.comparing / thenComparing / reversed  (OOP2: sorting)
 * - Switch expressions (standard, no preview features required)
 * - Pattern matching via instanceof (Java 16+, standard)
 */
public class FitnessServiceImpl implements FitnessService {

    // ----------------------------------------------------------------
    // Enum used as a sort key — clean alternative to magic strings
    // ----------------------------------------------------------------
    public enum WorkoutSortKey { DATE, DURATION, CALORIES }

    private final Map<String, User> users;

    public FitnessServiceImpl() {
        this.users = new HashMap<>();
    }

    @Override
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public User getUser(String userId) {
        return users.get(userId);
    }

    @Override
    public void addWorkoutToUser(String userId, WorkoutSession session) throws InvalidWorkoutException {
        ValidationService.validateWorkoutSession(session);
        var user = users.get(userId);
        if (user == null) {
            throw new InvalidWorkoutException("User not found: " + userId);
        }
        user.addWorkoutSession(session);
    }

    // ----------------------------------------------------------------
    // SORTING — Comparator.comparing, thenComparing, reversed
    // ----------------------------------------------------------------

    /**
     * Returns the user's workout history sorted by the given key.
     *
     * DATE     → newest first   (Comparator.comparing(WorkoutSession::date).reversed())
     * DURATION → longest first  (thenComparing used as tie-breaker on date)
     * CALORIES → highest first  (thenComparing used as tie-breaker on date)
     *
     * Demonstrates:
     *   - Comparator.comparing with method reference
     *   - .reversed() for descending order
     *   - .thenComparing() for secondary sort key
     */
    public List<WorkoutSession> getWorkoutsSortedBy(String userId, WorkoutSortKey key) {
        var user = users.get(userId);
        if (user == null) return Collections.emptyList();

        Comparator<WorkoutSession> comparator = switch (key) {
            case DATE ->
                // Primary: newest date first; tie-break by duration (longest first)
                Comparator.comparing(WorkoutSession::date)
                          .reversed()
                          .thenComparing(
                              Comparator.comparingInt(WorkoutSession::durationMinutes).reversed());

            case DURATION ->
                // Primary: longest session first; tie-break by date (newest first)
                Comparator.comparingInt(WorkoutSession::durationMinutes)
                          .reversed()
                          .thenComparing(
                              Comparator.comparing(WorkoutSession::date).reversed());

            case CALORIES ->
                // Primary: most calories first; tie-break by date (newest first)
                Comparator.comparingDouble(WorkoutSession::calculateTotalCalories)
                          .reversed()
                          .thenComparing(
                              Comparator.comparing(WorkoutSession::date).reversed());
        };

        return user.getWorkoutHistory().stream()
                   .sorted(comparator)
                   .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // Lambdas and Predicate
    // ----------------------------------------------------------------
    @Override
    public List<WorkoutSession> filterWorkouts(String userId, Predicate<WorkoutSession> criteria) {
        var user = users.get(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getWorkoutHistory().stream()
            .filter(criteria)
            .collect(Collectors.toList());
    }

    @Override
    public double calculateUserProgress(String userId) {
        var user = users.get(userId);
        if (user == null) return 0.0;
        return user.getTotalCaloriesBurned();
    }

    public List<String> getUserNames() {
        return users.values().stream()
            .map(User::getName)
            .sorted()
            .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // Switch expression on enum — no preview needed
    // ----------------------------------------------------------------
    public String getWorkoutRecommendation(WorkoutType type) {
        return switch (type) {
            case CARDIO      -> "Great for heart health! Aim for 150 minutes per week.";
            case STRENGTH    -> "Build muscle and boost metabolism. Train 3-4 times weekly.";
            case HIIT        -> "Maximum calorie burn! Keep sessions under 30 minutes.";
            case YOGA        -> "Perfect for flexibility and stress relief. Practice daily if possible.";
            case FLEXIBILITY -> "Essential for injury prevention. Stretch after every workout.";
        };
    }

    /**
     * Pattern matching with instanceof (Java 16+, fully released — no preview flags needed).
     */
    public String analyzeExercise(Exercise exercise) {
        if (exercise instanceof CardioExercise cardio) {
            return String.format("Cardio workout: %s covering %.2f km",
                cardio.getName(), cardio.getDistance());
        } else if (exercise instanceof StrengthExercise strength) {
            return String.format("Strength training: %s with %.1f kg",
                strength.getName(), strength.getWeight());
        } else if (exercise == null) {
            return "No exercise provided";
        } else {
            return "Unknown exercise type: " + exercise.getName();
        }
    }
}
