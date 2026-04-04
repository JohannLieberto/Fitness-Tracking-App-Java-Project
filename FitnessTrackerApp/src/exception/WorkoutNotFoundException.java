package exception;

/**
 * Thrown when a requested workout session cannot be found.
 */
public class WorkoutNotFoundException extends RuntimeException {
    public WorkoutNotFoundException(String message) {
        super(message);
    }
}
