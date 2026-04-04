package model;

/**
 * Enum of supported workout categories.
 * Used in switch expressions to demonstrate arrow-syntax switch (OOP2 Fundamentals).
 * getDisplayName() and getCaloriesPerMinute() used throughout Main and services.
 */
public enum WorkoutType {
    CARDIO("Cardio", 8.0),
    STRENGTH("Strength Training", 5.0),
    FLEXIBILITY("Flexibility", 3.0),
    HIIT("HIIT", 12.0),
    YOGA("Yoga", 2.5),
    SPORT("Sport", 7.0);

    private final String displayName;
    private final double caloriesPerMinute;

    WorkoutType(String displayName, double caloriesPerMinute) {
        this.displayName = displayName;
        this.caloriesPerMinute = caloriesPerMinute;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCaloriesPerMinute() {
        return caloriesPerMinute;
    }
}
