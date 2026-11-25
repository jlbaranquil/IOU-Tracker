
package ioutracker;

//Custom checked exception for invalid monetary amounts and estimated item values.

public class InvalidAmountException extends Exception {

    // Constructor for a custom error message.
    public InvalidAmountException(String message) {
        super(message);
    }

    // Constructor for a message and the original cause.
    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
