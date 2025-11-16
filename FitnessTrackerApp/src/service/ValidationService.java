package service;

import exception.InvalidNutritionException;
import exception.InvalidWorkoutException;
import model.WorkoutSession;
import model.NutritionPlan;

/**
 * Validation service demonstrating exception handling
 */
public class ValidationService {
    
    // Method throwing checked exception
    public static void validateWorkoutSession(WorkoutSession session) throws InvalidWorkoutException {
        if (session == null) {
            throw new InvalidWorkoutException("Workout session cannot be null");
        }
        
        if (session.totalDuration() <= 0) {
            throw new InvalidWorkoutException("Workout duration must be positive");
        }
        
        if (session.exercises() == null || session.exercises().isEmpty()) {
            throw new InvalidWorkoutException("Workout must contain at least one exercise");
        }
    }
    
    // Method throwing unchecked exception
    public static void validateNutritionPlan(NutritionPlan plan) {
        if (plan == null) {
            throw new InvalidNutritionException("Nutrition plan cannot be null");
        }
        
        if (plan.getDailyCalories() < 1000 || plan.getDailyCalories() > 5000) {
            throw new InvalidNutritionException("Daily calories must be between 1000 and 5000");
        }
        
        if (plan.getProteinGrams() < 0 || plan.getCarbsGrams() < 0 || plan.getFatsGrams() < 0) {
            throw new InvalidNutritionException("Macronutrients cannot be negative");
        }
    }
    
    public static boolean isValidBMI(double bmi) {
        return bmi >= 10.0 && bmi <= 50.0;
    }
    
    public static boolean isValidAge(int age) {
        return age >= 13 && age <= 120;
    }
}