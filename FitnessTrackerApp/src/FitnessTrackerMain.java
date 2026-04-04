import model.*;
import service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Entry point — runs all OOP2 feature demonstrations in sequence.
 * Demonstrates: All OOP2 Fundamentals + Advanced features
 *
 * Compile (from FitnessTrackerApp/src):
 *   javac -d . model/*.java exception/*.java service/*.java FitnessTrackerMain.java
 *
 * Run:
 *   java FitnessTrackerMain
 *
 * For Java 25 features, compile and run Java25Demo.java separately:
 *   javac --release 25 --enable-preview Java25Demo.java model/Activity.java ...
 *   java  --enable-preview Java25Demo
 */
public class FitnessTrackerMain {

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     Fitness Tracker — OOP2 Demo          ║");
        System.out.println("╚══════════════════════════════════════════╝");

        List<WorkoutSession> workouts = buildSampleData();
        WorkoutService service = new WorkoutService(workouts);
        AnalyticsService analytics = new AnalyticsService();
        WorkoutDataManager dataManager = new WorkoutDataManager();

        // ── FUNDAMENTALS ─────────────────────────────────────────
        service.getWorkoutsSortedByDate();
        service.getWorkoutsSortedByDuration();

        service.filterByType(WorkoutType.CARDIO);
        service.applyToAll(w -> System.out.println("  Consumer: " + w.getName() + " -> " + w.getCaloriesBurned() + " kcal"));
        service.getOrDefault(() -> List.of(new WorkoutSession("Default Run", LocalDate.now(), 30, 250, WorkoutType.CARDIO)));
        service.mapToCalories(WorkoutSession::getCaloriesBurned);

        service.showStreamTerminalOps();
        service.showStreamCollectors();
        service.showStreamIntermediateOps();

        service.scoreActivity(WorkoutType.HIIT);
        service.scoreActivity(WorkoutType.CARDIO);
        service.describeActivity(new Running(10.0, 5.5));
        service.describeActivity(new Cycling(25.0, 28.0));
        service.describeActivity(new StrengthTraining("Deadlift", 100.0, 5));

        service.showDateTimeFeatures();
        service.showRecords();

        // ── ADVANCED ─────────────────────────────────────────────
        demonstrateConcurrency(analytics, workouts);
        demonstrateNio2(dataManager, workouts);
        demonstrateLocalisation();

        System.out.println("\n✓ All OOP2 features demonstrated successfully.");
        System.out.println("  Run Java25Demo separately with --enable-preview for JEP 512/513.");
    }

    // ─────────────────────────────────────────────────────────────
    // ADVANCED: Concurrency
    // ─────────────────────────────────────────────────────────────
    private static void demonstrateConcurrency(AnalyticsService analytics,
                                               List<WorkoutSession> workouts) throws InterruptedException {
        analytics.runAnalytics(workouts);
    }

    // ─────────────────────────────────────────────────────────────
    // ADVANCED: NIO2
    // ─────────────────────────────────────────────────────────────
    private static void demonstrateNio2(WorkoutDataManager mgr,
                                        List<WorkoutSession> workouts) throws IOException {
        mgr.exportToCsv(workouts);
        mgr.importFromCsv();
        mgr.listBackups();
    }

    // ─────────────────────────────────────────────────────────────
    // ADVANCED: Localisation
    // ─────────────────────────────────────────────────────────────
    private static void demonstrateLocalisation() {
        System.out.println("\n=== [LOCALISATION] ResourceBundle + Locale ===");

        for (Locale locale : List.of(Locale.ENGLISH, Locale.FRENCH)) {
            System.out.println("  -- Locale: " + locale.getDisplayLanguage() + " --");
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", locale);
            System.out.println("  " + bundle.getString("app.greeting"));
            System.out.println("  " + bundle.getString("app.workout.prompt"));
            System.out.println("  " + bundle.getString("app.stats.weekly"));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Sample data
    // ─────────────────────────────────────────────────────────────
    private static List<WorkoutSession> buildSampleData() {
        return new ArrayList<>(List.of(
                new WorkoutSession("Morning Run",    LocalDate.now().minusDays(1),  35, 320, WorkoutType.CARDIO),
                new WorkoutSession("HIIT Blast",     LocalDate.now().minusDays(2),  25, 410, WorkoutType.HIIT),
                new WorkoutSession("Leg Day",        LocalDate.now().minusDays(3),  55, 380, WorkoutType.STRENGTH),
                new WorkoutSession("Yoga Flow",      LocalDate.now().minusDays(4),  45, 180, WorkoutType.YOGA),
                new WorkoutSession("Cycling Session",LocalDate.now().minusDays(5),  60, 500, WorkoutType.CARDIO),
                new WorkoutSession("Upper Body",     LocalDate.now().minusDays(6),  50, 350, WorkoutType.STRENGTH),
                new WorkoutSession("Stretching",     LocalDate.now().minusDays(8),  30, 120, WorkoutType.FLEXIBILITY),
                new WorkoutSession("5k Parkrun",     LocalDate.now().minusDays(10), 28, 290, WorkoutType.CARDIO),
                new WorkoutSession("Morning Run",    LocalDate.now().minusDays(12), 35, 310, WorkoutType.CARDIO),
                new WorkoutSession("Full Body HIIT", LocalDate.now().minusDays(14), 20, 430, WorkoutType.HIIT)
        ));
    }
}
