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
 * - Switch expressions
 */
public class FitnessServiceImpl implements FitnessService {
    
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
        // Demonstrating checked exception handling
        ValidationService.validateWorkoutSession(session);
        
        var user = users.get(userId);
        if (user == null) {
            throw new InvalidWorkoutException("User not found: " + userId);
        }
        
        user.addWorkoutSession(session);
    }
    
    // Demonstrating lambdas and Predicate
    @Override
    public List<WorkoutSession> filterWorkouts(String userId, Predicate<WorkoutSession> criteria) {
        var user = users.get(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        
        return user.getWorkoutHistory().stream()
            .filter(criteria) // Lambda with Predicate
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
            .collect(java.util.stream.Collectors.toList());
}


    public String getWorkoutRecommendation(WorkoutType type) {
    return switch (type) {
        case CARDIO -> "Great for heart health! Aim for 150 minutes per week.";
        case STRENGTH -> "Build muscle and boost metabolism. Train 3-4 times weekly.";
        case HIIT -> "Maximum calorie burn! Keep sessions under 30 minutes.";
        case YOGA -> "Perfect for flexibility and stress relief. Practice daily if possible.";
        case FLEXIBILITY -> "Essential for injury prevention. Stretch after every workout.";
    };
}


public String analyzeExercise(Exercise exercise) {
    return switch (exercise) {
        case CardioExercise cardio -> 
            String.format("Cardio workout: %s covering %.2f km", 
                cardio.getName(), cardio.getDistance());
        case StrengthExercise strength -> 
            String.format("Strength training: %s with %.1f kg", 
                strength.getName(), strength.getWeight());
        case null, default -> "Unknown or null exercise"; // JAVA 21+ UNNAMED PATTERN
    };
}


}
    // Method demonstrating switch expres
