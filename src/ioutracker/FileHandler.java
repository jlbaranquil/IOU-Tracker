package ioutracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file persistence (loading and saving IOU records and exporting receipts)
 * using FileReader/BufferedReader and FileWriter/BufferedWriter with try-catch-finally.
 */
public class FileHandler {

    private String dataFilePath;

    // Constructs a FileHandler with the target storage file path
    public FileHandler(String dataFilePath) {
        this.setDataFilePath(dataFilePath);
    }

    // Returns the file path used for storing IOU records
    public String getDataFilePath() {
        return this.dataFilePath;
    }

    // Sets the file path used for storing IOU records
    public void setDataFilePath(String dataFilePath) {
        if (dataFilePath == null || dataFilePath.trim().isEmpty()) {
            this.dataFilePath = "iou_records.txt";
        } else {
            this.dataFilePath = dataFilePath.trim();
        }
    }

	// Loads IOU records from disk, returning an empty list if the file is missing or empty
	public List<IOURecord> loadRecords() {
		File file = new File(this.dataFilePath);
		if (!file.exists()) {
			List<IOURecord> emptyList = new ArrayList<IOURecord>();
			saveRecords(emptyList); // Optional: creates an empty iou_records.txt file
			return emptyList;
		}

		List<IOURecord> loadedList = new ArrayList<IOURecord>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				String trimmed = line.trim();
				if (trimmed.isEmpty()) {
					continue;
				}
				try {
					IOURecord rec = parseLineToRecord(trimmed);
					if (rec != null) {
						loadedList.add(rec);
					}
				} catch (Exception parseEx) {
					System.err.println("Skipping corrupted record line: " + trimmed);
				}
			}
		} catch (IOException ioEx) {
			System.err.println("Error reading data file: " + ioEx.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException closeEx) {
					System.err.println("Error closing file reader: " + closeEx.getMessage());
				}
			}
		}

		return loadedList;
	}

    // Saves the current list of IOU records to disk
    public boolean saveRecords(List<IOURecord> records) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.dataFilePath, false));
            for (int i = 0; i < records.size(); i++) {
                writer.write(records.get(i).toDataLine());
                writer.newLine();
            }
            writer.flush();
            return true;
        } catch (IOException ioEx) {
            System.err.println("Error saving records to file: " + ioEx.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException closeEx) {
                    System.err.println("Error closing file writer: " + closeEx.getMessage());
                }
            }
        }
    }

    // Exports a plain-text statement receipt to a specified .txt file
    public boolean exportReceiptToFile(String fileName, String receiptContent) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.write(receiptContent);
            writer.flush();
            return true;
        } catch (IOException ioEx) {
            System.err.println("Error exporting receipt to file: " + ioEx.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException closeEx) {
                    System.err.println("Error closing receipt writer: " + closeEx.getMessage());
                }
            }
        }
    }

    // Parses a single pipe-delimited line into a CashIOU or ItemIOU object
    private IOURecord parseLineToRecord(String line) throws InvalidAmountException, InvalidDateFormatException {
        String[] parts = line.split("\\|");
        if (parts.length < 10) {
            throw new IllegalArgumentException("Incomplete record data line.");
        }

        String type = parts[0];
        if ("Cash".equals(type) && parts.length >= 10) {
            int id = Integer.parseInt(parts[1]);
            String direction = parts[2];
            String person = parts[3];
            double origAmt = Double.parseDouble(parts[4]);
            double remBal = Double.parseDouble(parts[5]);
            String dateCreated = parts[6];
            String dueDate = parts[7];
            String reason = parts[8];
            String status = parts[9];
            return new CashIOU(id, direction, person, origAmt, remBal, dateCreated, dueDate, reason, status);
        } else if ("Item".equals(type) && parts.length >= 11) {
            int id = Integer.parseInt(parts[1]);
            String direction = parts[2];
            String person = parts[3];
            String itemName = parts[4];
            String condition = parts[5];
            double estValue = Double.parseDouble(parts[6]);
            String dateCreated = parts[7];
            String dueDate = parts[8];
            String reason = parts[9];
            String status = parts[10];
            return new ItemIOU(id, direction, person, itemName, condition, estValue, dateCreated, dueDate, reason, status);
        }
        return null;
    }
}
