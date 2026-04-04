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

    /**
     * Runs parallel analytics using ExecutorService + Callable.
     * Called as runAnalytics() from WorkoutService and runParallelAnalysis() from Main.
     */
    public void runParallelAnalysis(List<WorkoutSession> workouts) throws InterruptedException {
        runAnalytics(workouts);
    }

    public void runAnalytics(List<WorkoutSession> workouts) throws InterruptedException {
        System.out.println("\n=== [CONCURRENCY] ExecutorService + Callable + invokeAll ===");

        List<WorkoutSession> snapshot = Collections.unmodifiableList(new ArrayList<>(workouts));

        List<Callable<AnalysisResult>> tasks = List.of(
                () -> new AnalysisResult("Total calories",
                        String.valueOf(snapshot.stream().mapToInt(WorkoutSession::caloriesBurned).sum())),

                () -> new AnalysisResult("Avg duration (min)",
                        String.format("%.1f", snapshot.stream()
                                .mapToInt(WorkoutSession::durationMinutes).average().orElse(0))),

                () -> new AnalysisResult("Longest session",
                        snapshot.stream()
                                .max(Comparator.comparing(WorkoutSession::durationMinutes))
                                .map(w -> w.sessionId() + " (" + w.durationMinutes() + "min)")
                                .orElse("none")),

                () -> new AnalysisResult("Session count",
                        String.valueOf(snapshot.size())),

                () -> new AnalysisResult("Unique workout types",
                        snapshot.stream()
                                .map(w -> w.workoutType().name())
                                .distinct()
                                .sorted()
                                .collect(Collectors.joining(", "))),

                () -> new AnalysisResult("Most recent session",
                        snapshot.stream()
                                .max(Comparator.comparing(WorkoutSession::date))
                                .map(w -> w.sessionId() + " on " + w.date())
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
