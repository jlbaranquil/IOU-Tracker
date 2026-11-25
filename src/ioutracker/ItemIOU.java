
package ioutracker;

// Represents an IOU involving a physical item.
public class ItemIOU extends IOURecord implements Reportable {

    private String itemName;
    private String condition;
    private double estimatedValue;

    public ItemIOU(int id, String direction, String personName,
                   String itemName, String condition,
                   double estimatedValue, String dateCreated,
                   String dueDate, String reason, String status)
                   throws InvalidDateFormatException,
                          InvalidAmountException {

        super(id, direction, personName, dateCreated,
              dueDate, reason, status);

        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Item name cannot be blank."
            );
        }

        if (!Double.isFinite(estimatedValue) || estimatedValue < 0) {
            throw new InvalidAmountException(
                "Estimated value cannot be negative."
            );
        }

        this.itemName = itemName.trim();
        this.condition = (condition == null || condition.trim().isEmpty())
                ? "Good" : condition.trim();
        this.estimatedValue = estimatedValue;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCondition() {
        return condition;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }
    public String getItemCondition() {
    return condition;
    }

    @Override
    public double getComparableValue() {
    return estimatedValue;
    }

    @Override
    public void markAsSettled() {
        status = SETTLED;
    }

    @Override
    public String getCategoryName() {
        return "Item";
    }

    @Override
    public String getSummaryDetails() {
        return String.format(
            "%s | Condition: %s | Estimated Value: ₱%.2f",
            itemName, condition, estimatedValue
        );
    }

    @Override
    public String toReceiptSection(int itemNumber, String todayDate) {
        return String.format(
            "%d. ITEM IOU%n" +
            "   Person: %s%n" +
            "   Item: %s%n" +
            "   Condition: %s%n" +
            "   Estimated Value: ₱%.2f%n" +
            "   Due Date: %s%n" +
            "   Status: %s%n",
            itemNumber, personName, itemName,
            condition, estimatedValue, dueDate, status
        );
    }

    // Converts the record into the required text-file format.
    @Override
    public String toDataLine() {
        return String.join("|",
            "Item",
            String.valueOf(id),
            direction,
            clean(personName),
            clean(itemName),
            clean(condition),
            String.valueOf(estimatedValue),
            dateCreated,
            dueDate,
            clean(reason),
            status
        );
    }

    private String clean(String value) {
        return value.replace("|", "/");
    }
}
