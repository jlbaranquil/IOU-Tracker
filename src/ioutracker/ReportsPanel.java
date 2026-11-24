package ioutracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Screen 3: The People & Reports Split-Screen Panel.
 * Left side displays the Contact Directory; Right side generates the printable
 * Statement of Account receipt with Settle All and Export to File actions.
 */
public class ReportsPanel extends JPanel {


    public static final Font FONT_SUBHEADER    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_PERSON_NAME  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_PERSON_NET   = new Font("Consolas", Font.BOLD, 12);
    public static final Font FONT_PERSON_ITEMS = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_RECEIPT_MONO = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 12);

    public static final Color COLOR_BG_MAIN      = new Color(0xEC, 0xEF, 0xF1);
    public static final Color COLOR_BORDER_DARK  = new Color(0x78, 0x90, 0x9C);
    public static final Color COLOR_SUBHEADER_BG = new Color(0xDF, 0xE5, 0xE8);
    public static final Color COLOR_RECEIPT_WELL = new Color(0xCF, 0xD8, 0xDC);


    private IOUManager manager;
    private MainFrame mainFrame;

    private JLabel peopleCountLabel;
    private DefaultListModel<String> peopleListModel;
    private JList<String> peopleJList;

    private JLabel receiptHeaderPersonLabel;
    private JTextArea receiptTextArea;

    private JButton btnSettleAllPerson;
    private JButton btnExportReceipt;

    private String selectedPerson = "Juan Dela Cruz";
    private List<String> contactNamesList;

    // Constructs the ReportsPanel with references to the manager and mainFrame
    public ReportsPanel(IOUManager manager, MainFrame mainFrame) {
        this.manager = manager;
        this.mainFrame = mainFrame;
        this.contactNamesList = new ArrayList<String>();
        setLayout(new BorderLayout(12, 0));
        setBackground(COLOR_BG_MAIN);
        initComponents();
    }

    // Builds the left directory panel and right statement receipt panel
    private void initComponents() {
        // Left Half: The People Directory
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(360, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_DARK, 1));

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBackground(COLOR_SUBHEADER_BG);
        leftHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER_DARK),
            new EmptyBorder(9, 12, 9, 12)
        ));
        JLabel dirTitle = new JLabel("Directory");
        dirTitle.setFont(FONT_SUBHEADER);
        this.peopleCountLabel = new JLabel("0 Contacts");
        this.peopleCountLabel.setFont(FONT_SUBHEADER);
        this.peopleCountLabel.setForeground(new Color(0x54, 0x6E, 0x7A));
        leftHeader.add(dirTitle, BorderLayout.WEST);
        leftHeader.add(this.peopleCountLabel, BorderLayout.EAST);
        leftPanel.add(leftHeader, BorderLayout.NORTH);

        this.peopleListModel = new DefaultListModel<String>();
        this.peopleJList = new JList<String>(this.peopleListModel);
        this.peopleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.peopleJList.setCellRenderer(new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String person,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEC, 0xEF, 0xF1)),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                itemPanel.setBackground(isSelected ? DashboardPanel.COLOR_SELECTION_BG : Color.WHITE);

                JLabel nameLbl = new JLabel(person);
                nameLbl.setFont(FONT_PERSON_NAME);
                nameLbl.setForeground(new Color(0x1C, 0x28, 0x33));
                itemPanel.add(nameLbl);
                itemPanel.add(Box.createVerticalStrut(3));

                Map<String, Double> stats = manager.computePersonStats(person);
                double netCash = stats.get("netCash");
                int itemsTheyHave = stats.get("itemsTheyHave").intValue();
                int itemsYouHave = stats.get("itemsYouHave").intValue();

                JLabel netLbl = new JLabel();
                netLbl.setFont(FONT_PERSON_NET);
                if (netCash > 0) {
                    netLbl.setText("Net: Owes you " + IOUManager.formatPeso(netCash));
                    netLbl.setForeground(DashboardPanel.COLOR_GREEN_TEXT);
                } else if (netCash < 0) {
                    netLbl.setText("Net: You owe " + IOUManager.formatPeso(Math.abs(netCash)));
                    netLbl.setForeground(DashboardPanel.COLOR_RED_TEXT);
                } else {
                    netLbl.setText("Net: Settled (\u20B10.00)");
                    netLbl.setForeground(new Color(0x54, 0x6E, 0x7A));
                }
                itemPanel.add(netLbl);

                StringBuilder itemInfo = new StringBuilder();
                if (itemsTheyHave > 0) {
                    itemInfo.append("Has ").append(itemsTheyHave).append(" of your item(s)");
                }
                if (itemsYouHave > 0) {
                    if (itemInfo.length() > 0) itemInfo.append(" | ");
                    itemInfo.append("You hold ").append(itemsYouHave).append(" item(s)");
                }
                if (itemInfo.length() > 0) {
                    itemPanel.add(Box.createVerticalStrut(2));
                    JLabel itemsLbl = new JLabel(itemInfo.toString());
                    itemsLbl.setFont(FONT_PERSON_ITEMS);
                    itemsLbl.setForeground(new Color(0x54, 0x6E, 0x7A));
                    itemPanel.add(itemsLbl);
                }

                return itemPanel;
            }
        });

        this.peopleJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String picked = peopleJList.getSelectedValue();
                    if (picked != null) {
                        selectedPerson = picked;
                        renderStatementReceipt(selectedPerson);
                    }
                }
            }
        });

        JScrollPane listScroll = new JScrollPane(this.peopleJList);
        listScroll.setBorder(null);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        // Right Half: The Statement / Receipt Generator
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_DARK, 1));

        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setBackground(COLOR_SUBHEADER_BG);
        rightHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER_DARK),
            new EmptyBorder(9, 12, 9, 12)
        ));
        JLabel stmtTitle = new JLabel("Statement / Receipt");
        stmtTitle.setFont(FONT_SUBHEADER);
        this.receiptHeaderPersonLabel = new JLabel("");
        this.receiptHeaderPersonLabel.setFont(FONT_SUBHEADER);
        this.receiptHeaderPersonLabel.setForeground(DashboardPanel.COLOR_BLUE_BORDER);
        rightHeader.add(stmtTitle, BorderLayout.WEST);
        rightHeader.add(this.receiptHeaderPersonLabel, BorderLayout.EAST);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        this.receiptTextArea = new JTextArea();
        this.receiptTextArea.setFont(FONT_RECEIPT_MONO);
        this.receiptTextArea.setEditable(false);
        this.receiptTextArea.setBackground(Color.WHITE);
        this.receiptTextArea.setForeground(new Color(0x1C, 0x28, 0x33));
        this.receiptTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x90, 0xA4, 0xAE), 1),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel receiptWell = new JPanel(new BorderLayout());
        receiptWell.setBackground(COLOR_RECEIPT_WELL);
        receiptWell.setBorder(new EmptyBorder(14, 18, 14, 18));
        receiptWell.add(new JScrollPane(this.receiptTextArea), BorderLayout.CENTER);
        rightPanel.add(receiptWell, BorderLayout.CENTER);

        JPanel receiptActionsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        receiptActionsBar.setBackground(new Color(0xF5, 0xF7, 0xF8));
        receiptActionsBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DashboardPanel.COLOR_BORDER));

        this.btnSettleAllPerson = new JButton("Settle All with Person");
        this.btnSettleAllPerson.setFont(FONT_BUTTON);
        this.btnSettleAllPerson.setBackground(DashboardPanel.COLOR_GREEN_BORDER);
        this.btnSettleAllPerson.setForeground(Color.WHITE);
        this.btnSettleAllPerson.setFocusPainted(false);

        this.btnExportReceipt = new JButton("Export Receipt to File");
        this.btnExportReceipt.setFont(FONT_BUTTON);
        this.btnExportReceipt.setBackground(DashboardPanel.COLOR_BLUE_BORDER);
        this.btnExportReceipt.setForeground(Color.WHITE);
        this.btnExportReceipt.setFocusPainted(false);

        this.btnSettleAllPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSettleAllWithPerson();
            }
        });

        this.btnExportReceipt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExportReceiptToFile();
            }
        });

        receiptActionsBar.add(this.btnSettleAllPerson);
        receiptActionsBar.add(this.btnExportReceipt);
        rightPanel.add(receiptActionsBar, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    // Sets the currently selected contact person for the statement receipt
    public void setSelectedPerson(String personName) {
        if (personName != null && !personName.trim().isEmpty()) {
            this.selectedPerson = personName.trim();
        }
    }

    // Refreshes the contact directory list and regenerates the statement receipt
    public void refreshReportsView() {
        List<IOURecord> allRecs = this.manager.getAllRecords();
        List<String> uniquePeople = new ArrayList<String>();
        for (int i = 0; i < allRecs.size(); i++) {
            String p = allRecs.get(i).getPerson();
            boolean exists = false;
            for (int j = 0; j < uniquePeople.size(); j++) {
                if (uniquePeople.get(j).equalsIgnoreCase(p)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                uniquePeople.add(p);
            }
        }
        java.util.Collections.sort(uniquePeople, String.CASE_INSENSITIVE_ORDER);
        this.contactNamesList = uniquePeople;

        this.peopleCountLabel.setText(this.contactNamesList.size() + " Contacts");
        this.peopleListModel.clear();

        int indexToSelect = 0;
        for (int i = 0; i < this.contactNamesList.size(); i++) {
            String name = this.contactNamesList.get(i);
            this.peopleListModel.addElement(name);
            if (name.equalsIgnoreCase(this.selectedPerson)) {
                indexToSelect = i;
            }
        }

        if (!this.contactNamesList.isEmpty()) {
            this.peopleJList.setSelectedIndex(indexToSelect);
            this.selectedPerson = this.contactNamesList.get(indexToSelect);
        } else {
            this.selectedPerson = null;
        }

        renderStatementReceipt(this.selectedPerson);
    }

    // Updates the monospace receipt text area and action buttons for the given person
    private void renderStatementReceipt(String personName) {
        this.receiptHeaderPersonLabel.setText(personName != null ? personName : "");
        this.receiptTextArea.setText(this.manager.generatePlainTextReceipt(personName));
        this.receiptTextArea.setCaretPosition(0);

        if (personName != null) {
            Map<String, Double> stats = this.manager.computePersonStats(personName);
            int activeCount = stats.get("activeCount").intValue();
            this.btnSettleAllPerson.setText("Settle All with " + personName);
            this.btnSettleAllPerson.setEnabled(activeCount > 0);
            this.btnExportReceipt.setEnabled(true);
        } else {
            this.btnSettleAllPerson.setText("Settle All");
            this.btnSettleAllPerson.setEnabled(false);
            this.btnExportReceipt.setEnabled(false);
        }
    }

    // Confirms and settles all active IOUs with the currently selected person
    private void handleSettleAllWithPerson() {
        if (this.selectedPerson == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Mark all active cash and item IOUs with " + this.selectedPerson + " as Paid / Returned?",
            "Settle All Active IOUs",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            this.manager.settleAllForPerson(this.selectedPerson);
            this.mainFrame.triggerStorageSyncIndicator();
            refreshReportsView();
        }
    }

    // Opens a file explorer picker and exports the current person's plain-text receipt to a .txt file
    private void handleExportReceiptToFile() {
        if (this.selectedPerson == null) {
            return;
        }
        String receiptText = this.manager.generatePlainTextReceipt(this.selectedPerson);
        String safeName = this.selectedPerson.replaceAll("[^a-zA-Z0-9]+", "_");
        String defaultFileName = "IOU_Receipt_" + safeName + ".txt";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Receipt to File");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath = filePath + ".txt";
            }

            boolean saved = this.manager.getFileHandler().exportReceiptToFile(filePath, receiptText);
            if (saved) {
                JOptionPane.showMessageDialog(
                    this,
                    "Printable statement receipt has been saved to:\n\"" + filePath + "\"",
                    "Export Receipt to File",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to save receipt to:\n\"" + filePath + "\"",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
