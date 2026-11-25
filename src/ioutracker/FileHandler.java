
package ioutracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Handles loading, saving, and exporting IOU records.
public class FileHandler {

    private String dataFilePath;

    public FileHandler(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    //Saves all records to the data file.
    public boolean saveRecords(List<IOURecord> records) {

    Path path = Paths.get(dataFilePath);
    Path parent = path.getParent();

    try {
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8)) {

            for (IOURecord record : records) {
                writer.write(record.toDataLine());
                writer.newLine();
            }
        }

        return true;

    } catch (IOException e) {
        System.err.println("Error saving IOU records: " + e.getMessage());
        return false;
        }
    }


    /*
      Exports receipt content into a text file.
      Parameters:
      filePath - destination of the receipt file
      receiptContent - text content of the receipt
     */
    
    public boolean exportReceiptToFile(
        String filePath, String receiptContent) {

    Path path = Paths.get(filePath);
    Path parent = path.getParent();

    try {
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8)) {

            writer.write(receiptContent);
        }

        return true;

    } catch (IOException e) {
        System.err.println("Error exporting receipt: " + e.getMessage());
        return false;
    }
}


    // Converts one text-file line into a CashIOU or ItemIOU.
    private IOURecord parseLineToRecord(String line)
            throws InvalidAmountException,
                   InvalidDateFormatException {

        String[] fields = line.split("\\|", -1);

        if (fields.length == 10 && fields[0].equals("Cash")) {

            return new CashIOU(
                Integer.parseInt(fields[1]),
                fields[2],
                fields[3],
                Double.parseDouble(fields[4]),
                Double.parseDouble(fields[5]),
                fields[6],
                fields[7],
                fields[8],
                fields[9]
            );

        } else if (fields.length == 11 &&
                   fields[0].equals("Item")) {

            return new ItemIOU(
                Integer.parseInt(fields[1]),
                fields[2],
                fields[3],
                fields[4],
                fields[5],
                Double.parseDouble(fields[6]),
                fields[7],
                fields[8],
                fields[9],
                fields[10]
            );
        }

        throw new IllegalArgumentException(
            "Invalid record type or number of fields."
        );
    }

    /*
      Starts with an empty record collection.
      The application creates a fresh data file when none exists.
     */
    
public List<IOURecord> loadRecords() {

    List<IOURecord> records = new ArrayList<>();
    Path path = Paths.get(dataFilePath);

    try {
        if (!Files.exists(path)) {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.createFile(path);
            return records;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    IOURecord record = parseLineToRecord(line);
                    records.add(record);
                } catch (Exception e) {
                    System.err.println(
                        "Skipped invalid record: " + line
                    );
                }
            }
        }

    } catch (IOException e) {
        System.err.println(
            "Error loading IOU records: " + e.getMessage()
        );
    }

    return records;
}

}
