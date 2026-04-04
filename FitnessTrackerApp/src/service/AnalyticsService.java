package service;

import model.WorkoutSession;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Demonstrates OOP2 Advanced: Concurrency
 *   - ExecutorService (fixed thread pool)
 *   - Callable<AnalysisResult>
 *   - invokeAll() + Future<T>
 */
public class AnalyticsService {

    /** Value object returned by each parallel task */
    public record AnalysisResult(String metricName, String value) {
        @Override public String toString() {
            return String.format("  %-28s : %s", metricName, value);
        }
    }

    public void runAnalytics(List<WorkoutSession> workouts) throws InterruptedException {
        System.out.println("\n=== [CONCURRENCY] ExecutorService + Callable + invokeAll ===");

        // Immutable snapshot — avoids ConcurrentModificationException
        List<WorkoutSession> snapshot = Collections.unmodifiableList(new ArrayList<>(workouts));

        List<Callable<AnalysisResult>> tasks = List.of(
                () -> new AnalysisResult("Total calories",
                        String.valueOf(snapshot.stream().mapToInt(WorkoutSession::getCaloriesBurned).sum())),

                () -> new AnalysisResult("Avg duration (min)",
                        String.format("%.1f", snapshot.stream()
                                .mapToInt(WorkoutSession::getDurationMinutes).average().orElse(0))),

                () -> new AnalysisResult("Longest session",
                        snapshot.stream()
                                .max(Comparator.comparing(WorkoutSession::getDurationMinutes))
                                .map(w -> w.getName() + " (" + w.getDurationMinutes() + "min)")
                                .orElse("none")),

                () -> new AnalysisResult("Session count",
                        String.valueOf(snapshot.size())),

                () -> new AnalysisResult("Unique workout types",
                        snapshot.stream()
                                .map(w -> w.getWorkoutType().name())
                                .distinct()
                                .sorted()
                                .collect(Collectors.joining(", "))),

                () -> new AnalysisResult("Most recent session",
                        snapshot.stream()
                                .max(Comparator.comparing(WorkoutSession::getDate))
                                .map(w -> w.getName() + " on " + w.getDate())
                                .orElse("none"))
        );

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            List<Future<AnalysisResult>> futures = pool.invokeAll(tasks);
            for (Future<AnalysisResult> future : futures) {
                System.out.println(future.get());
            }
        } catch (ExecutionException e) {
            System.err.println("Analytics task failed: " + e.getCause().getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
