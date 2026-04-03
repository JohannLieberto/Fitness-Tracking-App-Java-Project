package model;

/**
 * A strength training activity — permitted subtype of the sealed Activity interface.
 * Implemented as a record.
 */
public record StrengthTraining(String exerciseName, int sets, int reps, double weightKg) implements Activity {

    @Override
    public String name() {
        return "Strength Training";
    }

    @Override
    public int estimatedCalories() {
        // Simple estimate: 5 kcal per set
        return sets * 5;
    }
}
