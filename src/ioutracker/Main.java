package ioutracker;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point.
 * Launches the IOU Tracker desktop application on the Swing Event Dispatch Thread.
 */
public class Main {

    // Main method that initializes the persistence layer, logic controller, and Swing GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    System.err.println("Using default Swing look and feel.");
                }

                FileHandler fileHandler = new FileHandler("iou_records.txt");
                IOUManager manager = new IOUManager(fileHandler);
                MainFrame frame = new MainFrame(manager);
                frame.setVisible(true);
            }
        });
    }
}
