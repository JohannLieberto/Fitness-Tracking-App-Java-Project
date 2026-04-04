package model;

/**
 * Permitted subtype of sealed Activity.
 * Demonstrates: Sealed classes (OOP2 Fundamentals)
 */
public record Running(double distanceKm, double paceMinPerKm) implements Activity {
    @Override
    public String activityType() { return "Running"; }

    @Override
    public String toString() {
        return String.format("Running[%.1f km @ %.1f min/km]", distanceKm, paceMinPerKm);
    }
}
