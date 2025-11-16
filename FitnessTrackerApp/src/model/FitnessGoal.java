package model;

import java.time.LocalDate;


public record FitnessGoal(
    String goalType,
    double targetValue,
    double currentValue,
    LocalDate deadline,
    boolean achieved
) {
    public FitnessGoal {
        if (targetValue <= 0) {
            throw new IllegalArgumentException("Target value must be positive");
        }
    }

    public double getProgress() {
        return (currentValue / targetValue) * 100.0;
    }

    public String getStatus() {
        if (achieved) return "✓ Achieved";
        if (getProgress() >= 100) return "✓ Target Reached";
        if (getProgress() >= 75) return "Almost There!";
        if (getProgress() >= 50) return "Halfway";
        return "Keep Going!";
    }
}

