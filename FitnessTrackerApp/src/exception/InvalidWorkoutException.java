package exception;

/**
 * Thrown when a workout session contains invalid data.
 */
public class InvalidWorkoutException extends Exception {
    public InvalidWorkoutException(String message) {
        super(message);
    }

    public InvalidWorkoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
