package model;


public final class CardioExercise implements Exercise {
    private final String name;
    private final int duration;
    private final double distance; // in kilometers
    private final int heartRate;

    // Constructor demonstrating this()
    public CardioExercise(String name, int duration) {
        this(name, duration, 0.0, 0);
    }

    // Full constructor
    public CardioExercise(String name, int duration, double distance, int heartRate) {
        this.name = name;
        this.duration = duration;
        this.distance = distance;
        this.heartRate = heartRate;
    }

    @Override
    public String getName() {
        return this.name; // Demonstrating this.
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    public double getDistance() {
        return distance;
    }

    public int getHeartRate() {
        return heartRate;
    }

    @Override
    public double calculateCaloriesBurned() {
        // Basic formula: duration * 8 + distance bonus
        return duration * 8.0 + (distance * 50);
    }

    public double calculatePace() {
        if (distance > 0) {
            return duration / distance; // minutes per km
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Cardio: %s | Duration: %d min | Distance: %.2f km | HR: %d bpm | Calories: %.0f", 
            name, duration, distance, heartRate, calculateCaloriesBurned());
    }
}
