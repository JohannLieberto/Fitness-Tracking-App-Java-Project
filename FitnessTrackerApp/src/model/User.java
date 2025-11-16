package model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main User class demonstrating:
 * - Encapsulation
 * - Method overloading
 * - Varargs
 * - LVTI (Local Variable Type Inference)
 * - Arrays
 * - Java Core API (String, StringBuilder, List/ArrayList, Date API)
 */
public class User {
    // Private fields demonstrating encapsulation
    private String userId;
    private String name;
    private int age;
    private double weight; // in kg
    private double height; // in cm
    private List<WorkoutSession> workoutHistory;
    private List<FitnessGoal> goals;
    private NutritionPlan nutritionPlan;

    // Constructor 1 - basic (demonstrating this())
    public User(String userId, String name) {
        this(userId, name, 0, 0.0, 0.0);
    }

    // Constructor 2 - with age (demonstrating this())
    public User(String userId, String name, int age) {
        this(userId, name, age, 0.0, 0.0);
    }

    // Constructor 3 - full constructor
    public User(String userId, String name, int age, double weight, double height) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.workoutHistory = new ArrayList<>();
        this.goals = new ArrayList<>();
    }

    // Encapsulation - getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<WorkoutSession> getWorkoutHistory() {
        return new ArrayList<>(workoutHistory); // Defensive copy
    }

    public List<FitnessGoal> getGoals() {
        return new ArrayList<>(goals); // Defensive copy
    }

    public NutritionPlan getNutritionPlan() {
        return nutritionPlan;
    }

    public void setNutritionPlan(NutritionPlan nutritionPlan) {
        this.nutritionPlan = nutritionPlan;
    }

    // Method overloading - addWorkoutSession
    public void addWorkoutSession(WorkoutSession session) {
        this.workoutHistory.add(session);
    }

    // Overloaded version with varargs
    public void addWorkoutSessions(WorkoutSession... sessions) {
        // Demonstrating varargs and LVTI
        var sessionList = Arrays.asList(sessions);
        this.workoutHistory.addAll(sessionList);
    }

    // Method demonstrating StringBuilder and String API
    public String getUserProfile() {
        // LVTI (Local Variable Type Inference)
        var profile = new StringBuilder();
        profile.append("=== User Profile ===\n");
        profile.append("ID: ").append(this.userId).append("\n");
        profile.append("Name: ").append(this.name).append("\n");
        profile.append("Age: ").append(this.age).append(" years\n");
        profile.append("Weight: ").append(this.weight).append(" kg\n");
        profile.append("Height: ").append(this.height).append(" cm\n");
        profile.append("BMI: ").append(String.format("%.2f", calculateBMI())).append("\n");
        profile.append("Total Workouts: ").append(workoutHistory.size()).append("\n");
        profile.append("Active Goals: ").append(goals.size()).append("\n");
        
        return profile.toString();
    }

    // Calculate BMI
    public double calculateBMI() {
        if (height == 0) return 0;
        var heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    // Method using List/ArrayList
    public List<WorkoutSession> getRecentWorkouts(int count) {
        var size = workoutHistory.size();
        var startIndex = Math.max(0, size - count);
        return new ArrayList<>(workoutHistory.subList(startIndex, size));
    }

    // Method demonstrating Date API
    public List<WorkoutSession> getWorkoutsInDateRange(LocalDate startDate, LocalDate endDate) {
        var result = new ArrayList<WorkoutSession>();
        for (var session : workoutHistory) {
            if (!session.date().isBefore(startDate) && !session.date().isAfter(endDate)) {
                result.add(session);
            }
        }
        return result;
    }

    // Method using arrays
    public double[] getMonthlyCaloriesBurned() {
        var monthlyCalories = new double[12]; // Array for 12 months
        
        for (var session : workoutHistory) {
            int month = session.date().getMonthValue() - 1; // 0-indexed
            monthlyCalories[month] += session.calculateTotalCalories();
        }
        
        return monthlyCalories;
    }

    public void addGoal(FitnessGoal goal) {
        this.goals.add(goal);
    }

    public long getTotalWorkoutDuration() {
        return workoutHistory.stream()
            .mapToLong(WorkoutSession::totalDuration)
            .sum();
    }

    public double getTotalCaloriesBurned() {
        return workoutHistory.stream()
            .mapToDouble(WorkoutSession::calculateTotalCalories)
            .sum();
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, age=%d, workouts=%d]", 
            userId, name, age, workoutHistory.size());
    }
}

