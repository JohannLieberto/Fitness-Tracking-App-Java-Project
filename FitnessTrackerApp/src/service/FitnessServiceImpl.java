package service;

import model.*;
import exception.InvalidWorkoutException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of FitnessService demonstrating:
 *  - Comparator.comparing / thenComparing / reversed  (OOP2: sorting)
 *  - Lambdas: Predicate, method references
 *  - Switch expressions on enum  (standard, no preview needed)
 *  - Pattern matching instanceof  (Java 16+, standard)
 */
public class FitnessServiceImpl implements FitnessService {

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
    // WorkoutSession is now a record so date(), durationMinutes(),
    // calculateTotalCalories() are all valid accessor / instance methods.
    // ----------------------------------------------------------------
    public List<WorkoutSession> getWorkoutsSortedBy(String userId, WorkoutSortKey key) {
        var user = users.get(userId);
        if (user == null) return Collections.emptyList();

        Comparator<WorkoutSession> comparator = switch (key) {
            case DATE ->
                Comparator.comparing(WorkoutSession::date)
                          .reversed()
                          .thenComparing((a, b) ->
                              Integer.compare(b.durationMinutes(), a.durationMinutes()));

            case DURATION ->
                Comparator.<WorkoutSession>comparingInt(WorkoutSession::durationMinutes)
                          .reversed()
                          .thenComparing((a, b) -> b.date().compareTo(a.date()));

            case CALORIES ->
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
        if (user == null) return Collections.emptyList();
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

    // Switch expression on enum — covers ALL WorkoutType constants
    public String getWorkoutRecommendation(WorkoutType type) {
        return switch (type) {
            case CARDIO      -> "Great for heart health! Aim for 150 minutes per week.";
            case STRENGTH    -> "Build muscle and boost metabolism. Train 3-4 times weekly.";
            case HIIT        -> "Maximum calorie burn! Keep sessions under 30 minutes.";
            case YOGA        -> "Perfect for flexibility and stress relief. Practice daily if possible.";
            case FLEXIBILITY -> "Essential for injury prevention. Stretch after every workout.";
            case SPORT       -> "Team sports build coordination and cardiovascular fitness. Enjoy the game!";
        };
    }

    // Pattern matching with instanceof (Java 16+, fully released)
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
