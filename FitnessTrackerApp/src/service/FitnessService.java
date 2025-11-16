package service;

import model.*;
import exception.InvalidWorkoutException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

/**
 * Service interface demonstrating interface methods
 */
public interface FitnessService {
    
    void addUser(User user);
    User getUser(String userId);
    void addWorkoutToUser(String userId, WorkoutSession session) throws InvalidWorkoutException;
    List<WorkoutSession> filterWorkouts(String userId, Predicate<WorkoutSession> criteria);
    double calculateUserProgress(String userId);
    
    // Default interface method
    default String getServiceInfo() {
        return "Fitness Tracking Service v1.0";
    }
    
    // Static interface method
    static String getWelcomeMessage() {
        return "Welcome to the Fitness Tracker Application!";
    }
    
    // Private interface method (Java 9+)
    private String formatCalories(double calories) {
        return String.format("%.0f cal", calories);
    }
    
    // Default method using private method
    default String getFormattedCalories(double calories) {
        return formatCalories(calories);
    }
}