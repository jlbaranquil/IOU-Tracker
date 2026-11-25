
package ioutracker;

/**
 * Defines the methods used to display IOU summaries
 * and format records in statement receipts.
 */
public interface Reportable {

    // Returns the category of the IOU (Cash or Item).
    String getCategoryName();

    // Returns a short summary of the IOU details.
    String getSummaryDetails();

    // Formats this IOU as a section of a receipt.
    String toReceiptSection(int itemNumber, String todayDate);
}
