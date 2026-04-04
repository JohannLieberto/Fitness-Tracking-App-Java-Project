package service;

import model.WorkoutSession;
import model.WorkoutType;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Demonstrates OOP2 Advanced: NIO2
 *   - Path + Files.createDirectories()
 *   - Files.newBufferedWriter() / newBufferedReader()
 *   - Files.copy() for timestamped backups
 *   - Files.walkFileTree() + SimpleFileVisitor
 *   - BasicFileAttributes
 */
public class WorkoutDataManager {

    private static final String DATA_DIR  = "fitness_data";
    private static final String DATA_FILE = "workouts.csv";
    private static final DateTimeFormatter BACKUP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Path dataDir;
    private final Path dataFile;

    public WorkoutDataManager() {
        this.dataDir  = Path.of(DATA_DIR);
        this.dataFile = dataDir.resolve(DATA_FILE);
    }

    /**
     * Convenience method called from FitnessTrackerMain.
     * Runs export, list backups in sequence.
     */
    public void demonstrateNIO2(List<WorkoutSession> workouts, String userId) throws IOException {
        System.out.println("User: " + userId);
        exportToCsv(workouts);
        listBackups();
        List<WorkoutSession> imported = importFromCsv();
        System.out.println("  Re-imported " + imported.size() + " session(s).");
    }

    // ── EXPORT ────────────────────────────────────────────────────

    public void exportToCsv(List<WorkoutSession> workouts) throws IOException {
        System.out.println("\n=== [NIO2] Exporting workouts to CSV ===");

        Files.createDirectories(dataDir);

        if (Files.exists(dataFile)) {
            String timestamp = LocalDateTime.now().format(BACKUP_FMT);
            Path backup = dataDir.resolve("workouts_backup_" + timestamp + ".csv");
            Files.copy(dataFile, backup, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  Backup created: " + backup.getFileName());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
            writer.write("sessionId,userId,date,durationMinutes,caloriesBurned,workoutType,notes");
            writer.newLine();
            for (WorkoutSession w : workouts) {
                writer.write(String.join(",",
                        w.sessionId(),
                        w.userId(),
                        w.date().toString(),
                        String.valueOf(w.durationMinutes()),
                        String.valueOf(w.caloriesBurned()),
                        w.workoutType().name(),
                        w.notes().replace(",", ";")));
                writer.newLine();
            }
        }
        System.out.println("  Exported " + workouts.size() + " sessions to " + dataFile);
    }

    // ── IMPORT ────────────────────────────────────────────────────

    public List<WorkoutSession> importFromCsv() throws IOException {
        System.out.println("\n=== [NIO2] Importing workouts from CSV ===");
        List<WorkoutSession> result = new ArrayList<>();

        if (!Files.exists(dataFile)) {
            System.out.println("  No data file found at " + dataFile);
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;
                WorkoutSession ws = new WorkoutSession(
                        parts[0],                          // sessionId
                        parts[1],                          // userId
                        LocalDate.parse(parts[2]),         // date
                        WorkoutType.valueOf(parts[5]),     // workoutType
                        Integer.parseInt(parts[3]),        // durationMinutes
                        Integer.parseInt(parts[4]),        // caloriesBurned
                        parts[6]                           // notes
                );
                result.add(ws);
            }
        }
        System.out.println("  Imported " + result.size() + " sessions.");
        return result;
    }

    // ── WALK FILE TREE ────────────────────────────────────────────

    public void listBackups() throws IOException {
        System.out.println("\n=== [NIO2] walkFileTree + BasicFileAttributes ===");

        if (!Files.exists(dataDir)) {
            System.out.println("  Data directory does not exist yet.");
            return;
        }

        Files.walkFileTree(dataDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String filename = file.getFileName().toString();
                if (filename.startsWith("workouts_backup")) {
                    System.out.printf("  Backup: %-45s | size: %5d bytes | created: %s%n",
                            filename,
                            attrs.size(),
                            attrs.creationTime());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
