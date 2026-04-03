import model.WorkoutSession;
import model.WorkoutType;
import model.CardioExercise;

import java.time.LocalDate;
import java.util.List;

/**
 * OOP2 - JAVA 21/25 FEATURES DEMO
 *
 * Demonstrates (JEP 445 / JEP 512): Unnamed classes and instance main method.
 * In Java 21 this was a preview feature (JEP 445).
 * In Java 25 it became standard (JEP 512).
 *
 * The instance main method (no 'static', no String[] args) is the key feature.
 * ValidatedSession shows flexible constructor body (JEP 482 / JEP 513):
 * statements may now appear BEFORE field assignments in a constructor.
 *
 * ---------------------------------------------------------------
 * Compile + run on JDK 21 (preview):
 *   javac --release 21 --enable-preview Java25Demo.java \
 *         model/CardioExercise.java model/WorkoutSession.java model/WorkoutType.java
 *   java --enable-preview Java25Demo
 *
 * Compile + run on JDK 25 (standard):
 *   javac Java25Demo.java model/CardioExercise.java \
 *         model/WorkoutSession.java model/WorkoutType.java
 *   java Java25Demo
 * ---------------------------------------------------------------
 */
public class Java25Demo {

    // ---------------------------------------------------------------
    // Instance main method (JEP 445 preview in Java 21; JEP 512 in Java 25)
    // No 'static', no String[] args required.
    // ---------------------------------------------------------------
    void main() {
        System.out.println("=".repeat(60));
        System.out.println("  JAVA 21/25 PREVIEW FEATURES DEMO");
        System.out.println("=".repeat(60));

        demonstrateInstanceMain();
        demonstrateFlexibleConstructorBody();
        demonstrateCompactStreamPipeline();
    }

    // ---------------------------------------------------------------
    // JEP 445 / JEP 512 — instance main
    // ---------------------------------------------------------------
    void demonstrateInstanceMain() {
        System.out.println("\n--- JEP 445/512: Instance Main Method ---");
        System.out.println("This method is non-static — Java launches it by creating an instance.");
        System.out.println("No 'public static void main(String[] args)' required.");
        System.out.println("Version available: preview in Java 21 (JEP 445), standard in Java 25 (JEP 512).");
    }

    // ---------------------------------------------------------------
    // JEP 482 / JEP 513 — Flexible Constructor Body
    // Statements may now appear BEFORE field assignments.
    // Previously Java required super()/this() to be the very first statement.
    // ---------------------------------------------------------------
    void demonstrateFlexibleConstructorBody() {
        System.out.println("\n--- JEP 482/513: Flexible Constructor Body ---");

        var session = new ValidatedSession("WS-J21", 45);
        System.out.println("Created: " + session.info());

        try {
            var bad = new ValidatedSession("WS-BAD", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Compact stream pipeline to round out the demo
    // ---------------------------------------------------------------
    void demonstrateCompactStreamPipeline() {
        System.out.println("\n--- Compact Stream Pipeline on Sample Data ---");

        var exercise = new CardioExercise("Morning Run", 30, 5.0, 145);
        var sessions = List.of(
            new WorkoutSession("A", LocalDate.now(),             WorkoutType.CARDIO,    List.of(exercise), 30, "easy"),
            new WorkoutSession("B", LocalDate.now().minusDays(1), WorkoutType.STRENGTH, List.of(exercise), 45, "hard"),
            new WorkoutSession("C", LocalDate.now().minusDays(2), WorkoutType.CARDIO,   List.of(exercise), 20, "moderate")
        );

        sessions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                         WorkoutSession::type, java.util.stream.Collectors.counting()))
                .forEach((type, count) ->
                    System.out.printf("  %-12s : %d session(s)%n", type, count));
    }

    // ---------------------------------------------------------------
    // ValidatedSession — demonstrates flexible constructor body.
    // Pre-assignment logic (validation + logging) appears BEFORE
    // 'this.x = x' assignments, which was illegal before JEP 482.
    // ---------------------------------------------------------------
    static class ValidatedSession {
        private final String sessionId;
        private final int durationMinutes;

        ValidatedSession(String sessionId, int durationMinutes) {
            // JEP 482/513: statements before field assignments are now legal.
            // In Java < 21 this block had to come AFTER all this()/super() calls.
            if (durationMinutes <= 0) {
                throw new IllegalArgumentException(
                    "Duration must be positive, got: " + durationMinutes);
            }
            System.out.println("  [JEP 482/513] Pre-assignment validation passed for: " + sessionId);

            // Field assignments happen AFTER validation — the flexible constructor body
            this.sessionId      = sessionId;
            this.durationMinutes = durationMinutes;
        }

        String info() {
            return sessionId + " (" + durationMinutes + " min)";
        }
    }
}
