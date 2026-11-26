package ioutracker;

/**
 * Custom checked exception thrown when a date string does not match YYYY-MM-DD.
 */
public class InvalidDateFormatException extends Exception {

    // Constructs an InvalidDateFormatException with a descriptive error message
    public InvalidDateFormatException(String message) {
        super(message);
    }

    // Constructs an InvalidDateFormatException with a message and root cause
    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
