import model.*;

import java.time.LocalDate;

/**
 * Demonstrates Java 25 preview features.
 * Compile: javac --release 25 --enable-preview Java25Demo.java model/Activity.java model/Running.java model/Cycling.java model/StrengthTraining.java
 * Run:     java  --enable-preview Java25Demo
 *
 * Features:
 *   JEP 512 — Compact source files + instance main methods
 *   JEP 513 — Flexible constructor bodies
 */
class Java25Demo {

    // ─── JEP 512: instance main — no 'public', no 'static', no String[] args ───
    void main() {
        System.out.println("\n==============================");
        System.out.println(" Java 25 Feature Demo");
        System.out.println("==============================");
        demonstrateInstanceMain();
        demonstrateFlexibleConstructorBody();
    }

    void demonstrateInstanceMain() {
        System.out.println("\n=== [JEP 512] Instance main method ===");
        System.out.println("  This class has no 'public' modifier.");
        System.out.println("  main() is an instance method — no 'static', no String[] args.");
        System.out.println("  JVM instantiates the class and calls main() directly.");

        // Also show compact source: use Activity sealed hierarchy inline
        Activity run = new Running(5.0, 6.5);
        System.out.println("  Activity created inline: " + run);
    }

    void demonstrateFlexibleConstructorBody() {
        System.out.println("\n=== [JEP 513] Flexible constructor body ===");
        System.out.println("  Creating ValidatedSession with id='morning-run', duration=45:");
        ValidatedSession s1 = new ValidatedSession("morning-run", 45);
        System.out.println("  Created: " + s1);

        System.out.println("  Creating WorkoutSummaryExtended (extends WorkoutSummaryBase):");
        WorkoutSummaryExtended ext = new WorkoutSummaryExtended("Leg Day", 500, 60);
        System.out.println("  Created: " + ext);

        System.out.println("  Attempting invalid session (blank id):");
        try {
            new ValidatedSession("", 30);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught expected exception: " + e.getMessage());
        }
    }

    // ─── JEP 513: flexible constructor — statements BEFORE field assignment ───
    static class ValidatedSession {
        final String sessionId;
        final int durationMinutes;

        ValidatedSession(String sessionId, int durationMinutes) {
            // Legal in Java 25 BEFORE field assignment:
            if (sessionId == null || sessionId.isBlank())
                throw new IllegalArgumentException("Session ID cannot be blank");
            if (durationMinutes <= 0)
                throw new IllegalArgumentException("Duration must be positive: " + durationMinutes);
            System.out.println("  [JEP 513] Pre-assignment validation passed for: '" + sessionId.trim() + "'");

            // Field assignment follows validation
            this.sessionId = sessionId.trim();
            this.durationMinutes = durationMinutes;
        }

        @Override
        public String toString() {
            return "ValidatedSession[id='" + sessionId + "', duration=" + durationMinutes + "min]";
        }
    }

    // ─── JEP 513: flexible constructor body in inheritance ───────────────────
    static class WorkoutSummaryBase {
        final String name;
        final int calories;

        WorkoutSummaryBase(String name, int calories) {
            this.name = name;
            this.calories = calories;
        }
    }

    static class WorkoutSummaryExtended extends WorkoutSummaryBase {
        final int durationMinutes;

        WorkoutSummaryExtended(String name, int calories, int durationMinutes) {
            // Java 25: statements allowed before super() call
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("Name required");
            int validatedCalories = Math.max(0, calories);
            System.out.println("  [JEP 513] Pre-super validation: calories adjusted to " + validatedCalories);

            super(name, validatedCalories);
            this.durationMinutes = durationMinutes;
        }

        @Override
        public String toString() {
            return "WorkoutSummaryExtended[name='" + name + "', calories=" + calories +
                    ", duration=" + durationMinutes + "min]";
        }
    }
}
