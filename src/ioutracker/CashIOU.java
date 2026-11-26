package ioutracker;

/**
 * Subclass of IOURecord representing a Cash Money IOU.
 */
public class CashIOU extends IOURecord {

    private double originalAmount;
    private double remainingBalance;

    // Constructor initializing a CashIOU record with validation
    public CashIOU(int id, String direction, String person, double originalAmount,
                   double remainingBalance, String dateCreated, String dueDate,
                   String reason, String status) throws InvalidAmountException, InvalidDateFormatException {
        super(id, direction, person, dateCreated, dueDate, reason, status);
        this.setOriginalAmount(originalAmount);
        this.setRemainingBalance(remainingBalance);
    }

    // Returns the original cash amount
    public double getOriginalAmount() {
        return this.originalAmount;
    }

    // Validates and sets the original cash amount
    public void setOriginalAmount(double originalAmount) throws InvalidAmountException {
        if (originalAmount <= 0) {
            throw new InvalidAmountException("Invalid Amount: Please enter a positive number greater than zero.");
        }
        this.originalAmount = originalAmount;
    }

    // Returns the remaining unpaid balance
    public double getRemainingBalance() {
        return this.remainingBalance;
    }

    // Validates and sets the remaining unpaid balance
    public void setRemainingBalance(double remainingBalance) throws InvalidAmountException {
        if (remainingBalance < 0) {
            throw new InvalidAmountException("Remaining balance cannot be negative.");
        }
        this.remainingBalance = remainingBalance;
    }

    // Applies a partial or full payment to the remaining balance and updates status
    public void recordPayment(double paymentAmount) throws InvalidAmountException {
        if (paymentAmount <= 0) {
            throw new InvalidAmountException("Payment amount must be greater than zero.");
        }
        if (paymentAmount > this.remainingBalance) {
            throw new InvalidAmountException(
                "Entered payment (" + IOUManager.formatPeso(paymentAmount) +
                ") exceeds the remaining balance of " + IOUManager.formatPeso(this.remainingBalance) + "."
            );
        }
        this.remainingBalance = Math.max(0.0, Math.round((this.remainingBalance - paymentAmount) * 100.0) / 100.0);
        if (this.remainingBalance == 0.0) {
            this.setStatus("Settled");
        } else {
            this.setStatus("Partially Paid");
        }
    }

    // Marks this cash IOU as fully paid (remainingBalance = 0 and status = "Settled")
    @Override
    public void markAsSettled() {
        this.remainingBalance = 0.0;
        this.setStatus("Settled");
    }

    // Returns the remaining balance for sorting by amount
    @Override
    public double getComparableValue() {
        return this.remainingBalance;
    }

    // Returns "Cash" as the category name
    @Override
    public String getCategoryName() {
        return "Cash";
    }

    // Returns the formatted summary string for the dashboard table (without reason)
    @Override
    public String getSummaryDetails() {
        String extraNote = "";
        if ("Partially Paid".equals(this.getStatus())) {
            extraNote = " (of " + IOUManager.formatPeso(this.originalAmount) + ")";
        } else if ("Settled".equals(this.getStatus())) {
            extraNote = " (Paid " + IOUManager.formatPeso(this.originalAmount) + ")";
        }
        return IOUManager.formatPeso(this.remainingBalance) + extraNote;
    }

    // Formats this CashIOU as a plain-text receipt entry
    @Override
    public String toReceiptSection(int itemNumber, String todayDate) {
        int width = 62;
        String dirLabel = "THEY_OWE".equals(this.getDirection()) ? "THEY OWE " : "I OWE    ";
        StringBuilder sb = new StringBuilder();

        if ("Settled".equals(this.getStatus())) {
            sb.append(IOUManager.formatReceiptLine(
                itemNumber + ". " + dirLabel + "Cash",
                IOUManager.formatPhpReceipt(this.originalAmount),
                width
            )).append("\n");
            sb.append("   ").append(this.getReason()).append("\n");
            sb.append(IOUManager.formatReceiptLine(
                "   Created " + this.getDateCreated() + "   Due " + this.getDueDate(),
                "SETTLED",
                width
            )).append("\n");
        } else {
            String statusLabel = this.isOverdue(todayDate) ? "OVERDUE" : this.getStatus().toUpperCase();
            String partialNote = "Partially Paid".equals(this.getStatus())
                ? " (of " + IOUManager.formatPhpReceipt(this.originalAmount) + ")"
                : "";
            sb.append(IOUManager.formatReceiptLine(
                itemNumber + ". " + dirLabel + "Cash",
                IOUManager.formatPhpReceipt(this.remainingBalance),
                width
            )).append("\n");
            sb.append("   ").append(this.getReason()).append(partialNote).append("\n");
            sb.append(IOUManager.formatReceiptLine(
                "   Created " + this.getDateCreated() + "   Due " + this.getDueDate(),
                statusLabel,
                width
            )).append("\n");
        }
        return sb.toString();
    }

    // Serializes this CashIOU into a pipe-delimited text line for saving to file
    @Override
    public String toDataLine() {
        String cleanPerson = this.getPerson().replace("|", "/");
        String cleanReason = this.getReason().replace("|", "/");
        return "Cash|" + this.getId() + "|" + this.getDirection() + "|" + cleanPerson + "|"
                + this.originalAmount + "|" + this.remainingBalance + "|"
                + this.getDateCreated() + "|" + this.getDueDate() + "|"
                + cleanReason + "|" + this.getStatus();
    }
}
