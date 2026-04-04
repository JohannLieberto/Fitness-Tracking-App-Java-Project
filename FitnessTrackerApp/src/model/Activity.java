package model;

/**
 * Sealed interface — Activity hierarchy.
 * Demonstrates: Sealed classes + pattern matching switch (OOP2 Fundamentals)
 *
 * permits: Running, Cycling, StrengthTraining
 */
public sealed interface Activity permits Running, Cycling, StrengthTraining {
    String activityType();
}
