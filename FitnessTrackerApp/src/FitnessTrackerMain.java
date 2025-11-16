import model.*;
import service.*;
import exception.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

/**
 * Main application demonstrating ALL required Java features
 */
public class FitnessTrackerMain {
    
    public static void main(String[] args) {
        System.out.println("=" .repeat(80));
        System.out.println(FitnessService.getWelcomeMessage()); // Static interface method
        System.out.println("=" .repeat(80));
        System.out.println();
        
        // Initialize service
        var service = new FitnessServiceImpl(); // LVTI
        
        try {
            // Run all demonstrations
            demonstrateBasicFeatures(service);
            System.out.println("\n" + "=".repeat(80) + "\n");
            
            demonstrateAdvancedFeatures(service);
            System.out.println("\n" + "=".repeat(80) + "\n");
            
            demonstrateExceptions(service);
            System.out.println("\n" + "=".repeat(80) + "\n");
            
            demonstrateLambdasAndStreams(service);
            System.out.println("\n" + "=".repeat(80) + "\n");
            
            demonstrateSwitchAndPatternMatching(service);
            
        } catch (InvalidWorkoutException e) {
            System.err.println("Workout Error: " + e.getMessage());
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Application completed successfully!");
        System.out.println("=".repeat(80));
    }
    
    // Demonstrate: Classes, this(), this., method overloading, encapsulation
    private static void demonstrateBasicFeatures(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING BASIC FEATURES <<<\n");
        
        // Create user with different constructors (this() chaining)
        var user1 = new User("U001", "Hitesh");
        var user2 = new User("U002", "Sarah", 28);
        var user3 = new User("U003", "John", 32, 75.5, 178.0);
        
        service.addUser(user1);
        service.addUser(user2);
        service.addUser(user3);
        
        // Demonstrate encapsulation
        user3.setWeight(76.0);
        user3.setHeight(178.5);
        
        System.out.println(user3.getUserProfile()); // Uses this. internally
        System.out.println("BMI: " + String.format("%.2f", user3.calculateBMI()));
        System.out.println();
        
        // Demonstrate arrays
        double[] monthlyCalories = user3.getMonthlyCaloriesBurned();
        System.out.println("Monthly calories tracking initialized (12 months)");
        System.out.println();
    }
    
    // Demonstrate: Records, Enums, Interfaces, Sealed types, Immutable type
    private static void demonstrateAdvancedFeatures(FitnessServiceImpl service) throws InvalidWorkoutException {
        System.out.println(">>> DEMONSTRATING ADVANCED FEATURES <<<\n");
        
        var user = service.getUser("U003");
        
        // Demonstrate Enum
        System.out.println("--- ENUMS ---");
        for (WorkoutType type : WorkoutType.values()) {
            System.out.println(type);
        }
        System.out.println();
        
        // Demonstrate Sealed Interface (Exercise) with implementations
        System.out.println("--- SEALED INTERFACE & IMPLEMENTATIONS ---");
        
        // Polymorphism - Exercise interface reference
        Exercise cardio1 = new CardioExercise("Morning Run", 30, 5.0, 145);
        Exercise cardio2 = new CardioExercise("Evening Jog", 20, 3.5, 130);
        Exercise strength1 = new StrengthExercise("Bench Press", 25, 4, 10, 60.0);
        Exercise strength2 = new StrengthExercise("Squats", 20, 5, 8, 80.0);
        
        System.out.println(Exercise.getGeneralAdvice()); // Static interface method
        System.out.println(cardio1.getExerciseInfo()); // Default interface method
        System.out.println(strength1.getExerciseInfo());
        System.out.println();
        
        // Demonstrate Record (WorkoutSession)
        System.out.println("--- RECORDS ---");
        var session1 = new WorkoutSession(
            "WS001",
            LocalDate.now(),
            WorkoutType.CARDIO,
            List.of(cardio1, cardio2),
            50,
            "Great morning workout!"
        );
        
        System.out.println(session1.getSummary());
        System.out.println("Session ID: " + session1.sessionId());
        System.out.println("Date: " + session1.date());
        System.out.println("Total Calories: " + session1.calculateTotalCalories());
        System.out.println();
        
        var session2 = new WorkoutSession(
            "WS002",
            LocalDate.now().minusDays(1),
            WorkoutType.STRENGTH,
            List.of(strength1, strength2),
            45,
            "Leg day complete"
        );
        
        // Demonstrate varargs - addWorkoutSessions
        user.addWorkoutSessions(session1, session2);
        
        // Demonstrate Record (FitnessGoal)
        var goal = new FitnessGoal("Weight Loss", 10.0, 3.5, LocalDate.now().plusMonths(3), false);
        System.out.println("Goal Progress: " + String.format("%.1f%%", goal.getProgress()));
        System.out.println("Goal Status: " + goal.getStatus());
        user.addGoal(goal);
        System.out.println();
        
        // Demonstrate Immutable Type (NutritionPlan)
        System.out.println("--- CUSTOM IMMUTABLE TYPE (DEFENSIVE COPYING) ---");
        var meals = new ArrayList<String>();
        meals.add("Breakfast: Oats with protein");
        meals.add("Lunch: Chicken with rice");
        meals.add("Dinner: Fish with vegetables");
        meals.add("Snacks: Nuts and fruits");
        
        var nutritionPlan = new NutritionPlan("Cutting Plan", 2200, 180, 200, 60, meals);
        user.setNutritionPlan(nutritionPlan);
        
        System.out.println(nutritionPlan);
        System.out.println("Protein: " + String.format("%.1f%%", nutritionPlan.getProteinPercentage()));
        
        // Try to modify original list - won't affect immutable NutritionPlan
        meals.add("Extra meal");
        System.out.println("Original list modified, but NutritionPlan has " + 
            nutritionPlan.getMeals().size() + " meals (defensive copy works!)");
        System.out.println();
    }
    
    // Demonstrate: Exceptions (checked and unchecked)
    private static void demonstrateExceptions(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING EXCEPTION HANDLING <<<\n");
        
        var user = service.getUser("U003");
        
        // Demonstrate checked exception
        System.out.println("--- CHECKED EXCEPTION ---");
        try {
            var invalidSession = new WorkoutSession(
                "INVALID",
                LocalDate.now(),
                WorkoutType.CARDIO,
                List.of(), // Empty exercises list
                0,
                "Invalid"
            );
            service.addWorkoutToUser("U003", invalidSession);
        } catch (InvalidWorkoutException e) {
            System.out.println("✓ Caught checked exception: " + e.getMessage());
        }
        System.out.println();
        
        // Demonstrate unchecked exception
        System.out.println("--- UNCHECKED EXCEPTION ---");
        try {
            var invalidNutrition = new NutritionPlan(
                "Invalid Plan",
                6000, // Too high
                200, 300, 100,
                List.of("Meal1")
            );
            ValidationService.validateNutritionPlan(invalidNutrition);
        } catch (InvalidNutritionException e) {
            System.out.println("✓ Caught unchecked exception: " + e.getMessage());
        }
        System.out.println();
    }
    
    // Demonstrate: Lambdas, Predicate, Method references, final/effectively final
    private static void demonstrateLambdasAndStreams(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING LAMBDAS & STREAMS <<<\n");
        
        var userId = "U003";
        
        // Lambda with Predicate - filter high calorie workouts
        System.out.println("--- LAMBDAS WITH PREDICATE ---");
        Predicate<WorkoutSession> highCaloriePredicate = session -> session.calculateTotalCalories() > 300;
        var highCalorieWorkouts = service.filterWorkouts(userId, highCaloriePredicate);
        
        System.out.println("High calorie workouts (>300 cal):");
        highCalorieWorkouts.forEach(session -> System.out.println("  - " + session.getSummary()));
        System.out.println();
        
        // Lambda with effectively final variable
        final LocalDate cutoffDate = LocalDate.now().minusDays(7); // effectively final
        Predicate<WorkoutSession> recentPredicate = session -> session.date().isAfter(cutoffDate);
        var recentWorkouts = service.filterWorkouts(userId, recentPredicate);
        
        System.out.println("Recent workouts (last 7 days):");
        recentWorkouts.forEach(session -> System.out.println("  - " + session.getSummary()));
        System.out.println();
        
        // Method reference
        System.out.println("--- METHOD REFERENCES ---");
        System.out.println("All user names:");
        service.getUserNames().forEach(System.out::println); // Method reference
        System.out.println();
        
        // Using method reference with streams
        var user = service.getUser(userId);
        var totalCalories = user.getWorkoutHistory().stream()
            .mapToDouble(WorkoutSession::calculateTotalCalories) // Method reference
            .sum();
        System.out.println("Total calories burned: " + String.format("%.0f", totalCalories));
        System.out.println();
    }
    
    // Demonstrate: Switch expressions and pattern matching
    private static void demonstrateSwitchAndPatternMatching(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING SWITCH EXPRESSIONS & PATTERN MATCHING <<<\n");
        
        // Switch expressions with enum
        System.out.println("--- SWITCH EXPRESSIONS ---");
        for (WorkoutType type : new WorkoutType[]{WorkoutType.CARDIO, WorkoutType.STRENGTH, WorkoutType.HIIT}) {
            System.out.println(type.name() + ": " + service.getWorkoutRecommendation(type));
        }
        System.out.println();
        
        // Pattern matching with switch (Java 21)
        System.out.println("--- PATTERN MATCHING WITH SWITCH ---");
        Exercise cardio = new CardioExercise("Sprint", 15, 2.5, 160);
        Exercise strength = new StrengthExercise("Deadlift", 30, 5, 5, 100.0);
        
        System.out.println(service.analyzeExercise(cardio));
        System.out.println(service.analyzeExercise(strength));
        System.out.println();
        
        // Additional switch expression example
        var user = service.getUser("U003");
        var bmi = user.calculateBMI();
        
        String bmiCategory = switch ((int) bmi / 5) {
            case 0, 1, 2, 3 -> "Underweight";
            case 4 -> "Normal weight";
            case 5 -> "Overweight";
            default -> "Obese";
        };
        
        System.out.println("BMI Category: " + bmiCategory + " (BMI: " + String.format("%.2f", bmi) + ")");
    }
}

