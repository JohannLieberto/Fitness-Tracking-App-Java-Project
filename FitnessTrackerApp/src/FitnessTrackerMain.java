import model.*;
import service.*;
import service.FitnessServiceImpl.WorkoutSortKey;
import exception.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Main entry point — wires all OOP2 features together.
 *
 * Compile from FitnessTrackerApp/src:
 *   javac -d . model/*.java exception/*.java service/*.java FitnessTrackerMain.java
 *   java FitnessTrackerMain
 */
public class FitnessTrackerMain {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Welcome to the Fitness Tracker Application!");
        System.out.println("=".repeat(80));

        User user = new User("U003", "Hitesh", 25, 76.0, 178.5);
        FitnessServiceImpl service = new FitnessServiceImpl();
        service.addUser(user);

        WorkoutSession ws1 = new WorkoutSession("WS001", "U003", LocalDate.now(),
                WorkoutType.CARDIO,   50, 825,  "Great morning workout!");
        WorkoutSession ws2 = new WorkoutSession("WS002", "U003", LocalDate.now().minusDays(1),
                WorkoutType.STRENGTH, 45, 590,  "Leg day complete");
        WorkoutSession ws3 = new WorkoutSession("WS003", "U003", LocalDate.now().minusDays(2),
                WorkoutType.CARDIO,   40, 1070, "Cycling session");
        WorkoutSession ws4 = new WorkoutSession("WS004", "U003", LocalDate.now().minusDays(3),
                WorkoutType.STRENGTH, 35, 460,  "Heavy pull day");
        WorkoutSession ws5 = new WorkoutSession("WS005", "U003", LocalDate.now().minusDays(4),
                WorkoutType.HIIT,     25, 200,  "Rowing intervals");

        service.addWorkoutToUser("U003", ws1);
        service.addWorkoutToUser("U003", ws2);
        service.addWorkoutToUser("U003", ws3);
        service.addWorkoutToUser("U003", ws4);
        service.addWorkoutToUser("U003", ws5);

        demonstrateBasicFeatures(user);
        demonstrateAdvancedFeatures(user, service);
        demonstrateExceptionHandling();
        demonstrateLambdasAndStreams(user, service);
        demonstrateSwitchAndPatternMatching(user, service);
        demonstrateConcurrency(user);
        demonstrateNIO2(user);
        demonstrateLocalisation();
    }

    // ----------------------------------------------------------------
    // Basic features
    // ----------------------------------------------------------------
    static void demonstrateBasicFeatures(User user) {
        System.out.println("\n>>> DEMONSTRATING BASIC FEATURES <<<\n");
        System.out.println("=== User Profile ===");
        System.out.println("ID: "     + user.getUserId());
        System.out.println("Name: "   + user.getName());
        System.out.println("Age: "    + user.getAge() + " years");
        System.out.println("Weight: " + user.getWeightKg() + " kg");
        System.out.println("Height: " + user.getHeightCm() + " cm");
        System.out.printf("BMI: %.2f%n", user.getBMI());
        System.out.println("Total Workouts: " + user.getTotalWorkouts());
        System.out.println("Active Goals: "  + user.getGoals().size());
        user.getMonthlyCalories();
    }

    // ----------------------------------------------------------------
    // Advanced features
    // ----------------------------------------------------------------
    static void demonstrateAdvancedFeatures(User user, FitnessServiceImpl service) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING ADVANCED FEATURES <<<\n");

        // Enums with fields
        System.out.println("--- ENUMS ---");
        for (WorkoutType type : WorkoutType.values()) {
            System.out.printf("%s (%s) - %.1f cal/min%n",
                    type.name(), type.getDisplayName(), type.getCaloriesPerMinute());
        }

        // Sealed interface + permitted subtypes (record accessors, no name field)
        System.out.println("\n--- SEALED INTERFACE & IMPLEMENTATIONS ---");
        Activity warmup   = new Running(2.5, 4.5);              // distanceKm, paceMinPerKm
        Activity strength = new StrengthTraining("Bench Press", 80.0, 10); // exerciseName, weightKg, reps
        System.out.println(warmup);
        System.out.println(strength);

        // WorkoutSummary record
        System.out.println("\n--- RECORDS ---");
        WorkoutSession ws1 = user.getWorkoutHistory().get(0);
        WorkoutSummary summary = new WorkoutSummary(
                ws1.sessionId(), ws1.date(), ws1.workoutType(),
                ws1.durationMinutes(), ws1.caloriesBurned(), ws1.notes());
        System.out.println(summary);
        System.out.printf("Total Calories: %.1f%n", summary.totalCalories());

        // UserGoal record — (label, targetCaloriesPerWeek, deadline)
        UserGoal goal = new UserGoal("Weight Loss", 3500, LocalDate.of(2026, 12, 31));
        System.out.println(goal);
        double progress = (user.getTotalCaloriesBurned() / (double) goal.targetCaloriesPerWeek()) * 100;
        System.out.printf("%nGoal Progress: %.1f%%%n", Math.min(progress, 100.0));

        // FitnessGoal record — (goalType, targetValue, currentValue, deadline, achieved)
        FitnessGoal fitnessGoal = new FitnessGoal("Weekly Calories", 3500.0, 0.0,
                LocalDate.of(2026, 12, 31), false);
        user.addGoal(fitnessGoal);
        System.out.println("FitnessGoal status: " + fitnessGoal.getStatus());

        // NutritionPlan — (planName, dailyCalories, proteinGrams, carbsGrams, fatsGrams, meals)
        System.out.println("\n--- CUSTOM IMMUTABLE TYPE (DEFENSIVE COPYING) ---");
        List<String> meals = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack"));
        NutritionPlan plan = new NutritionPlan("Maintenance", 2200, 180, 200, 60, new ArrayList<>(meals));
        System.out.println(plan);
        meals.clear();
        System.out.println("NutritionPlan still has " + plan.getMeals().size() + " meals (defensive copy works!)");
    }

    // ----------------------------------------------------------------
    // Exception handling
    // ----------------------------------------------------------------
    static void demonstrateExceptionHandling() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING EXCEPTION HANDLING <<<\n");

        System.out.println("--- CHECKED EXCEPTION (compact constructor validation) ---");
        try {
            WorkoutSession bad = new WorkoutSession("WS-BAD", "U003",
                    LocalDate.now(), WorkoutType.CARDIO, -1, 0, "");
        } catch (IllegalArgumentException e) {
            System.out.println("\u2713 Caught: " + e.getMessage());
        }

        System.out.println("\n--- UNCHECKED EXCEPTION ---");
        try {
            ValidationService.validateNutritionPlan(
                    new NutritionPlan("Empty", 500, 50, 60, 20, new ArrayList<>()));
        } catch (InvalidNutritionException e) {
            System.out.println("\u2713 Caught: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Lambdas & Streams
    // ----------------------------------------------------------------
    static void demonstrateLambdasAndStreams(User user, FitnessServiceImpl service) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING LAMBDAS & STREAMS <<<\n");

        List<WorkoutSession> sessions = user.getWorkoutHistory();

        // Predicate — record accessors
        System.out.println("--- LAMBDAS WITH PREDICATE ---");
        Predicate<WorkoutSession> highCalorie = s -> s.caloriesBurned() > 300;
        Predicate<WorkoutSession> recentWeek  = s -> !s.date().isBefore(LocalDate.now().minusDays(7));

        System.out.println("High calorie workouts (>300 cal):");
        sessions.stream().filter(highCalorie).forEach(s -> System.out.println("  - " + s));

        System.out.println("\nRecent workouts (last 7 days):");
        sessions.stream().filter(recentWeek).forEach(s -> System.out.println("  - " + s));

        // Method references
        System.out.println("\n--- METHOD REFERENCES ---");
        FitnessServiceImpl svc2 = new FitnessServiceImpl();
        svc2.addUser(new User("U001", "Hitesh", 25, 76.0, 178.5));
        svc2.addUser(new User("U002", "John",   32, 80.0, 180.0));
        svc2.addUser(new User("U003", "Sarah",  28, 65.0, 165.0));
        svc2.getUserNames().forEach(System.out::println);

        // Streams — terminal
        int totalCalories = sessions.stream().mapToInt(WorkoutSession::caloriesBurned).sum();
        System.out.println("\nTotal calories burned: " + totalCalories);

        System.out.println("\n--- STREAMS: groupingBy, partitioningBy, toMap ---");
        Map<WorkoutType, List<WorkoutSession>> byType = sessions.stream()
                .collect(Collectors.groupingBy(WorkoutSession::workoutType));
        byType.forEach((type, list) ->
                System.out.printf("  %s: %d session(s)%n", type.getDisplayName(), list.size()));

        Map<Boolean, List<WorkoutSession>> partitioned = sessions.stream()
                .collect(Collectors.partitioningBy(s -> s.durationMinutes() >= 45));
        System.out.println("  >=45 min: " + partitioned.get(true).size()
                + "  <45 min: " + partitioned.get(false).size());

        Map<String, Integer> calorieMap = sessions.stream()
                .collect(Collectors.toMap(WorkoutSession::sessionId, WorkoutSession::caloriesBurned));
        System.out.println("  Calorie map: " + calorieMap);

        sessions.stream()
                .filter(s -> s.workoutType() == WorkoutType.HIIT)
                .findFirst()
                .ifPresent(s -> System.out.println("  First HIIT session: " + s.sessionId()));
        boolean anyLong = sessions.stream().anyMatch(s -> s.durationMinutes() > 40);
        System.out.println("  Any session >40 min: " + anyLong);
        long cardioCount = sessions.stream().filter(s -> s.workoutType() == WorkoutType.CARDIO).count();
        System.out.println("  Cardio session count: " + cardioCount);
    }

    // ----------------------------------------------------------------
    // Switch expressions & pattern matching
    // ----------------------------------------------------------------
    static void demonstrateSwitchAndPatternMatching(User user, FitnessServiceImpl service) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING SWITCH EXPRESSIONS & PATTERN MATCHING <<<\n");

        System.out.println("--- SWITCH EXPRESSIONS ---");
        for (WorkoutType t : new WorkoutType[]{WorkoutType.CARDIO, WorkoutType.STRENGTH, WorkoutType.HIIT}) {
            System.out.println(t + ": " + service.getWorkoutRecommendation(t));
        }

        System.out.println("\n--- PATTERN MATCHING WITH SWITCH (Exercise hierarchy) ---");
        Exercise[] exercises = {
            new CardioExercise("Sprint", 15, 2.5, 160.0),
            // StrengthExercise(name, duration, sets, reps, weight) — weight is last arg
            new StrengthExercise("Deadlift", 20, 5, 3, 100.0)
        };
        for (Exercise ex : exercises) {
            System.out.println(service.analyzeExercise(ex));
        }

        double bmi = user.getBMI();
        String category = switch ((int) bmi / 5) {
            case 0, 1, 2, 3 -> "Underweight (BMI < 18.5)";
            case 4          -> bmi < 18.5 ? "Underweight" : "Normal weight (BMI: " + String.format("%.2f", bmi) + ")";
            case 5          -> bmi < 25.0 ? "Normal weight (BMI: " + String.format("%.2f", bmi) + ")" : "Overweight";
            default         -> "Overweight / Obese (BMI >= 25)";
        };
        System.out.println("\nBMI Category: " + category);
    }

    // ----------------------------------------------------------------
    // Concurrency
    // ----------------------------------------------------------------
    static void demonstrateConcurrency(User user) throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING CONCURRENCY <<<\n");
        AnalyticsService analytics = new AnalyticsService();
        analytics.runParallelAnalysis(user.getWorkoutHistory());
    }

    // ----------------------------------------------------------------
    // NIO2
    // ----------------------------------------------------------------
    static void demonstrateNIO2(User user) throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING NIO2 <<<\n");
        WorkoutDataManager dm = new WorkoutDataManager();
        dm.demonstrateNIO2(user.getWorkoutHistory(), user.getUserId());
    }

    // ----------------------------------------------------------------
    // Localisation
    // ----------------------------------------------------------------
    static void demonstrateLocalisation() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("\n>>> DEMONSTRATING LOCALISATION <<<\n");

        Locale[] locales = { Locale.ENGLISH, Locale.FRENCH };
        for (Locale locale : locales) {
            System.out.println("  --- Locale: " + locale.getDisplayLanguage() + " ---");
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            System.out.println("  " + bundle.getString("app.welcome"));
            System.out.println("  " + bundle.getString("app.goodbye"));
            System.out.printf("  " + bundle.getString("stats.calories") + "%n", 3145);
            System.out.printf("  " + bundle.getString("stats.duration") + "%n", 195);
            System.out.println();
        }
    }
}
