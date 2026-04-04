package model;

/**
 * Permitted subtype of sealed Activity.
 * Demonstrates: Sealed classes (OOP2 Fundamentals)
 */
public record Cycling(double distanceKm, double speedKmh) implements Activity {
    @Override
    public String activityType() { return "Cycling"; }

    @Override
    public String toString() {
        return String.format("Cycling[%.1f km @ %.1f km/h]", distanceKm, speedKmh);
    }
}
