package ioutracker;

/**
 * Interface implemented by classes that can be summarized in the UI table
 * and formatted into a plain-text receipt section.
 */
public interface Reportable {

    // Returns the category name of the record (e.g., "Cash" or "Item")
    String getCategoryName();

    // Returns a formatted summary string of the record details for the dashboard table
    String getSummaryDetails();

    // Formats this record as a multi-line entry for the plain-text statement receipt
    String toReceiptSection(int itemNumber, String todayDate);
}
