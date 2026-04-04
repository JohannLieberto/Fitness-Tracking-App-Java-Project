package model;

public final class CardioExercise implements Exercise {
    private final String name;
    private final int duration;
    private final double distance;
    private final int heartRate;

    public CardioExercise(String name, int duration) {
        this(name, duration, 0.0, 0);
    }

    /** heartRate accepted as double for convenience, stored as int */
    public CardioExercise(String name, int duration, double distance, double heartRate) {
        this.name = name;
        this.duration = duration;
        this.distance = distance;
        this.heartRate = (int) heartRate;
    }

    @Override public String getName()     { return name; }
    @Override public int    getDuration() { return duration; }
    public double getDistance()           { return distance; }
    public int    getHeartRate()          { return heartRate; }

    @Override
    public double calculateCaloriesBurned() {
        return duration * 8.0 + (distance * 50);
    }

    public double calculatePace() {
        return distance > 0 ? duration / distance : 0;
    }

    @Override
    public String toString() {
        return String.format("Cardio: %s | Duration: %d min | Distance: %.2f km | HR: %d bpm | Calories: %.0f",
            name, duration, distance, heartRate, calculateCaloriesBurned());
    }
}
