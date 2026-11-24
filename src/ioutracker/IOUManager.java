package ioutracker;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Business logic controller that manages IOU records in memory, performs
 * search/filter/sort, recursive balance calculations, contact directory
 * management, and plain-text statement receipt generation.
 */
public class IOUManager {

    public static final String TODAY_DATE = LocalDate.now().toString();

    private List<IOURecord> records;
    private FileHandler fileHandler;
    private int nextRecordId;

    // Initializes the manager, loads persisted records from FileHandler, and computes nextRecordId
    public IOUManager(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.records = new ArrayList<IOURecord>(this.fileHandler.loadRecords());
        this.nextRecordId = 1;
        for (int i = 0; i < this.records.size(); i++) {
            int recId = this.records.get(i).getId();
            if (recId >= this.nextRecordId) {
                this.nextRecordId = recId + 1;
            }
        }
    }

    // Returns the current system date string (YYYY-MM-DD) for overdue checks and default dates
    public String getTodayDate() {
        return LocalDate.now().toString();
    }

    // Returns the list of all IOU records
    public List<IOURecord> getAllRecords() {
        return this.records;
    }

    // Returns the FileHandler instance
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    // Generates and increments the next unique record ID
    public int getNextRecordId() {
        return this.nextRecordId++;
    }

    // Adds a new IOURecord to the top of the list and saves to disk
    public void addRecord(IOURecord record) {
        if (record != null) {
            this.records.add(0, record);
            saveAllToDisk();
        }
    }

    // Deletes an IOURecord by its ID and saves to disk
    public boolean deleteRecordById(int id) {
        for (int i = 0; i < this.records.size(); i++) {
            if (this.records.get(i).getId() == id) {
                this.records.remove(i);
                saveAllToDisk();
                return true;
            }
        }
        return false;
    }

    // Finds and returns an IOURecord by its ID, or null if not found
    public IOURecord findRecordById(int id) {
        for (int i = 0; i < this.records.size(); i++) {
            IOURecord rec = this.records.get(i);
            if (rec.getId() == id) {
                return rec;
            }
        }
        return null;
    }

    // Filters and sorts the IOU records based on dashboard toolbar criteria
    public List<IOURecord> searchFilterAndSort(String searchText, String dirFilter,
                                              String catFilter, String statusFilter, String sortBy) {
        List<IOURecord> filtered = new ArrayList<IOURecord>();
        String query = (searchText == null) ? "" : searchText.trim().toLowerCase();
        String todayDate = getTodayDate();

        for (int i = 0; i < this.records.size(); i++) {
            IOURecord rec = this.records.get(i);

            // 1. Keyword search check
            if (!query.isEmpty()) {
                String itemText = (rec instanceof ItemIOU) ? ((ItemIOU) rec).getItemName() : "";
                String haystack = (rec.getPerson() + " " + rec.getReason() + " "
                        + itemText + " " + rec.getCategoryName()).toLowerCase();
                if (!haystack.contains(query)) {
                    continue;
                }
            }

            // 2. Direction filter check
            if (dirFilter != null && !"ALL".equals(dirFilter) && !rec.getDirection().equals(dirFilter)) {
                continue;
            }

            // 3. Category filter check
            if (catFilter != null && !"ALL".equals(catFilter) && !rec.getCategoryName().equals(catFilter)) {
                continue;
            }

            // 4. Status filter check
            if ("UNSETTLED".equals(statusFilter) && "Settled".equals(rec.getStatus())) {
                continue;
            }
            if ("SETTLED".equals(statusFilter) && !"Settled".equals(rec.getStatus())) {
                continue;
            }
            if ("OVERDUE".equals(statusFilter) && !rec.isOverdue(todayDate)) {
                continue;
            }

            filtered.add(rec);
        }

        // 5. Sort filtered records
        Collections.sort(filtered, new Comparator<IOURecord>() {
            @Override
            public int compare(IOURecord a, IOURecord b) {
                if ("DUE_ASC".equals(sortBy)) {
                    return a.getDueDate().compareTo(b.getDueDate());
                } else if ("AMOUNT_DESC".equals(sortBy)) {
                    return Double.compare(b.getComparableValue(), a.getComparableValue());
                } else if ("NAME_ASC".equals(sortBy)) {
                    return a.getPerson().compareToIgnoreCase(b.getPerson());
                }
                return 0;
            }
        });

        return filtered;
    }

    // Recursively computes the total active cash balance for a given direction ("THEY_OWE" or "I_OWE")
    public double calculateTotalCashRecursive(int index, String direction) {
        if (index >= this.records.size()) {
            return 0.0;
        }
        IOURecord current = this.records.get(index);
        double currentAmount = 0.0;
        if (!"Settled".equals(current.getStatus())
                && current.getDirection().equals(direction)
                && current instanceof CashIOU) {
            currentAmount = ((CashIOU) current).getRemainingBalance();
        }
        return currentAmount + calculateTotalCashRecursive(index + 1, direction);
    }

    // Counts the number of unsettled borrowed items for a given direction
    public int countActiveItemsByDirection(String direction) {
        int count = 0;
        for (int i = 0; i < this.records.size(); i++) {
            IOURecord rec = this.records.get(i);
            if (!"Settled".equals(rec.getStatus())
                    && rec.getDirection().equals(direction)
                    && rec instanceof ItemIOU) {
                count++;
            }
        }
        return count;
    }

    // Returns an alphabetically sorted list of unique contact names dynamically from current records
    public List<String> getAllUniqueContacts() {
        Set<String> contactSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < this.records.size(); i++) {
            String person = this.records.get(i).getPerson();
            if (person != null && !person.trim().isEmpty()) {
                contactSet.add(person.trim());
            }
        }
        return new ArrayList<String>(contactSet);
    }

    // Computes net statistics for a specific person and returns them in a HashMap
    public Map<String, Double> computePersonStats(String personName) {
        double theyOweCash = 0.0;
        double iOweCash = 0.0;
        double itemsTheyHave = 0.0;
        double itemsYouHave = 0.0;
        double activeCount = 0.0;

        for (int i = 0; i < this.records.size(); i++) {
            IOURecord rec = this.records.get(i);
            if (!rec.getPerson().equalsIgnoreCase(personName)) {
                continue;
            }
            if ("Settled".equals(rec.getStatus())) {
                continue;
            }
            activeCount++;
            if ("THEY_OWE".equals(rec.getDirection())) {
                if (rec instanceof CashIOU) {
                    theyOweCash += ((CashIOU) rec).getRemainingBalance();
                } else {
                    itemsTheyHave++;
                }
            } else {
                if (rec instanceof CashIOU) {
                    iOweCash += ((CashIOU) rec).getRemainingBalance();
                } else {
                    itemsYouHave++;
                }
            }
        }

        Map<String, Double> stats = new HashMap<String, Double>();
        stats.put("theyOweCash", theyOweCash);
        stats.put("iOweCash", iOweCash);
        stats.put("netCash", theyOweCash - iOweCash);
        stats.put("itemsTheyHave", itemsTheyHave);
        stats.put("itemsYouHave", itemsYouHave);
        stats.put("activeCount", activeCount);
        return stats;
    }

    // Marks all active cash and item IOUs for a given person as Settled and saves to disk
    public int settleAllForPerson(String personName) {
        int settledCount = 0;
        for (int i = 0; i < this.records.size(); i++) {
            IOURecord rec = this.records.get(i);
            if (rec.getPerson().equalsIgnoreCase(personName) && !"Settled".equals(rec.getStatus())) {
                rec.markAsSettled();
                settledCount++;
            }
        }
        if (settledCount > 0) {
            saveAllToDisk();
        }
        return settledCount;
    }

    // Generates the formatted plain-text Statement of Account receipt for a person
    public String generatePlainTextReceipt(String personName) {
        if (personName == null || personName.trim().isEmpty()) {
            return "No contact selected.";
        }

        String todayDate = getTodayDate();
        int width = 62;
        String doubleDiv = "==============================================================";
        String singleDiv = "--------------------------------------------------------------";

        List<IOURecord> personRecords = new ArrayList<IOURecord>();
        List<IOURecord> activeRecs = new ArrayList<IOURecord>();
        List<IOURecord> settledRecs = new ArrayList<IOURecord>();

        for (int i = 0; i < this.records.size(); i++) {
            IOURecord r = this.records.get(i);
            if (r.getPerson().equalsIgnoreCase(personName)) {
                personRecords.add(r);
                if ("Settled".equals(r.getStatus())) {
                    settledRecs.add(r);
                } else {
                    activeRecs.add(r);
                }
            }
        }

        Map<String, Double> stats = computePersonStats(personName);
        StringBuilder sb = new StringBuilder();

        sb.append(centerReceiptText("I O U   T R A C K E R", width)).append("\n");
        sb.append(centerReceiptText("STATEMENT OF ACCOUNT", width)).append("\n");
        sb.append(doubleDiv).append("\n");
        sb.append("Person  : ").append(personName).append("\n");
        sb.append("As of   : ").append(todayDate).append("\n");
        sb.append("Records : ").append(personRecords.size()).append(" total (")
          .append(activeRecs.size()).append(" active, ")
          .append(settledRecs.size()).append(" settled)\n");
        sb.append(singleDiv).append("\n");
        sb.append("ACTIVE IOUs (").append(activeRecs.size()).append(")\n");
        sb.append(singleDiv).append("\n");

        if (activeRecs.isEmpty()) {
            sb.append("  (none)\n\n");
        } else {
            for (int i = 0; i < activeRecs.size(); i++) {
                sb.append(activeRecs.get(i).toReceiptSection(i + 1, todayDate)).append("\n");
            }
        }

        sb.append(singleDiv).append("\n");
        sb.append("SETTLED / PAST IOUs (").append(settledRecs.size()).append(")\n");
        sb.append(singleDiv).append("\n");

        if (settledRecs.isEmpty()) {
            sb.append("  (none yet)\n\n");
        } else {
            for (int i = 0; i < settledRecs.size(); i++) {
                sb.append(settledRecs.get(i).toReceiptSection(i + 1, todayDate)).append("\n");
            }
        }

        double theyOweCash = stats.get("theyOweCash");
        double iOweCash = stats.get("iOweCash");
        double netCash = stats.get("netCash");

        sb.append(doubleDiv).append("\n");
        sb.append(formatReceiptLine("Subtotal they owe you (cash)", formatPhpReceipt(theyOweCash), width)).append("\n");
        sb.append(formatReceiptLine("Subtotal you owe them (cash)", formatPhpReceipt(iOweCash), width)).append("\n");
        sb.append(singleDiv).append("\n");
        sb.append("FINAL SETTLEMENT\n");

        if (netCash > 0) {
            sb.append(formatReceiptLine("  " + personName + " owes you", formatPhpReceipt(netCash), width)).append("\n");
        } else if (netCash < 0) {
            sb.append(formatReceiptLine("  You owe " + personName, formatPhpReceipt(Math.abs(netCash)), width)).append("\n");
        } else {
            sb.append(formatReceiptLine("  All cash balances settled", formatPhpReceipt(0.0), width)).append("\n");
        }

        sb.append(doubleDiv).append("\n");
        sb.append("Generated by IOU Tracker \u00B7 ").append(todayDate);

        return sb.toString();
    }

    // Persists all current records to the storage file via FileHandler
    public boolean saveAllToDisk() {
        return this.fileHandler.saveRecords(this.records);
    }

    // Formats a number with the Philippine Peso sign (e.g., "₱1,450.00")
    public static String formatPeso(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "\u20B1" + df.format(amount);
    }

    // Formats a number with "PHP " for plain-text receipts (e.g., "PHP 1,450.00")
    public static String formatPhpReceipt(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "PHP " + df.format(amount);
    }

    // Pads a left and right string with spaces so the total line width equals width
    public static String formatReceiptLine(String left, String right, int width) {
        int gap = Math.max(1, width - left.length() - right.length());
        StringBuilder sb = new StringBuilder(left);
        for (int i = 0; i < gap; i++) {
            sb.append(' ');
        }
        sb.append(right);
        return sb.toString();
    }

    // Centers a text string within a given character width for receipt headers
    public static String centerReceiptText(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pad; i++) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }
}