import model.*;
import service.*;
import exception.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

/**
 * Main application demonstrating ALL required Java features.
 *
 * OOP2 Additions vs OOP1:
 *  - Concurrency:    demonstrateConcurrency()   -> service.AnalyticsService
 *  - NIO2:           demonstrateNio2()           -> service.WorkoutDataManager
 *  - Localisation:   demonstrateLocalisation()  -> i18n/messages_*.properties
 *  - Java 25 demo:   Java25Demo (separate file, see compile instructions)
 */
public class FitnessTrackerMain {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println(FitnessService.getWelcomeMessage());
        System.out.println("=".repeat(80));
        System.out.println();

        var service = new FitnessServiceImpl(); // LVTI

        try {
            demonstrateBasicFeatures(service);
            sep();

            demonstrateAdvancedFeatures(service);
            sep();

            demonstrateExceptions(service);
            sep();

            demonstrateLambdasAndStreams(service);
            sep();

            demonstrateSwitchAndPatternMatching(service);
            sep();

            // ---- NEW OOP2 SECTIONS ----
            demonstrateConcurrency(service);
            sep();

            demonstrateNio2(service);
            sep();

            demonstrateLocalisation();

        } catch (InvalidWorkoutException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Application completed successfully!");
        System.out.println("=".repeat(80));
    }

    // ----------------------------------------------------------------
    // CONCURRENCY — ExecutorService, Callable, Future, invokeAll
    // ----------------------------------------------------------------
    private static void demonstrateConcurrency(FitnessServiceImpl service)
            throws InvalidWorkoutException {
        System.out.println(">>> DEMONSTRATING CONCURRENCY <<<\n");

        // Build a richer workout history to make analytics meaningful
        var u = service.getUser("U003");
        if (u == null) return;

        // Add a few extra sessions if the list is thin
        if (u.getWorkoutHistory().size() < 3) {
            Exercise e1 = new CardioExercise("Cycle", 40, 15.0, 140);
            Exercise e2 = new StrengthExercise("Deadlift", 35, 5, 5, 100.0);
            Exercise e3 = new CardioExercise("Row", 25, 0, 150);

            service.addWorkoutToUser("U003",
                new WorkoutSession("WS003", LocalDate.now().minusDays(2),
                    WorkoutType.CARDIO, List.of(e1), 40, "Cycling session"));
            service.addWorkoutToUser("U003",
                new WorkoutSession("WS004", LocalDate.now().minusDays(3),
                    WorkoutType.STRENGTH, List.of(e2), 35, "Heavy pull day"));
            service.addWorkoutToUser("U003",
                new WorkoutSession("WS005", LocalDate.now().minusDays(4),
                    WorkoutType.HIIT, List.of(e3), 25, "Rowing intervals"));
        }

        var sessions = u.getWorkoutHistory();
        var analytics = new AnalyticsService();

        System.out.println("Running " + 6 + " analytics tasks concurrently...");
        System.out.println();

        // --- Submit all Callable tasks via invokeAll ---
        List<AnalyticsService.AnalyticsResult> results =
                analytics.runConcurrentAnalytics(sessions);

        System.out.println("  Analytics results (all tasks completed in parallel):");
        results.forEach(System.out::println);
        System.out.println();

        // --- Bonus: groupingBy calories per workout type ---
        System.out.println("  Calories burned by workout type (groupingBy):");
        analytics.caloriesByType(sessions)
                 .forEach((type, cal) ->
                     System.out.printf("    %-12s : %.0f kcal%n", type, cal));
    }

    // ----------------------------------------------------------------
    // NIO2 — Path, Files, walkFileTree, BasicFileAttributes
    // ----------------------------------------------------------------
    private static void demonstrateNio2(FitnessServiceImpl service) throws IOException {
        System.out.println(">>> DEMONSTRATING NIO2 <<<\n");

        var manager = new WorkoutDataManager();
        var user = service.getUser("U003");
        if (user == null) return;

        var sessions = user.getWorkoutHistory();

        // 1 — Save workout history to CSV using NIO2 Files.newBufferedWriter
        System.out.println("  1. Saving workout history to CSV...");
        manager.saveWorkoutHistory(user.getUserId(), sessions);

        // 2 — Read it back using NIO2 Files.newBufferedReader
        System.out.println("\n  2. Reading back from CSV...");
        var rows = manager.loadWorkoutHistory();
        rows.forEach(row -> System.out.println("     Row: " + Arrays.toString(row)));

        // 3 — Backup using NIO2 Files.copy + Path
        System.out.println("\n  3. Creating backup...");
        manager.backupHistory();

        // 4 — Walk backup directory with walkFileTree (NIO2)
        System.out.println("\n  4. Listing backup files via walkFileTree...");
        manager.listBackups();

        // 5 — File metadata via BasicFileAttributes (NIO2)
        System.out.println("\n  5. File attributes (BasicFileAttributes)...");
        manager.printFileInfo();
    }

    // ----------------------------------------------------------------
    // LOCALISATION — ResourceBundle, Locale, MessageFormat
    // ----------------------------------------------------------------
    private static void demonstrateLocalisation() {
        System.out.println(">>> DEMONSTRATING LOCALISATION <<<\n");

        // Locales to demonstrate
        Locale[] locales = { Locale.ENGLISH, Locale.FRENCH };

        for (Locale locale : locales) {
            // Load the correct properties file from i18n/ package
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

            System.out.println("  --- Locale: " + locale.getDisplayLanguage() + " ---");
            System.out.println("  " + bundle.getString("app.welcome"));
            System.out.println("  " + bundle.getString("app.subtitle"));
            System.out.println();

            // Menu items
            System.out.println("  Menu:");
            for (String key : List.of("menu.addWorkout", "menu.viewHistory",
                                       "menu.analytics", "menu.goals")) {
                System.out.println("    > " + bundle.getString(key));
            }
            System.out.println();

            // MessageFormat for parameterised messages
            String calMsg = MessageFormat.format(
                    bundle.getString("stats.totalCalories"), 1540);
            String sessionMsg = MessageFormat.format(
                    bundle.getString("stats.totalSessions"), 5);
            String streakMsg = MessageFormat.format(
                    bundle.getString("stats.streak"), 7);

            System.out.println("  " + calMsg);
            System.out.println("  " + sessionMsg);
            System.out.println("  " + streakMsg);
            System.out.println();
        }
    }

    // ----------------------------------------------------------------
    //  Existing methods below — unchanged from OOP1
    // ----------------------------------------------------------------

    private static void sep() {
        System.out.println("\n" + "=".repeat(80) + "\n");
    }

    private static void demonstrateBasicFeatures(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING BASIC FEATURES <<<\n");

        var user1 = new User("U001", "Hitesh");
        var user2 = new User("U002", "Sarah", 28);
        var user3 = new User("U003", "John", 32, 75.5, 178.0);

        service.addUser(user1);
        service.addUser(user2);
        service.addUser(user3);

        user3.setWeight(76.0);
        user3.setHeight(178.5);

        System.out.println(user3.getUserProfile());
        System.out.println("BMI: " + String.format("%.2f", user3.calculateBMI()));
        System.out.println();

        double[] monthlyCalories = user3.getMonthlyCaloriesBurned();
        System.out.println("Monthly calories tracking initialized (12 months)");
        System.out.println();
    }

    private static void demonstrateAdvancedFeatures(FitnessServiceImpl service)
            throws InvalidWorkoutException {
        System.out.println(">>> DEMONSTRATING ADVANCED FEATURES <<<\n");

        var user = service.getUser("U003");

        System.out.println("--- ENUMS ---");
        for (WorkoutType type : WorkoutType.values()) {
            System.out.println(type);
        }
        System.out.println();

        System.out.println("--- SEALED INTERFACE & IMPLEMENTATIONS ---");
        Exercise cardio1 = new CardioExercise("Morning Run", 30, 5.0, 145);
        Exercise cardio2 = new CardioExercise("Evening Jog", 20, 3.5, 130);
        Exercise strength1 = new StrengthExercise("Bench Press", 25, 4, 10, 60.0);
        Exercise strength2 = new StrengthExercise("Squats", 20, 5, 8, 80.0);

        System.out.println(Exercise.getGeneralAdvice());
        System.out.println(cardio1.getExerciseInfo());
        System.out.println(strength1.getExerciseInfo());
        System.out.println();

        System.out.println("--- RECORDS ---");
        var session1 = new WorkoutSession(
            "WS001", LocalDate.now(), WorkoutType.CARDIO,
            List.of(cardio1, cardio2), 50, "Great morning workout!");

        System.out.println(session1.getSummary());
        System.out.println("Total Calories: " + session1.calculateTotalCalories());
        System.out.println();

        var session2 = new WorkoutSession(
            "WS002", LocalDate.now().minusDays(1), WorkoutType.STRENGTH,
            List.of(strength1, strength2), 45, "Leg day complete");

        user.addWorkoutSessions(session1, session2);

        var goal = new FitnessGoal("Weight Loss", 10.0, 3.5,
                LocalDate.now().plusMonths(3), false);
        System.out.println("Goal Progress: " + String.format("%.1f%%", goal.getProgress()));
        user.addGoal(goal);
        System.out.println();

        System.out.println("--- CUSTOM IMMUTABLE TYPE (DEFENSIVE COPYING) ---");
        var meals = new ArrayList<String>();
        meals.add("Breakfast: Oats with protein");
        meals.add("Lunch: Chicken with rice");
        meals.add("Dinner: Fish with vegetables");
        meals.add("Snacks: Nuts and fruits");

        var nutritionPlan = new NutritionPlan("Cutting Plan", 2200, 180, 200, 60, meals);
        user.setNutritionPlan(nutritionPlan);
        System.out.println(nutritionPlan);
        meals.add("Extra meal");
        System.out.println("NutritionPlan still has " + nutritionPlan.getMeals().size()
                + " meals (defensive copy works!)");
        System.out.println();
    }

    private static void demonstrateExceptions(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING EXCEPTION HANDLING <<<\n");

        System.out.println("--- CHECKED EXCEPTION ---");
        try {
            var invalid = new WorkoutSession(
                "INVALID", LocalDate.now(), WorkoutType.CARDIO,
                List.of(), 0, "Invalid");
            service.addWorkoutToUser("U003", invalid);
        } catch (InvalidWorkoutException e) {
            System.out.println("\u2713 Caught checked exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- UNCHECKED EXCEPTION ---");
        try {
            var bad = new NutritionPlan("Invalid Plan", 6000, 200, 300, 100,
                    List.of("Meal1"));
            ValidationService.validateNutritionPlan(bad);
        } catch (exception.InvalidNutritionException e) {
            System.out.println("\u2713 Caught unchecked exception: " + e.getMessage());
        }
        System.out.println();
    }

    private static void demonstrateLambdasAndStreams(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING LAMBDAS & STREAMS <<<\n");

        var userId = "U003";

        System.out.println("--- LAMBDAS WITH PREDICATE ---");
        Predicate<WorkoutSession> highCaloriePredicate =
                session -> session.calculateTotalCalories() > 300;
        var highCalorie = service.filterWorkouts(userId, highCaloriePredicate);
        System.out.println("High calorie workouts (>300 cal):");
        highCalorie.forEach(s -> System.out.println("  - " + s.getSummary()));
        System.out.println();

        final LocalDate cutoff = LocalDate.now().minusDays(7);
        Predicate<WorkoutSession> recentPredicate = s -> s.date().isAfter(cutoff);
        var recent = service.filterWorkouts(userId, recentPredicate);
        System.out.println("Recent workouts (last 7 days):");
        recent.forEach(s -> System.out.println("  - " + s.getSummary()));
        System.out.println();

        System.out.println("--- METHOD REFERENCES ---");
        service.getUserNames().forEach(System.out::println);
        System.out.println();

        var user = service.getUser(userId);
        var totalCalories = user.getWorkoutHistory().stream()
                .mapToDouble(WorkoutSession::calculateTotalCalories)
                .sum();
        System.out.println("Total calories burned: " + String.format("%.0f", totalCalories));
        System.out.println();
    }

    private static void demonstrateSwitchAndPatternMatching(FitnessServiceImpl service) {
        System.out.println(">>> DEMONSTRATING SWITCH EXPRESSIONS & PATTERN MATCHING <<<\n");

        System.out.println("--- SWITCH EXPRESSIONS ---");
        for (WorkoutType type : new WorkoutType[]{
                WorkoutType.CARDIO, WorkoutType.STRENGTH, WorkoutType.HIIT}) {
            System.out.println(type.name() + ": " + service.getWorkoutRecommendation(type));
        }
        System.out.println();

        System.out.println("--- PATTERN MATCHING WITH SWITCH ---");
        Exercise cardio = new CardioExercise("Sprint", 15, 2.5, 160);
        Exercise strength = new StrengthExercise("Deadlift", 30, 5, 5, 100.0);
        System.out.println(service.analyzeExercise(cardio));
        System.out.println(service.analyzeExercise(strength));
        System.out.println();

        var user = service.getUser("U003");
        var bmi = user.calculateBMI();
        String bmiCategory = switch ((int) bmi / 5) {
            case 0, 1, 2, 3 -> "Underweight";
            case 4 -> "Normal weight";
            case 5 -> "Overweight";
            default -> "Obese";
        };
        System.out.println("BMI Category: " + bmiCategory
                + " (BMI: " + String.format("%.2f", bmi) + ")");
    }
}
