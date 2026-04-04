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
 * - Comparator.comparing / thenComparing / reversed (OOP2: sorting)
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
    public void addWorkoutToUser(String userId, WorkoutSession session)
            throws InvalidWorkoutException {
        ValidationService.validateWorkoutSession(session);
        var user = users.get(userId);
        if (user == null) {
            throw new InvalidWorkoutException("User not found: " + userId);
        }
        user.addWorkoutSession(session);
    }

    // ----------------------------------------------------------------
    // SORTING — Comparator.comparing, thenComparing, reversed
    //
    // Fix: avoid chaining .reversed() after comparingInt() — Java loses
    // the concrete type parameter at that point and cannot infer
    // ToIntFunction<? super T> for the next comparingInt() call.
    // Solution: use explicit lambda comparators (a, b) -> ... for
    // tie-breakers so no type inference is required.
    // ----------------------------------------------------------------

    /**
     * Returns the user's workout history sorted by the given key.
     *
     * DATE     → newest first; tie-break: longest duration first
     * DURATION → longest first; tie-break: newest date first
     * CALORIES → most calories first; tie-break: newest date first
     *
     * Demonstrates:
     *   - Comparator.comparing with method reference
     *   - .reversed() for descending order
     *   - .thenComparing() with explicit lambda for secondary sort key
     */
    public List<WorkoutSession> getWorkoutsSortedBy(String userId, WorkoutSortKey key) {
        var user = users.get(userId);
        if (user == null) return Collections.emptyList();

        Comparator<WorkoutSession> comparator = switch (key) {
            case DATE ->
                // Primary: newest date first
                // Tie-break: longest duration first — lambda avoids type-inference issue
                Comparator.comparing(WorkoutSession::date)
                          .reversed()
                          .thenComparing((a, b) -> Integer.compare(b.durationMinutes(), a.durationMinutes()));

            case DURATION ->
                // Primary: longest duration first
                // Tie-break: newest date first
                Comparator.<WorkoutSession>comparingInt(WorkoutSession::durationMinutes)
                          .reversed()
                          .thenComparing((a, b) -> b.date().compareTo(a.date()));

            case CALORIES ->
                // Primary: most calories first
                // Tie-break: newest date first
                Comparator.<WorkoutSession>comparingDouble(WorkoutSession::calculateTotalCalories)
                          .reversed()
                          .thenComparing((a, b) -> b.date().compareTo(a.date()));
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
