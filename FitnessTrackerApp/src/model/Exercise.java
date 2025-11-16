package model;

public sealed interface Exercise permits CardioExercise, StrengthExercise {
    
    String getName();
    int getDuration();
    double calculateCaloriesBurned();
    
    // Default interface method (Java 8+)
    default String getExerciseInfo() {
        return String.format("%s - %d minutes - %.0f calories", 
            getName(), getDuration(), calculateCaloriesBurned());
    }
    
    // Static interface method
    static String getGeneralAdvice() {
        return "Always warm up before exercise and cool down afterwards!";
    }
    
    // Private interface method (Java 9+)
    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours > 0 ? String.format("%dh %dm", hours, mins) : String.format("%dm", mins);
    }
    
    // Default method using private method
    default String getFormattedDuration() {
        return formatDuration(getDuration());
    }
}