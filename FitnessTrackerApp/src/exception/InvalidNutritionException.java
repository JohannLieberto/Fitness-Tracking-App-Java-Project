package exception;

/**
 * Unchecked exception (RuntimeException) demonstrating exception handling
 */
public class InvalidNutritionException extends RuntimeException {
    
    public InvalidNutritionException(String message) {
        super(message);
    }
    
    public InvalidNutritionException(String message, Throwable cause) {
        super(message, cause);
    }
}
