package model;

/**
 * Demonstrates inheritance, polymorphism, and sealed interface implementation
 */
public final class StrengthExercise implements Exercise {
    private final String name;
    private final int duration;
    private final int sets;
    private final int reps;
    private final double weight; // in kg

    // Constructor demonstrating this()
    public StrengthExercise(String name, int duration) {
        this(name, duration, 0, 0, 0.0);
    }

    // Full constructor
    public StrengthExercise(String name, int duration, int sets, int reps, double weight) {
        this.name = name;
        this.duration = duration;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public double calculateCaloriesBurned() {
        // Formula: duration * 6 + weight factor
        return duration * 6.0 + (weight * sets * 0.5);
    }

    public double calculateVolume() {
        return sets * reps * weight;
    }

    @Override
    public String toString() {
        return String.format("Strength: %s | Duration: %d min | %dx%d @ %.1fkg | Volume: %.1f | Calories: %.0f", 
            name, duration, sets, reps, weight, calculateVolume(), calculateCaloriesBurned());
    }
}