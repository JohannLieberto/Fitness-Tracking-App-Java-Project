package model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User — core domain class.
 *
 * Demonstrates:
 *  - Encapsulation (private fields, public getters)
 *  - Defensive copying of mutable collections
 *  - Stream operations using WorkoutSession record accessors
 */
public class User {

    private final String userId;
    private String name;
    private int age;
    private double weightKg;
    private double heightCm;
    private final List<WorkoutSession> workoutHistory;
    private final List<FitnessGoal> goals;

    public User(String userId, String name, int age, double weightKg, double heightCm) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.workoutHistory = new ArrayList<>();
        this.goals = new ArrayList<>();
    }

    // ----------------------------------------------------------------
    // Mutators
    // ----------------------------------------------------------------
    public void addWorkoutSession(WorkoutSession session) {
        workoutHistory.add(session);
    }

    public void addGoal(FitnessGoal goal) {
        goals.add(goal);
    }

    // ----------------------------------------------------------------
    // Accessors — defensive copy of collections
    // ----------------------------------------------------------------
    public String getUserId()                    { return userId; }
    public String getName()                      { return name; }
    public int    getAge()                       { return age; }
    public double getWeightKg()                  { return weightKg; }
    public double getHeightCm()                  { return heightCm; }
    public List<WorkoutSession> getWorkoutHistory() { return Collections.unmodifiableList(workoutHistory); }
    public List<FitnessGoal>    getGoals()          { return Collections.unmodifiableList(goals); }

    public void setName(String name)         { this.name = name; }
    public void setAge(int age)              { this.age = age; }
    public void setWeightKg(double w)        { this.weightKg = w; }
    public void setHeightCm(double h)        { this.heightCm = h; }

    // ----------------------------------------------------------------
    // Derived / computed properties
    // ----------------------------------------------------------------
    public double getBMI() {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 100.0) / 100.0;
    }

    public int getTotalWorkouts() {
        return workoutHistory.size();
    }

    /** Total calories burned — uses record accessor caloriesBurned() */
    public double getTotalCaloriesBurned() {
        return workoutHistory.stream()
                .mapToInt(WorkoutSession::caloriesBurned)
                .sum();
    }

    /** Workouts within a date range — uses record accessor date() */
    public List<WorkoutSession> getWorkoutsInRange(LocalDate startDate, LocalDate endDate) {
        return workoutHistory.stream()
                .filter(s -> !s.date().isBefore(startDate) && !s.date().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /** Monthly calorie totals — uses record accessors date() and caloriesBurned() */
    public int[] getMonthlyCalories() {
        int[] monthlyCalories = new int[12];
        for (WorkoutSession session : workoutHistory) {
            int month = session.date().getMonthValue() - 1; // 0-indexed
            monthlyCalories[month] += session.caloriesBurned();
        }
        System.out.println("Monthly calories tracking initialized (12 months)");
        return monthlyCalories;
    }

    /** Total workout duration — uses record accessor totalDuration() */
    public long getTotalDurationMinutes() {
        return workoutHistory.stream()
                .mapToLong(WorkoutSession::totalDuration)
                .sum();
    }

    @Override
    public String toString() {
        return String.format(
            "User[id=%s, name=%s, age=%d, weight=%.1fkg, height=%.1fcm, bmi=%.2f, workouts=%d, goals=%d]",
            userId, name, age, weightKg, heightCm, getBMI(), workoutHistory.size(), goals.size()
        );
    }
}
