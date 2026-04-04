package model;

import java.time.LocalDate;

/**
 * Core domain class representing a single workout session.
 */
public class WorkoutSession {

    private final String name;
    private final LocalDate date;
    private final int durationMinutes;
    private final int caloriesBurned;
    private final WorkoutType workoutType;

    public WorkoutSession(String name, LocalDate date, int durationMinutes,
                          int caloriesBurned, WorkoutType workoutType) {
        this.name = name;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.workoutType = workoutType;
    }

    public String getName()            { return name; }
    public LocalDate getDate()         { return date; }
    public int getDurationMinutes()    { return durationMinutes; }
    public int getCaloriesBurned()     { return caloriesBurned; }
    public WorkoutType getWorkoutType(){ return workoutType; }

    @Override
    public String toString() {
        return String.format("WorkoutSession[name='%s', date=%s, duration=%dmin, calories=%d, type=%s]",
                name, date, durationMinutes, caloriesBurned, workoutType);
    }
}
