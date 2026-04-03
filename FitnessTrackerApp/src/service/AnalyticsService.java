package service;

import model.WorkoutSession;
import model.WorkoutType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * OOP2 - CONCURRENCY DEMO
 * Demonstrates: ExecutorService, Callable, Future, invokeAll
 *
 * Uses a thread pool to compute multiple fitness analytics tasks
 * in parallel, simulating a real reporting workload.
 */
public class AnalyticsService {

    // ----------------------------------------------------------------
    // Result record — immutable data carrier from each Callable task
    // ----------------------------------------------------------------
    public record AnalyticsResult(String metricName, double value, String unit) {
        @Override
        public String toString() {
            return String.format("  %-35s  %.2f %s", metricName, value, unit);
        }
    }

    /**
     * Runs several analytics tasks concurrently and collects results.
     *
     * @param sessions the full workout history to analyse
     * @return list of completed AnalyticsResult objects
     */
    public List<AnalyticsResult> runConcurrentAnalytics(List<WorkoutSession> sessions) {

        // Fixed thread pool — one thread per logical CPU core
        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());

        // Define independent tasks as Callable lambdas
        List<Callable<AnalyticsResult>> tasks = List.of(

            // Task 1 — total calories across all sessions
            () -> {
                double total = sessions.stream()
                        .mapToDouble(WorkoutSession::calculateTotalCalories)
                        .sum();
                return new AnalyticsResult("Total Calories Burned", total, "kcal");
            },

            // Task 2 — average session duration in minutes
            () -> {
                double avg = sessions.stream()
                        .mapToInt(WorkoutSession::durationMinutes)
                        .average()
                        .orElse(0.0);
                return new AnalyticsResult("Avg Session Duration", avg, "min");
            },

            // Task 3 — longest single session (calories)
            () -> {
                double max = sessions.stream()
                        .mapToDouble(WorkoutSession::calculateTotalCalories)
                        .max()
                        .orElse(0.0);
                return new AnalyticsResult("Best Session (Calories)", max, "kcal");
            },

            // Task 4 — cardio session count
            () -> {
                long cardioCount = sessions.stream()
                        .filter(s -> s.type() == WorkoutType.CARDIO)
                        .count();
                return new AnalyticsResult("Cardio Sessions", cardioCount, "sessions");
            },

            // Task 5 — strength session count
            () -> {
                long strengthCount = sessions.stream()
                        .filter(s -> s.type() == WorkoutType.STRENGTH)
                        .count();
                return new AnalyticsResult("Strength Sessions", strengthCount, "sessions");
            },

            // Task 6 — calories-per-minute efficiency
            () -> {
                double efficiency = sessions.stream()
                        .mapToDouble(s -> s.calculateTotalCalories() /
                                Math.max(1, s.durationMinutes()))
                        .average()
                        .orElse(0.0);
                return new AnalyticsResult("Avg Efficiency", efficiency, "kcal/min");
            }
        );

        List<AnalyticsResult> results = new java.util.ArrayList<>();
        try {
            // invokeAll blocks until ALL tasks finish (or timeout)
            List<Future<AnalyticsResult>> futures = executor.invokeAll(tasks);

            for (Future<AnalyticsResult> future : futures) {
                results.add(future.get());   // get() retrieves result; throws on task failure
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Analytics interrupted: " + e.getMessage());
        } catch (ExecutionException e) {
            System.err.println("Analytics task failed: " + e.getCause().getMessage());
        } finally {
            executor.shutdown();  // Always release the thread pool
        }

        return results;
    }

    /**
     * Groups sessions by WorkoutType and returns a summary map.
     * Uses Collectors.groupingBy + summingDouble — stream terminal op.
     */
    public Map<WorkoutType, Double> caloriesByType(List<WorkoutSession> sessions) {
        return sessions.stream()
                .collect(Collectors.groupingBy(
                        WorkoutSession::type,
                        Collectors.summingDouble(WorkoutSession::calculateTotalCalories)
                ));
    }
}
