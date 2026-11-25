
package ioutracker;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
   Abstract parent class for all IOU records.
   Stores common information and handles shared validation.
 */
public abstract class IOURecord implements Reportable {

    public static final String THEY_OWE = "THEY_OWE";
    public static final String I_OWE = "I_OWE";

    public static final String PENDING = "Pending";
    public static final String PARTIALLY_PAID = "Partially Paid";
    public static final String SETTLED = "Settled";

    protected int id;
    protected String direction;
    protected String personName;
    protected String dateCreated;
    protected String dueDate;
    protected String reason;
    protected String status;

    public IOURecord(int id, String direction, String personName,
                     String dateCreated, String dueDate,
                     String reason, String status)
                     throws InvalidDateFormatException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Record ID must be a positive number."
            );
        }

        if (!THEY_OWE.equals(direction) && !I_OWE.equals(direction)) {
            throw new IllegalArgumentException("Invalid IOU direction.");
        }

        this.id = id;
        this.direction = direction;
        this.personName = validateText(personName, "Person's name");
        this.dateCreated = validateDate(dateCreated);
        this.dueDate = validateDate(dueDate);
        this.reason = validateText(reason, "Reason");
        this.status = status;
    }

    protected String validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " cannot be blank."
            );
        }
        return value.trim();
    }

    protected String validateDate(String date)
            throws InvalidDateFormatException {

        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateFormatException(
                "Date must follow YYYY-MM-DD format."
            );
        }

        try {
            LocalDate.parse(date);
            return date;
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException(
                "Invalid calendar date.", e
            );
        }
    }

    // Validates a date supplied by the form.
    public static void validateDateString(String date, String fieldName)
            throws InvalidDateFormatException {

        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateFormatException(
                fieldName + " must follow YYYY-MM-DD format."
            );
        }

        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException(
                fieldName + " is not a valid calendar date.", e
            );
        }
    }

    public boolean isOverdue() {
        return isOverdue(LocalDate.now().toString());
    }

    public boolean isOverdue(String todayDate) {
        if (SETTLED.equals(status)) {
            return false;
        }

        return LocalDate.parse(dueDate)
                .isBefore(LocalDate.parse(todayDate));
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public String getPersonName() {
        return personName;
    }

    // Compatibility getter expected by the application.
    public String getPerson() {
        return personName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPersonName(String personName) {
        this.personName = validateText(personName, "Person's name");
    }

    public void setReason(String reason) {
        this.reason = validateText(reason, "Reason");
    }

    public abstract double getComparableValue();

    public abstract void markAsSettled();

    public abstract String toDataLine();
}
