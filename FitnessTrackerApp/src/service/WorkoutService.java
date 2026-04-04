package service;

import model.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    // SORTING  (User Stories 1 & 2)
    // ─────────────────────────────────────────────────────────────

    /** US-1: Sort by date descending, tie-break by duration ascending */
    public List<WorkoutSession> getWorkoutsSortedByDate() {
        System.out.println("\n=== [SORTING] Comparator.comparing + thenComparing ===");
        return workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::getDate).reversed()
                        .thenComparing(WorkoutSession::getDurationMinutes))
                .peek(w -> System.out.println("  " + w.getName() + " | " + w.getDate() + " | " + w.getDurationMinutes() + "min"))
                .collect(Collectors.toList());
    }

    /** US-2: Sort by duration descending */
    public List<WorkoutSession> getWorkoutsSortedByDuration() {
        return workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::getDurationMinutes).reversed())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // LAMBDAS  (User Stories 3-6)
    // ─────────────────────────────────────────────────────────────

    /** US-3: Filter using Predicate */
    public List<WorkoutSession> filterByType(WorkoutType type) {
        System.out.println("\n=== [LAMBDA] Predicate<WorkoutSession> ===");
        Predicate<WorkoutSession> byType = w -> w.getWorkoutType() == type;
        return workouts.stream().filter(byType).collect(Collectors.toList());
    }

    /** US-4: Apply Consumer to each workout */
    public void applyToAll(Consumer<WorkoutSession> action) {
        System.out.println("\n=== [LAMBDA] Consumer<WorkoutSession> ===");
        workouts.forEach(action);
    }

    /** US-5: Supplier provides default plan when list is empty */
    public List<WorkoutSession> getOrDefault(Supplier<List<WorkoutSession>> defaultSupplier) {
        System.out.println("\n=== [LAMBDA] Supplier<List<WorkoutSession>> ===");
        return workouts.isEmpty() ? defaultSupplier.get() : workouts;
    }

    /** US-6: Map workout to calorie value using Function */
    public List<Integer> mapToCalories(Function<WorkoutSession, Integer> calorieMapper) {
        System.out.println("\n=== [LAMBDA] Function<WorkoutSession, Integer> ===");
        return workouts.stream().map(calorieMapper).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // STREAMS — TERMINAL  (User Stories 7 & 8)
    // ─────────────────────────────────────────────────────────────

    /** US-7: count, max, min, findFirst */
    public void showStreamTerminalOps() {
        System.out.println("\n=== [STREAMS] Terminal Operations ===");

        long count = workouts.stream().count();
        System.out.println("  count()        = " + count);

        workouts.stream()
                .max(Comparator.comparing(WorkoutSession::getDurationMinutes))
                .ifPresent(w -> System.out.println("  max() duration = " + w.getDurationMinutes() + "min (" + w.getName() + ")"));

        workouts.stream()
                .min(Comparator.comparing(WorkoutSession::getDurationMinutes))
                .ifPresent(w -> System.out.println("  min() duration = " + w.getDurationMinutes() + "min (" + w.getName() + ")"));

        workouts.stream()
                .filter(w -> w.getWorkoutType() == WorkoutType.CARDIO)
                .findFirst()
                .ifPresent(w -> System.out.println("  findFirst() cardio = " + w.getName()));

        // US-8 — match operations
        boolean anyLong   = workouts.stream().anyMatch(w -> w.getDurationMinutes() > 60);
        boolean allValid  = workouts.stream().allMatch(w -> w.getWorkoutType() != null);
        boolean noneNeg   = workouts.stream().noneMatch(w -> w.getDurationMinutes() < 0);
        System.out.println("  anyMatch(>60min) = " + anyLong);
        System.out.println("  allMatch(type!=null) = " + allValid);
        System.out.println("  noneMatch(neg duration) = " + noneNeg);

        System.out.println("  forEach() summaries:");
        workouts.stream().forEach(w -> System.out.println("    - " + w.getName() + " [" + w.getWorkoutType() + "]"));
    }

    // ─────────────────────────────────────────────────────────────
    // STREAMS — COLLECTORS  (User Story 9)
    // ─────────────────────────────────────────────────────────────

    /** US-9: groupingBy, partitioningBy, toMap */
    public void showStreamCollectors() {
        System.out.println("\n=== [STREAMS] Collectors: groupingBy / partitioningBy / toMap ===");

        // groupingBy
        Map<WorkoutType, List<WorkoutSession>> grouped =
                workouts.stream().collect(Collectors.groupingBy(WorkoutSession::getWorkoutType));
        grouped.forEach((type, list) ->
                System.out.println("  groupingBy " + type + " -> " + list.size() + " sessions"));

        // partitioningBy
        Map<Boolean, List<WorkoutSession>> partitioned =
                workouts.stream().collect(Collectors.partitioningBy(w -> w.getDurationMinutes() > 40));
        System.out.println("  partitioningBy(>40min): high=" + partitioned.get(true).size() +
                ", low=" + partitioned.get(false).size());

        // toMap
        Map<String, Integer> calorieMap = workouts.stream()
                .collect(Collectors.toMap(
                        WorkoutSession::getName,
                        WorkoutSession::getCaloriesBurned,
                        Integer::sum));
        System.out.println("  toMap (name->calories): " + calorieMap);
    }

    // ─────────────────────────────────────────────────────────────
    // STREAMS — INTERMEDIATE  (User Story 10)
    // ─────────────────────────────────────────────────────────────

    /** US-10: filter, map, sorted, distinct, limit in a single pipeline */
    public List<String> showStreamIntermediateOps() {
        System.out.println("\n=== [STREAMS] Intermediate Ops: filter/map/sorted/distinct/limit ===");
        List<String> result = workouts.stream()
                .filter(w -> w.getDurationMinutes() >= 30)          // filter
                .map(WorkoutSession::getName)                        // map
                .sorted()                                            // sorted
                .distinct()                                          // distinct
                .limit(5)                                            // limit
                .collect(Collectors.toList());
        result.forEach(n -> System.out.println("  " + n));
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // SWITCH EXPRESSIONS + PATTERN MATCHING  (User Stories 11 & 12)
    // ─────────────────────────────────────────────────────────────

    /** US-11: Switch expression on enum with arrow syntax */
    public int scoreActivity(WorkoutType type) {
        System.out.println("\n=== [SWITCH EXPRESSION] WorkoutType scoring ===");
        int score = switch (type) {
            case CARDIO       -> 10;
            case STRENGTH     -> 8;
            case FLEXIBILITY  -> 5;
            case HIIT         -> 12;
            default           -> 3;
        };
        System.out.println("  Score for " + type + " = " + score);
        return score;
    }

    /** US-12: Pattern matching switch on sealed Activity hierarchy */
    public String describeActivity(Activity activity) {
        System.out.println("\n=== [PATTERN MATCHING SWITCH] Sealed Activity hierarchy ===");
        String desc = switch (activity) {
            case Running r         -> String.format("Running %.1f km at %.1f min/km", r.distanceKm(), r.paceMinPerKm());
            case Cycling c         -> String.format("Cycling %.1f km at %.1f km/h", c.distanceKm(), c.speedKmh());
            case StrengthTraining s -> String.format("Strength: %s — %.1f kg x %d reps", s.exerciseName(), s.weightKg(), s.reps());
        };
        System.out.println("  " + desc);
        return desc;
    }

    // ─────────────────────────────────────────────────────────────
    // DATE/TIME API  (User Story 13)
    // ─────────────────────────────────────────────────────────────

    /** US-13: LocalDate, Duration, Period, DateTimeFormatter */
    public void showDateTimeFeatures() {
        System.out.println("\n=== [DATE/TIME API] LocalDate / Duration / Period / DateTimeFormatter ===");

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        workouts.stream()
                .sorted(Comparator.comparing(WorkoutSession::getDate).reversed())
                .limit(3)
                .forEach(w -> {
                    Period since = Period.between(w.getDate(), today);
                    Duration dur = Duration.ofMinutes(w.getDurationMinutes());
                    System.out.printf("  %s | %s | %dd ago | %dh %dm%n",
                            w.getName(),
                            w.getDate().format(fmt),
                            since.getDays(),
                            dur.toHours(), dur.toMinutesPart());
                });

        // Weekly boundary
        LocalDate weekAgo = today.minusDays(7);
        long thisWeek = workouts.stream()
                .filter(w -> !w.getDate().isBefore(weekAgo))
                .count();
        System.out.println("  Sessions in last 7 days: " + thisWeek);
    }

    // ─────────────────────────────────────────────────────────────
    // RECORDS  (User Story 14)
    // ─────────────────────────────────────────────────────────────

    /** US-14: Create and display WorkoutSummary + UserGoal records */
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
