package model;

/**
 * A cycling activity — permitted subtype of the sealed Activity interface.
 * Implemented as a record.
 */
public record Cycling(double distanceKm, int elevationGainM) implements Activity {

    @Override
    public String name() {
        return "Cycling";
    }

    @Override
    public int estimatedCalories() {
        // Simple estimate: 30 kcal per km + 10 kcal per 100m elevation
        return (int) (distanceKm * 30 + (elevationGainM / 100.0) * 10);
    }
}
