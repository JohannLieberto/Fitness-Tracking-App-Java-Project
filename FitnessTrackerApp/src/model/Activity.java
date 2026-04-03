package model;

/**
 * Sealed interface representing a fitness activity.
 * Permitted subtypes: Running, Cycling, StrengthTraining.
 * Used to demonstrate sealed classes + pattern matching switch.
 */
public sealed interface Activity permits Running, Cycling, StrengthTraining {

    /** Returns the name of this activity. */
    String name();

    /** Estimated calories burned for this activity. */
    int estimatedCalories();

    /** Human-readable summary used in screencast demo. */
    static String describe(Activity activity) {
        return switch (activity) {
            case Running r    -> String.format("Running %.1f km at %.1f km/h — ~%d kcal",
                                    r.distanceKm(), r.paceKmh(), r.estimatedCalories());
            case Cycling c    -> String.format("Cycling %.1f km (elevation +%dm) — ~%d kcal",
                                    c.distanceKm(), c.elevationGainM(), c.estimatedCalories());
            case StrengthTraining s -> String.format("Strength: %s x%d sets x%d reps @ %.1f kg — ~%d kcal",
                                    s.exerciseName(), s.sets(), s.reps(), s.weightKg(), s.estimatedCalories());
        };
    }
}
