package model;

/**
 * A running activity — permitted subtype of the sealed Activity interface.
 * Implemented as a record for immutability and compact syntax (also demos Records).
 */
public record Running(double distanceKm, double paceKmh) implements Activity {

    @Override
    public String name() {
        return "Running";
    }

    @Override
    public int estimatedCalories() {
        // Simple estimate: 60 kcal per km
        return (int) (distanceKm * 60);
    }
}
