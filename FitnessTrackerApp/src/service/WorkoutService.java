package service;

import model.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Core service demonstrating all OOP2 Fundamentals features:
 *   - Sorting (Comparator.comparing / thenComparing)
 *   - Lambdas (Consumer, Predicate, Supplier, Function)
 *   - Streams: terminal + intermediate + collectors
 *   - Switch expressions + pattern matching
 *   - Sealed classes
 *   - Date/Time API
 *   - Records
 */
public class WorkoutService {

    private final List<WorkoutSession> workouts;

    public WorkoutService(List<WorkoutSession> workouts) {
        this.workouts = new ArrayList<>(workouts);
    }

    // ─────────────────────────────────────────────────────────────
    // SORTING
    // ─────────────────────────────────────────────────────────────

    public List<WorkoutSession> getWorkoutsSortedByDate() {
        System.out.println("\n=== [SORTING] Comparator.comparing + thenComparing ===");
        return workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::date).reversed()
                        .thenComparing(WorkoutSession::durationMinutes))
                .peek(w -> System.out.println("  " + w.sessionId() + " | " + w.date() + " | " + w.durationMinutes() + "min"))
                .collect(Collectors.toList());
    }

    public List<WorkoutSession> getWorkoutsSortedByDuration() {
        return workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::durationMinutes).reversed())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // LAMBDAS
    // ─────────────────────────────────────────────────────────────

    public List<WorkoutSession> filterByType(WorkoutType type) {
        System.out.println("\n=== [LAMBDA] Predicate<WorkoutSession> ===");
        Predicate<WorkoutSession> byType = w -> w.workoutType() == type;
        return workouts.stream().filter(byType).collect(Collectors.toList());
    }

    public void applyToAll(Consumer<WorkoutSession> action) {
        System.out.println("\n=== [LAMBDA] Consumer<WorkoutSession> ===");
        workouts.forEach(action);
    }

    public List<WorkoutSession> getOrDefault(Supplier<List<WorkoutSession>> defaultSupplier) {
        System.out.println("\n=== [LAMBDA] Supplier<List<WorkoutSession>> ===");
        return workouts.isEmpty() ? defaultSupplier.get() : workouts;
    }

    public List<Integer> mapToCalories(Function<WorkoutSession, Integer> calorieMapper) {
        System.out.println("\n=== [LAMBDA] Function<WorkoutSession, Integer> ===");
        return workouts.stream().map(calorieMapper).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // STREAMS — TERMINAL
    // ─────────────────────────────────────────────────────────────

    public void showStreamTerminalOps() {
        System.out.println("\n=== [STREAMS] Terminal Operations ===");

        long count = workouts.stream().count();
        System.out.println("  count()        = " + count);

        workouts.stream()
                .max(Comparator.comparing(WorkoutSession::durationMinutes))
                .ifPresent(w -> System.out.println("  max() duration = " + w.durationMinutes() + "min (" + w.sessionId() + ")"));

        workouts.stream()
                .min(Comparator.comparing(WorkoutSession::durationMinutes))
                .ifPresent(w -> System.out.println("  min() duration = " + w.durationMinutes() + "min (" + w.sessionId() + ")"));

        workouts.stream()
                .filter(w -> w.workoutType() == WorkoutType.CARDIO)
                .findFirst()
                .ifPresent(w -> System.out.println("  findFirst() cardio = " + w.sessionId()));

        boolean anyLong  = workouts.stream().anyMatch(w -> w.durationMinutes() > 60);
        boolean allValid = workouts.stream().allMatch(w -> w.workoutType() != null);
        boolean noneNeg  = workouts.stream().noneMatch(w -> w.durationMinutes() < 0);
        System.out.println("  anyMatch(>60min) = " + anyLong);
        System.out.println("  allMatch(type!=null) = " + allValid);
        System.out.println("  noneMatch(neg duration) = " + noneNeg);

        System.out.println("  forEach() summaries:");
        workouts.forEach(w -> System.out.println("    - " + w.sessionId() + " [" + w.workoutType() + "]"));
    }

    // ─────────────────────────────────────────────────────────────
    // STREAMS — COLLECTORS
    // ─────────────────────────────────────────────────────────────

    public void showStreamCollectors() {
        System.out.println("\n=== [STREAMS] Collectors: groupingBy / partitioningBy / toMap ===");

        Map<WorkoutType, List<WorkoutSession>> grouped =
                workouts.stream().collect(Collectors.groupingBy(WorkoutSession::workoutType));
        grouped.forEach((type, list) ->
                System.out.println("  groupingBy " + type + " -> " + list.size() + " sessions"));

        Map<Boolean, List<WorkoutSession>> partitioned =
                workouts.stream().collect(Collectors.partitioningBy(w -> w.durationMinutes() > 40));
        System.out.println("  partitioningBy(>40min): high=" + partitioned.get(true).size() +
                ", low=" + partitioned.get(false).size());

        Map<String, Integer> calorieMap = workouts.stream()
                .collect(Collectors.toMap(
                        WorkoutSession::sessionId,
                        WorkoutSession::caloriesBurned,
                        Integer::sum));
        System.out.println("  toMap (sessionId->calories): " + calorieMap);
    }

    // ─────────────────────────────────────────────────────────────
    // STREAMS — INTERMEDIATE
    // ─────────────────────────────────────────────────────────────

    public List<String> showStreamIntermediateOps() {
        System.out.println("\n=== [STREAMS] Intermediate Ops: filter/map/sorted/distinct/limit ===");
        List<String> result = workouts.stream()
                .filter(w -> w.durationMinutes() >= 30)
                .map(WorkoutSession::sessionId)
                .sorted()
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
        result.forEach(n -> System.out.println("  " + n));
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // SWITCH EXPRESSIONS + PATTERN MATCHING
    // ─────────────────────────────────────────────────────────────

    public int scoreActivity(WorkoutType type) {
        System.out.println("\n=== [SWITCH EXPRESSION] WorkoutType scoring ===");
        int score = switch (type) {
            case CARDIO      -> 10;
            case STRENGTH    -> 8;
            case FLEXIBILITY -> 5;
            case HIIT        -> 12;
            default          -> 3;
        };
        System.out.println("  Score for " + type + " = " + score);
        return score;
    }

    public String describeActivity(Activity activity) {
        System.out.println("\n=== [PATTERN MATCHING SWITCH] Sealed Activity hierarchy ===");
        String desc = switch (activity) {
            case Running r          -> String.format("Running %.1f km at %.1f min/km", r.distanceKm(), r.paceMinPerKm());
            case Cycling c          -> String.format("Cycling %.1f km at %.1f km/h", c.distanceKm(), c.speedKmh());
            case StrengthTraining s -> String.format("Strength: %s — %.1f kg x %d reps", s.exerciseName(), s.weightKg(), s.reps());
        };
        System.out.println("  " + desc);
        return desc;
    }

    // ─────────────────────────────────────────────────────────────
    // DATE/TIME API
    // ─────────────────────────────────────────────────────────────

    public void showDateTimeFeatures() {
        System.out.println("\n=== [DATE/TIME API] LocalDate / Duration / Period / DateTimeFormatter ===");

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::date).reversed())
                .limit(3)
                .forEach(w -> {
                    Period since = Period.between(w.date(), today);
                    Duration dur = Duration.ofMinutes(w.durationMinutes());
                    System.out.printf("  %s | %s | %dd ago | %dh %dm%n",
                            w.sessionId(),
                            w.date().format(fmt),
                            since.getDays(),
                            dur.toHours(), dur.toMinutesPart());
                });

        LocalDate weekAgo = today.minusDays(7);
        long thisWeek = workouts.stream()
                .filter(w -> !w.date().isBefore(weekAgo))
                .count();
        System.out.println("  Sessions in last 7 days: " + thisWeek);
    }

    // ─────────────────────────────────────────────────────────────
    // RECORDS
    // ─────────────────────────────────────────────────────────────

    public void showRecords() {
        System.out.println("\n=== [RECORDS] WorkoutSummary + UserGoal ===");

        WorkoutSummary summary = WorkoutSummary.of(LocalDate.now(), 450, 55);
        UserGoal goal = new UserGoal("Lose 2kg by summer", 2500, LocalDate.of(2026, 8, 1));

        System.out.println("  " + summary);
        System.out.println("  " + goal);
        System.out.println("  Summary date accessor: " + summary.date());
        System.out.println("  Goal expired? " + goal.isExpired());
    }
}
