package model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Custom immutable type demonstrating defensive copying
 * and immutability best practices
 */
public final class NutritionPlan {
    private final String planName;
    private final int dailyCalories;
    private final int proteinGrams;
    private final int carbsGrams;
    private final int fatsGrams;
    private final List<String> meals;

    // Constructor with defensive copying
    public NutritionPlan(String planName, int dailyCalories, 
                        int proteinGrams, int carbsGrams, int fatsGrams, 
                        List<String> meals) {
        this.planName = planName;
        this.dailyCalories = dailyCalories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatsGrams = fatsGrams;
        // Defensive copy - preventing external modification
        this.meals = new ArrayList<>(meals);
    }

    // Getters with defensive copying for mutable fields
    public String getPlanName() {
        return planName;
    }

    public int getDailyCalories() {
        return dailyCalories;
    }

    public int getProteinGrams() {
        return proteinGrams;
    }

    public int getCarbsGrams() {
        return carbsGrams;
    }

    public int getFatsGrams() {
        return fatsGrams;
    }

    // Return unmodifiable list to preserve immutability
    public List<String> getMeals() {
        return Collections.unmodifiableList(meals);
    }

    public int getTotalMacros() {
        return proteinGrams + carbsGrams + fatsGrams;
    }

    public double getProteinPercentage() {
        int proteinCals = proteinGrams * 4;
        return (proteinCals / (double) dailyCalories) * 100;
    }

    public double getCarbsPercentage() {
        int carbsCals = carbsGrams * 4;
        return (carbsCals / (double) dailyCalories) * 100;
    }

    public double getFatsPercentage() {
        int fatsCals = fatsGrams * 9;
        return (fatsCals / (double) dailyCalories) * 100;
    }

    @Override
    public String toString() {
        return String.format("%s: %d cal/day | P: %dg (%.1f%%) | C: %dg (%.1f%%) | F: %dg (%.1f%%) | %d meals",
            planName, dailyCalories, 
            proteinGrams, getProteinPercentage(),
            carbsGrams, getCarbsPercentage(),
            fatsGrams, getFatsPercentage(),
            meals.size());
    }
}