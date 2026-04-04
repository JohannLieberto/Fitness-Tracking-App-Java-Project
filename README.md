# Fitness Tracker — OOP2 Assignment

**Branch:** `oop2-upgrade`  
**Module:** Object-Oriented Programming 2  
**Student:** Hitesh Khade

## Project Structure

```
FitnessTrackerApp/src/
├── FitnessTrackerMain.java       ← Main entry point (all OOP2 features)
├── Java25Demo.java               ← Java 25 JEP 512 + JEP 513 (compile separately)
├── model/
│   ├── Activity.java             ← sealed interface permits Running, Cycling, StrengthTraining
│   ├── Running.java              ← record implements Activity
│   ├── Cycling.java              ← record implements Activity
│   ├── StrengthTraining.java     ← record implements Activity
│   ├── WorkoutSession.java       ← core domain class
│   ├── WorkoutType.java          ← enum (used in switch expressions)
│   ├── WorkoutSummary.java       ← record (immutable DTO)
│   └── UserGoal.java             ← record (immutable DTO)
├── service/
│   ├── WorkoutService.java       ← Sorting, Lambdas, Streams, Switch, Date/Time, Records
│   ├── AnalyticsService.java     ← Concurrency (ExecutorService, Callable, Future)
│   └── WorkoutDataManager.java   ← NIO2 (Path, Files, walkFileTree, BasicFileAttributes)
├── exception/
│   ├── InvalidWorkoutException.java
│   └── WorkoutNotFoundException.java
└── i18n/
    ├── messages_en.properties    ← English ResourceBundle
    └── messages_fr.properties    ← French ResourceBundle
```

## OOP2 Feature Map

| Feature | Class / Method |
|---------|---------------|
| Sorting (`Comparator.comparing`) | `WorkoutService.getWorkoutsSortedByDate()` |
| Lambda — Predicate | `WorkoutService.filterByType()` |
| Lambda — Consumer | `WorkoutService.applyToAll()` |
| Lambda — Supplier | `WorkoutService.getOrDefault()` |
| Lambda — Function | `WorkoutService.mapToCalories()` |
| Streams terminal (`count`, `max`, `min`, `anyMatch`, `allMatch`, `noneMatch`, `forEach`, `findFirst`) | `WorkoutService.showStreamTerminalOps()` |
| Streams collectors (`groupingBy`, `partitioningBy`, `toMap`) | `WorkoutService.showStreamCollectors()` |
| Streams intermediate (`filter`, `map`, `sorted`, `distinct`, `limit`) | `WorkoutService.showStreamIntermediateOps()` |
| Switch expression (enum) | `WorkoutService.scoreActivity()` |
| Pattern matching switch (sealed) | `WorkoutService.describeActivity()` |
| Sealed classes | `model/Activity.java` + `Running`, `Cycling`, `StrengthTraining` |
| Date/Time API | `WorkoutService.showDateTimeFeatures()` |
| Records | `model/WorkoutSummary.java`, `model/UserGoal.java` |
| Concurrency | `AnalyticsService.runAnalytics()` |
| NIO2 | `WorkoutDataManager` (export, import, walkFileTree) |
| Localisation | `FitnessTrackerMain.demonstrateLocalisation()` |
| Java 25 JEP 512 (instance main) | `Java25Demo.main()` |
| Java 25 JEP 513 (flexible constructor) | `Java25Demo.ValidatedSession`, `WorkoutSummaryExtended` |

## How to Compile & Run

### Main application (Java 21+)

```bash
cd FitnessTrackerApp/src
javac -d . model/*.java exception/*.java service/*.java FitnessTrackerMain.java
java FitnessTrackerMain
```

### Java 25 demo (requires JDK 25)

```bash
cd FitnessTrackerApp/src
javac --release 25 --enable-preview Java25Demo.java model/Activity.java model/Running.java model/Cycling.java model/StrengthTraining.java
java --enable-preview Java25Demo
```

> **Note:** The `i18n/` directory must be on the classpath. When running from `FitnessTrackerApp/src`, it is included automatically.
