package ioutracker;

/**
 * Abstract parent class representing a general IOU record.
 * Demonstrates encapsulation, validation, abstraction, and inheritance.
 */
public abstract class IOURecord implements Reportable {

    private int id;
    private String direction; // "THEY_OWE" or "I_OWE"
    private String person;
    private String dateCreated;
    private String dueDate;
    private String reason;
    private String status;    // "Pending", "Partially Paid", or "Settled"

    // Parameterized constructor that validates and initializes shared IOU fields
    public IOURecord(int id, String direction, String person, String dateCreated,
                     String dueDate, String reason, String status) throws InvalidDateFormatException {
        this.setId(id);
        this.setDirection(direction);
        this.setPerson(person);
        this.setDateCreated(dateCreated);
        this.setDueDate(dueDate);
        this.setReason(reason);
        this.setStatus(status);
    }

    // Validates that a date string strictly follows the YYYY-MM-DD format
    public static void validateDateString(String dateStr, String fieldName) throws InvalidDateFormatException {
        if (dateStr == null || !dateStr.trim().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new InvalidDateFormatException(
                "Invalid " + fieldName + ": Please use the exact YYYY-MM-DD format (e.g., 2025-05-15)."
            );
        }
        String[] parts = dateStr.trim().split("-");
        try {
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                throw new InvalidDateFormatException(
                    "Invalid " + fieldName + ": Month must be 01-12 and Day must be 01-31."
                );
            }
        } catch (NumberFormatException ex) {
            throw new InvalidDateFormatException(
                "Invalid " + fieldName + ": Date contains non-numeric values.", ex
            );
        }
    }

    // Returns the unique ID of the IOU record
    public int getId() {
        return this.id;
    }

    // Validates and sets the unique ID of the IOU record
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Record ID must be a positive integer.");
        }
        this.id = id;
    }

    // Returns the direction ("THEY_OWE" or "I_OWE")
    public String getDirection() {
        return this.direction;
    }

    // Validates and sets the direction ("THEY_OWE" or "I_OWE")
    public void setDirection(String direction) {
        if (direction == null || (!direction.equals("THEY_OWE") && !direction.equals("I_OWE"))) {
            throw new IllegalArgumentException("Direction must be 'THEY_OWE' or 'I_OWE'.");
        }
        this.direction = direction;
    }

    // Returns the contact person's name
    public String getPerson() {
        return this.person;
    }

    // Validates and sets the contact person's name
    public void setPerson(String person) {
        if (person == null || person.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Person's Name cannot be blank. Please select an existing contact or type a name."
            );
        }
        this.person = person.trim();
    }

    // Returns the creation date string (YYYY-MM-DD)
    public String getDateCreated() {
        return this.dateCreated;
    }

    // Validates and sets the creation date string (YYYY-MM-DD)
    public void setDateCreated(String dateCreated) throws InvalidDateFormatException {
        validateDateString(dateCreated, "Date Created");
        this.dateCreated = dateCreated.trim();
    }

    // Returns the due date string (YYYY-MM-DD)
    public String getDueDate() {
        return this.dueDate;
    }

    // Validates and sets the due date string (YYYY-MM-DD)
    public void setDueDate(String dueDate) throws InvalidDateFormatException {
        validateDateString(dueDate, "Due Date");
        this.dueDate = dueDate.trim();
    }

    // Returns the reason or description for the IOU
    public String getReason() {
        return this.reason;
    }

    // Validates and sets the reason or description for the IOU
    public void setReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a short Reason / Description for this IOU.");
        }
        this.reason = reason.trim();
    }

    // Returns the current settlement status
    public String getStatus() {
        return this.status;
    }

    // Validates and sets the current settlement status
    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            this.status = "Pending";
        } else {
            this.status = status.trim();
        }
    }

    // Checks whether this IOU is unsettled and past the reference todayDate
    public boolean isOverdue(String todayDate) {
        return !this.status.equals("Settled") && this.dueDate.compareTo(todayDate) < 0;
    }

    // Marks the record as completely settled (implemented differently by subclasses)
    public abstract void markAsSettled();

    // Returns the numerical value used when sorting records by amount/value
    public abstract double getComparableValue();

    // Converts the record into a pipe-delimited string for file storage
    public abstract String toDataLine();
}
