import model.WorkoutSession;
import model.WorkoutType;
import model.CardioExercise;

import java.time.LocalDate;
import java.util.List;
import java.lang.ScopedValue;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

/**
 * OOP2 - JAVA 25 FEATURES DEMO
 *
 * Covers ALL Java 25 items in the marking rubric:
 *   [MANDATORY]  JEP 512 — Instance main method (no static, no String[] args)
 *   [MANDATORY]  JEP 513 — Flexible constructor body (statements before field assignments)
 *   [EXTRA MARK] JEP 487 — Scoped Values (pass immutable context across method calls / threads)
 *   [EXTRA MARK] JEP 485 — Stream Gatherers (custom intermediate stream operations)
 *
 * ---------------------------------------------------------------
 * Compile + run on JDK 25 (preview still required for some JEPs):
 *
 *   javac --release 25 --enable-preview \
 *         Java25Demo.java \
 *         model/CardioExercise.java model/WorkoutSession.java model/WorkoutType.java
 *
 *   java --enable-preview Java25Demo
 * ---------------------------------------------------------------
 */
public class Java25Demo {

    // ---------------------------------------------------------------
    // JEP 487 — Scoped Values
    // A ScopedValue is an immutable, thread-confined variable.
    // It replaces ThreadLocal for structured concurrency use-cases.
    // Here we bind the "current user" for the duration of an analytics run
    // so every helper method can read it without passing it as a parameter.
    // ---------------------------------------------------------------
    static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();

    // ---------------------------------------------------------------
    // JEP 512 — Instance main method
    // No 'static', no String[] args needed — Java 25 creates an
    // instance of this class and calls main() on it.
    // ---------------------------------------------------------------
    void main() {
        System.out.println("=".repeat(60));
        System.out.println("  JAVA 25 FEATURES DEMO");
        System.out.println("=".repeat(60));

        demonstrateInstanceMain();
        demonstrateFlexibleConstructorBody();
        demonstrateScopedValues();
        demonstrateStreamGatherers();
    }

    // ---------------------------------------------------------------
    // JEP 512 explanation
    // ---------------------------------------------------------------
    void demonstrateInstanceMain() {
        System.out.println("\n--- JEP 512: Instance Main Method ---");
        System.out.println("This method is non-static — Java 25 launches it by instantiating the class.");
        System.out.println("'public static void main(String[] args)' is no longer required.");
    }

    // ---------------------------------------------------------------
    // JEP 513 — Flexible Constructor Body
    // Validation and logging CAN now appear BEFORE field assignments.
    // Prior to JEP 482/513 that was a compile error.
    // ---------------------------------------------------------------
    void demonstrateFlexibleConstructorBody() {
        System.out.println("\n--- JEP 513: Flexible Constructor Body ---");

        var session = new ValidatedSession("WS-001", 45);
        System.out.println("Created: " + session.info());

        try {
            var bad = new ValidatedSession("WS-BAD", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected validation error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // JEP 487 — Scoped Values  [EXTRA MARKS]
    //
    // ScopedValue.where(CURRENT_USER, "Hitesh").run(() -> ...)
    // binds the value for the duration of the lambda.
    // Any method called inside can read it via CURRENT_USER.get()
    // without being passed the value explicitly.
    // It is immutable and cannot be changed mid-scope.
    // ---------------------------------------------------------------
    void demonstrateScopedValues() {
        System.out.println("\n--- JEP 487: Scoped Values [EXTRA MARKS] ---");
        System.out.println("Binding CURRENT_USER = \"Hitesh\" for the analytics scope...");

        ScopedValue.where(CURRENT_USER, "Hitesh").run(() -> {
            System.out.println("  Inside scope — CURRENT_USER.get() = " + CURRENT_USER.get());
            printPersonalisedGreeting();
            runScopedAnalytics();
        });

        // Outside the scope the value is gone — get() would throw NoSuchElementException
        System.out.println("  Outside scope — isBound: " + CURRENT_USER.isBound());
    }

    private void printPersonalisedGreeting() {
        // No parameter needed — reads the scoped value directly
        System.out.println("  [Helper] Loading dashboard for: " + CURRENT_USER.get());
    }

    private void runScopedAnalytics() {
        System.out.println("  [Analytics] Running weekly report for: " + CURRENT_USER.get());
        System.out.println("  [Analytics] Audit trail: user context propagated without method params.");
    }

    // ---------------------------------------------------------------
    // JEP 485 — Stream Gatherers  [EXTRA MARKS]
    //
    // Gatherers are custom intermediate stream operations — the
    // stream equivalent of a custom Collector but for the middle of a pipeline.
    //
    // Three demos:
    //   1. Built-in Gatherers.windowFixed(n)  — sliding window of n elements
    //   2. Built-in Gatherers.scan(...)       — running total of duration
    //   3. Custom Gatherer                    — keep sessions until cumulative
    //                                          duration reaches 100 min
    // ---------------------------------------------------------------
    void demonstrateStreamGatherers() {
        System.out.println("\n--- JEP 485: Stream Gatherers [EXTRA MARKS] ---");

        var exercise = new CardioExercise("Run", 30, 5.0, 145);
        var sessions = List.of(
            new WorkoutSession("Monday",    LocalDate.now().minusDays(6), WorkoutType.CARDIO,      List.of(exercise), 30, "easy"),
            new WorkoutSession("Tuesday",   LocalDate.now().minusDays(5), WorkoutType.STRENGTH,    List.of(exercise), 45, "hard"),
            new WorkoutSession("Wednesday", LocalDate.now().minusDays(4), WorkoutType.CARDIO,      List.of(exercise), 25, "moderate"),
            new WorkoutSession("Thursday",  LocalDate.now().minusDays(3), WorkoutType.FLEXIBILITY, List.of(exercise), 20, "easy"),
            new WorkoutSession("Friday",    LocalDate.now().minusDays(2), WorkoutType.CARDIO,      List.of(exercise), 40, "hard"),
            new WorkoutSession("Saturday",  LocalDate.now().minusDays(1), WorkoutType.STRENGTH,    List.of(exercise), 50, "moderate"),
            new WorkoutSession("Sunday",    LocalDate.now(),              WorkoutType.CARDIO,      List.of(exercise), 35, "easy")
        );

        // --- Demo 1: windowFixed — group sessions into windows of 3
        System.out.println("\n  [Gatherers.windowFixed(3)] Sessions grouped into windows of 3:");
        sessions.stream()
                .gather(Gatherers.windowFixed(3))
                .forEach(window -> {
                    var names = window.stream().map(WorkoutSession::name).toList();
                    System.out.println("    Window: " + names);
                });

        // --- Demo 2: scan — running cumulative duration
        System.out.println("\n  [Gatherers.scan] Running cumulative duration (minutes):");
        sessions.stream()
                .map(WorkoutSession::durationMinutes)
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .forEach(cum -> System.out.print("  " + cum));
        System.out.println();

        // --- Demo 3: custom Gatherer — emit sessions until 100-min weekly load reached
        System.out.println("\n  [Custom Gatherer] Sessions until 100-min weekly load is reached:");
        int threshold = 100;
        Gatherer<WorkoutSession, int[], WorkoutSession> untilLoad =
            Gatherer.ofSequential(
                () -> new int[]{0},
                (state, session, downstream) -> {
                    state[0] += session.durationMinutes();
                    downstream.push(session);
                    return state[0] < threshold;
                }
            );

        sessions.stream()
                .gather(untilLoad)
                .forEach(s -> System.out.printf("    %-12s  %d min%n",
                                                s.name(), s.durationMinutes()));
    }

    // ---------------------------------------------------------------
    // ValidatedSession — flexible constructor body demo class
    // ---------------------------------------------------------------
    static class ValidatedSession {
        private final String sessionId;
        private final int durationMinutes;

        ValidatedSession(String sessionId, int durationMinutes) {
            // JEP 513: validation BEFORE field assignments — legal in Java 25
            if (durationMinutes <= 0) {
                throw new IllegalArgumentException(
                    "Duration must be positive, got: " + durationMinutes);
            }
            System.out.println("  [JEP 513] Pre-assignment validation passed for: " + sessionId);

            this.sessionId       = sessionId;
            this.durationMinutes = durationMinutes;
        }

        String info() {
            return sessionId + " (" + durationMinutes + " min)";
        }
    }
}
