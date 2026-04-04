package model;

import java.time.LocalDate;

/**
 * Immutable goal record.
 * Demonstrates: Records (OOP2 Fundamentals)
 */
public record UserGoal(
        String label,
        int targetCaloriesPerWeek,
        LocalDate deadline
) {
    public UserGoal {
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Goal label required");
        if (targetCaloriesPerWeek <= 0) throw new IllegalArgumentException("Target must be positive");
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(deadline);
    }

    @Override
    public String toString() {
        return String.format("UserGoal[label='%s', target=%d kcal/wk, deadline=%s, expired=%b]",
                label, targetCaloriesPerWeek, deadline, isExpired());
    }
}
