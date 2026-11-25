
package ioutracker;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Represents an IOU involving money.
public class CashIOU extends IOURecord implements Reportable {

    private double originalAmount;
    private double remainingBalance;

    public CashIOU(int id, String direction, String personName,
                   double originalAmount, double remainingBalance,
                   String dateCreated, String dueDate,
                   String reason, String status)
                   throws InvalidDateFormatException,
                          InvalidAmountException {

        super(id, direction, personName, dateCreated,
              dueDate, reason, status);

        if (!Double.isFinite(originalAmount) || originalAmount <= 0) {
            throw new InvalidAmountException(
                "Original amount must be greater than zero."
            );
        }

        if (!Double.isFinite(remainingBalance) || remainingBalance < 0) {
            throw new InvalidAmountException(
                "Remaining balance cannot be negative."
            );
        }

        if (remainingBalance > originalAmount) {
            throw new InvalidAmountException(
                "Remaining balance cannot exceed the original amount."
            );
        }

        this.originalAmount = originalAmount;
        this.remainingBalance = remainingBalance;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }
    @Override
    public double getComparableValue() {
    return remainingBalance;
}

    /**
     * Records a payment and updates the remaining balance.
     */
    public void recordPayment(double payment)
            throws InvalidAmountException {

        if (!Double.isFinite(payment) || payment <= 0) {
            throw new InvalidAmountException(
                "Payment must be greater than zero."
            );
        }

        BigDecimal paymentValue = BigDecimal.valueOf(payment)
                .setScale(2, RoundingMode.UNNECESSARY);

        BigDecimal balance = BigDecimal.valueOf(remainingBalance)
                .setScale(2, RoundingMode.HALF_UP);

        if (paymentValue.compareTo(balance) > 0) {
            throw new InvalidAmountException(
                "Payment cannot exceed the remaining balance."
            );
        }

        BigDecimal newBalance = balance.subtract(paymentValue)
                .setScale(2, RoundingMode.HALF_UP);

        remainingBalance = newBalance.doubleValue();

        if (remainingBalance == 0) {
            status = SETTLED;
        } else {
            status = PARTIALLY_PAID;
        }
    }

    @Override
    public void markAsSettled() {
        remainingBalance = 0.00;
        status = SETTLED;
    }

    @Override
    public String getCategoryName() {
        return "Cash";
    }

    @Override
    public String getSummaryDetails() {
        return String.format(
            "Original: ₱%.2f | Remaining: ₱%.2f",
            originalAmount, remainingBalance
        );
    }

    @Override
    public String toReceiptSection(int itemNumber, String todayDate) {
        return String.format(
            "%d. CASH IOU%n" +
            "   Person: %s%n" +
            "   Original Amount: ₱%.2f%n" +
            "   Remaining Balance: ₱%.2f%n" +
            "   Due Date: %s%n" +
            "   Status: %s%n",
            itemNumber, personName, originalAmount,
            remainingBalance, dueDate, status
        );
    }

    // Converts the record into the required text-file format.
    @Override
    public String toDataLine() {
        return String.join("|",
            "Cash",
            String.valueOf(id),
            direction,
            clean(personName),
            String.valueOf(originalAmount),
            String.valueOf(remainingBalance),
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
