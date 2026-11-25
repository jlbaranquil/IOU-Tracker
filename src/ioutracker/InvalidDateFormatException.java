
package ioutracker;

//Custom checked exception for invalid date format or date values.

public class InvalidDateFormatException extends Exception {

    public InvalidDateFormatException(String message) {
        super(message);
    }

    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
