package model;

/**
 * Permitted subtype of sealed Activity.
 * Demonstrates: Sealed classes (OOP2 Fundamentals)
 */
public record StrengthTraining(String exerciseName, double weightKg, int reps) implements Activity {
    @Override
    public String activityType() { return "StrengthTraining"; }

    @Override
    public String toString() {
        return String.format("StrengthTraining[%s %.1f kg x %d reps]", exerciseName, weightKg, reps);
    }
}
