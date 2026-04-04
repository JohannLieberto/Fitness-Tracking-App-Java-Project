package service;

import exception.InvalidNutritionException;
import exception.InvalidWorkoutException;
import model.WorkoutSession;
import model.NutritionPlan;

/**
 * Validation service demonstrating checked and unchecked exception handling.
 */
public class ValidationService {

    // Throws checked exception — caller must handle or declare
    public static void validateWorkoutSession(WorkoutSession session) throws InvalidWorkoutException {
        if (session == null) {
            throw new InvalidWorkoutException("Workout session cannot be null");
        }
        // Uses record accessor durationMinutes()
        if (session.durationMinutes() <= 0) {
            throw new InvalidWorkoutException("Workout duration must be positive");
        }
        if (session.caloriesBurned() < 0) {
            throw new InvalidWorkoutException("Calories burned cannot be negative");
        }
    }

    // Throws unchecked exception — no declaration needed
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
