package ioutracker;

/**
 * Subclass of IOURecord representing a Borrowed Physical Item IOU.
 */
public class ItemIOU extends IOURecord {

    private String itemName;
    private String itemCondition;
    private double estimatedValue;

    // Constructor initializing an ItemIOU record with validation
    public ItemIOU(int id, String direction, String person, String itemName,
                   String itemCondition, double estimatedValue, String dateCreated,
                   String dueDate, String reason, String status) throws InvalidAmountException, InvalidDateFormatException {
        super(id, direction, person, dateCreated, dueDate, reason, status);
        this.setItemName(itemName);
        this.setItemCondition(itemCondition);
        this.setEstimatedValue(estimatedValue);
    }

    // Returns the name of the borrowed item
    public String getItemName() {
        return this.itemName;
    }

    // Validates and sets the name of the borrowed item
    public void setItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter the name of the borrowed physical item.");
        }
        this.itemName = itemName.trim();
    }

    // Returns the physical condition of the borrowed item
    public String getItemCondition() {
        return this.itemCondition;
    }

    // Validates and sets the physical condition of the borrowed item
    public void setItemCondition(String itemCondition) {
        if (itemCondition == null || itemCondition.trim().isEmpty()) {
            this.itemCondition = "Good";
        } else {
            this.itemCondition = itemCondition.trim();
        }
    }

    // Returns the estimated monetary value of the item
    public double getEstimatedValue() {
        return this.estimatedValue;
    }

    // Validates and sets the estimated monetary value of the item (minimum value is 0)
    public void setEstimatedValue(double estimatedValue) throws InvalidAmountException {
        if (estimatedValue < 0) {
            throw new InvalidAmountException("Invalid Estimated Value: Please enter zero or a positive number using digits only.");
        }
        this.estimatedValue = estimatedValue;
    }

    // Marks this borrowed item as returned ("Settled")
    @Override
    public void markAsSettled() {
        this.setStatus("Settled");
    }

    // Returns the estimated value for sorting by amount
    @Override
    public double getComparableValue() {
        return this.estimatedValue;
    }

    // Returns "Item" as the category name
    @Override
    public String getCategoryName() {
        return "Item";
    }

    // Returns the formatted summary string for the dashboard table (without reason)
    @Override
    public String getSummaryDetails() {
        return this.itemName + " [" + this.itemCondition + ", Est. "
                + IOUManager.formatPeso(this.estimatedValue) + "]";
    }

    // Formats this ItemIOU as a plain-text receipt entry
    @Override
    public String toReceiptSection(int itemNumber, String todayDate) {
        int width = 62;
        String dirLabel = "THEY_OWE".equals(this.getDirection()) ? "THEY OWE " : "I OWE    ";
        StringBuilder sb = new StringBuilder();

        if ("Settled".equals(this.getStatus())) {
            sb.append(IOUManager.formatReceiptLine(
                itemNumber + ". " + dirLabel + "Item",
                "RETURNED",
                width
            )).append("\n");
            sb.append("   ").append(this.itemName).append(" [").append(this.itemCondition).append("]\n");
            sb.append("   ").append(this.getReason()).append("\n");
            sb.append(IOUManager.formatReceiptLine(
                "   Estimated value",
                IOUManager.formatPhpReceipt(this.estimatedValue),
                width
            )).append("\n");
            sb.append(IOUManager.formatReceiptLine(
                "   Created " + this.getDateCreated() + "   Due " + this.getDueDate(),
                "SETTLED",
                width
            )).append("\n");
        } else {
            String statusLabel = this.isOverdue(todayDate) ? "OVERDUE" : this.getStatus().toUpperCase();
            sb.append(IOUManager.formatReceiptLine(
                itemNumber + ". " + dirLabel + "Item",
                "FOR RETURN",
                width
            )).append("\n");
            sb.append("   ").append(this.itemName).append(" [").append(this.itemCondition).append("]\n");
            sb.append("   ").append(this.getReason()).append("\n");
            sb.append(IOUManager.formatReceiptLine(
                "   Estimated value",
                IOUManager.formatPhpReceipt(this.estimatedValue),
                width
            )).append("\n");
            sb.append(IOUManager.formatReceiptLine(
                "   Created " + this.getDateCreated() + "   Due " + this.getDueDate(),
                statusLabel,
                width
            )).append("\n");
        }
        return sb.toString();
    }

    // Serializes this ItemIOU into a pipe-delimited text line for saving to file
    @Override
    public String toDataLine() {
        String cleanPerson = this.getPerson().replace("|", "/");
        String cleanItem = this.itemName.replace("|", "/");
        String cleanCond = this.itemCondition.replace("|", "/");
        String cleanReason = this.getReason().replace("|", "/");
        return "Item|" + this.getId() + "|" + this.getDirection() + "|" + cleanPerson + "|"
                + cleanItem + "|" + cleanCond + "|" + this.estimatedValue + "|"
                + this.getDateCreated() + "|" + this.getDueDate() + "|"
                + cleanReason + "|" + this.getStatus();
    }
}
