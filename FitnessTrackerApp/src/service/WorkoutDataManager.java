package service;

import model.WorkoutSession;
import model.WorkoutType;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP2 - NIO2 DEMO
 * Demonstrates: Path, Files, BufferedReader, BufferedWriter,
 *               Files.walkFileTree, BasicFileAttributes, directory creation
 *
 * Handles saving and loading workout history to/from CSV files
 * using the modern java.nio.file API.
 */
public class WorkoutDataManager {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String DATA_DIR = "fitness_data";
    private static final String HISTORY_FILE = DATA_DIR + "/workout_history.csv";
    private static final String BACKUP_DIR = DATA_DIR + "/backups";

    // ------------------------------------------------------------------
    // Initialise — create directories if they don't already exist (NIO2)
    // ------------------------------------------------------------------
    public void initialise() throws IOException {
        Path dataPath = Path.of(DATA_DIR);
        Path backupPath = Path.of(BACKUP_DIR);

        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
            System.out.println("  [NIO2] Created data directory: " + dataPath.toAbsolutePath());
        }
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
            System.out.println("  [NIO2] Created backup directory: " + backupPath.toAbsolutePath());
        }
    }

    // ------------------------------------------------------------------
    // Write workout history to CSV — NIO2 Files.newBufferedWriter
    // ------------------------------------------------------------------
    public void saveWorkoutHistory(String userId, List<WorkoutSession> sessions) throws IOException {
        initialise();
        Path filePath = Path.of(HISTORY_FILE);

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write("userId,sessionId,date,type,durationMinutes,calories,notes");
            writer.newLine();

            for (WorkoutSession session : sessions) {
                String line = String.join(",",
                        userId,
                        session.sessionId(),
                        session.date().format(DATE_FMT),
                        session.type().name(),
                        String.valueOf(session.totalDuration()),    // fixed: was durationMinutes()
                        String.format("%.0f", session.calculateTotalCalories()),
                        session.notes().replace(",", ";")
                );
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("  [NIO2] Saved " + sessions.size() + " sessions to: " + filePath.toAbsolutePath());
    }

    // ------------------------------------------------------------------
    // Read workout history back from CSV — NIO2 Files.newBufferedReader
    // ------------------------------------------------------------------
    public List<String[]> loadWorkoutHistory() throws IOException {
        Path filePath = Path.of(HISTORY_FILE);
        List<String[]> rows = new ArrayList<>();

        if (!Files.exists(filePath)) {
            System.out.println("  [NIO2] No history file found at: " + filePath.toAbsolutePath());
            return rows;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            reader.lines()
                  .skip(1)
                  .map(line -> line.split(","))
                  .forEach(rows::add);
        }

        System.out.println("  [NIO2] Loaded " + rows.size() + " workout records from file.");
        return rows;
    }

    // ------------------------------------------------------------------
    // Backup — copy current history file (NIO2 Files.copy + Path)
    // ------------------------------------------------------------------
    public void backupHistory() throws IOException {
        initialise();
        Path source = Path.of(HISTORY_FILE);
        if (!Files.exists(source)) {
            System.out.println("  [NIO2] Nothing to back up — history file does not exist.");
            return;
        }

        String timestamp = LocalDate.now().format(DATE_FMT);
        Path backup = Path.of(BACKUP_DIR + "/workout_history_" + timestamp + ".csv");
        Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  [NIO2] Backup created: " + backup.toAbsolutePath());
    }

    // ------------------------------------------------------------------
    // List backups — walkFileTree to show all backup files (NIO2)
    // ------------------------------------------------------------------
    public void listBackups() throws IOException {
        Path backupPath = Path.of(BACKUP_DIR);
        if (!Files.exists(backupPath)) {
            System.out.println("  [NIO2] No backup directory found.");
            return;
        }

        System.out.println("  [NIO2] Backup files found:");
        Files.walkFileTree(backupPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                System.out.printf("    - %s  (%.1f KB, modified: %s)%n",
                        file.getFileName(),
                        attrs.size() / 1024.0,
                        attrs.lastModifiedTime());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // ------------------------------------------------------------------
    // Get file metadata — demonstrates BasicFileAttributes (NIO2)
    // ------------------------------------------------------------------
    public void printFileInfo() throws IOException {
        Path filePath = Path.of(HISTORY_FILE);
        if (!Files.exists(filePath)) return;

        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
        System.out.printf("  [NIO2] File: %s%n", filePath.toAbsolutePath());
        System.out.printf("         Size: %.1f KB%n", attrs.size() / 1024.0);
        System.out.printf("         Created:  %s%n", attrs.creationTime());
        System.out.printf("         Modified: %s%n", attrs.lastModifiedTime());
    }
}
