package model;

/**
 * Enum demonstrating Java enumeration types
 * Represents different types of workout activities
 */
public enum WorkoutType {
    CARDIO("Cardiovascular Exercise", 8.0),
    STRENGTH("Strength Training", 6.0),
    FLEXIBILITY("Flexibility & Stretching", 3.0),
    HIIT("High Intensity Interval Training", 10.0),
    YOGA("Yoga & Mindfulness", 4.0);

    private final String description;
    private final double caloriesPerMinute;

    // Constructor for enum
    WorkoutType(String description, double caloriesPerMinute) {
        this.description = description;
        this.caloriesPerMinute = caloriesPerMinute;
    }

    public String getDescription() {
        return description;
    }

    public double getCaloriesPerMinute() {
        return caloriesPerMinute;
    }

    // Method to calculate total calories burned
    public double calculateCalories(int durationMinutes) {
        return caloriesPerMinute * durationMinutes;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.1f cal/min", 
            name(), description, caloriesPerMinute);
    }
}

