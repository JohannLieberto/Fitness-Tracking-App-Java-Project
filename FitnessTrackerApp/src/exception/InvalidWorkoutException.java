package exception;

/**
 * Checked exception demonstrating exception handling
 */
public class InvalidWorkoutException extends Exception {
    
    public InvalidWorkoutException(String message) {
        super(message);
    }
    
    public InvalidWorkoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
