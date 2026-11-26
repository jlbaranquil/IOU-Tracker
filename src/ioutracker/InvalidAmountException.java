package ioutracker;

/**
 * Custom checked exception thrown when a monetary amount or item value is invalid.
 */
public class InvalidAmountException extends Exception {

    // Constructs an InvalidAmountException with a descriptive error message
    public InvalidAmountException(String message) {
        super(message);
    }

    // Constructs an InvalidAmountException with a message and root cause
    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
