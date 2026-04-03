import model.WorkoutSession;
import model.WorkoutType;
import model.CardioExercise;

import java.time.LocalDate;
import java.util.List;

/**
 * OOP2 - JAVA 25 FEATURES DEMO
 *
 * Demonstrates (JEP 512): Compact source file + instance main method.
 * This class does NOT need 'public static void main' — it uses
 * an instance main method, valid from Java 25 (preview in 21).
 *
 * Compile:  javac --release 25 --enable-preview Java25Demo.java
 * Run:      java --enable-preview Java25Demo
 *
 * Also demonstrates (JEP 513): Flexible constructor body — the
 * WorkoutBuilderDemo inner class validates and logs BEFORE calling
 * field assignments, which was previously illegal in Java.
 */
public class Java25Demo {

    // ---------------------------------------------------------------
    // Instance main method (JEP 512)
    // No 'static', no String[] args required — Java 25 allows this
    // ---------------------------------------------------------------
    void main() {
        System.out.println("=" .repeat(60));
        System.out.println("  JAVA 25 FEATURES DEMO");
        System.out.println("=" .repeat(60));

        demonstrateInstanceMain();
        demonstrateFlexibleConstructorBody();
        demonstrateCompactStreamPipeline();
    }

    // ---------------------------------------------------------------
    // JEP 512 — instance main: called directly from the instance main
    // ---------------------------------------------------------------
    void demonstrateInstanceMain() {
        System.out.println("\n--- JEP 512: Instance Main Method ---");
        System.out.println("This method is called from an instance main — no 'static' required!");
        System.out.println("Java 25 launches this class by creating an instance automatically.");
    }

    // ---------------------------------------------------------------
    // JEP 513 — Flexible Constructor Body
    // Statements CAN appear before super()/this() in Java 25.
    // Here WorkoutLogger validates input before the super() call.
    // ---------------------------------------------------------------
    void demonstrateFlexibleConstructorBody() {
        System.out.println("\n--- JEP 513: Flexible Constructor Body ---");

        var session = new ValidatedSession("WS-J25", 45);
        System.out.println("Created: " + session.info());

        try {
            var bad = new ValidatedSession("WS-BAD", -5); // Should throw
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Compact stream pipeline — shows modern one-liner stream style
    // ---------------------------------------------------------------
    void demonstrateCompactStreamPipeline() {
        System.out.println("\n--- Compact Stream Pipeline on Sample Data ---");

        var exercise = new CardioExercise("Morning Run", 30, 5.0, 145);
        var sessions = List.of(
            new WorkoutSession("A", LocalDate.now(), WorkoutType.CARDIO,
                               List.of(exercise), 30, "easy"),
            new WorkoutSession("B", LocalDate.now().minusDays(1), WorkoutType.STRENGTH,
                               List.of(exercise), 45, "hard"),
            new WorkoutSession("C", LocalDate.now().minusDays(2), WorkoutType.CARDIO,
                               List.of(exercise), 20, "moderate")
        );

        // Chained intermediates + terminal — groupingBy type → count
        sessions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                         WorkoutSession::type, java.util.stream.Collectors.counting()))
                .forEach((type, count) ->
                    System.out.printf("  %-12s : %d session(s)%n", type, count));
    }

    // ---------------------------------------------------------------
    // Inner class: ValidatedSession demonstrates flexible constructor
    // body — validation logic runs BEFORE field assignment
    // ---------------------------------------------------------------
    static class ValidatedSession {
        private final String sessionId;
        private final int durationMinutes;

        ValidatedSession(String sessionId, int durationMinutes) {
            // JEP 513: statements before field assignments are now legal in Java 25
            // In earlier Java versions this block had to come AFTER this() or super()
            if (durationMinutes <= 0) {
                throw new IllegalArgumentException(
                    "Duration must be positive, got: " + durationMinutes);
            }
            System.out.println("  [JEP 513] Pre-assignment validation passed for: " + sessionId);

            this.sessionId = sessionId;
            this.durationMinutes = durationMinutes;
        }

        String info() {
            return sessionId + " (" + durationMinutes + " min)";
        }
    }
}
