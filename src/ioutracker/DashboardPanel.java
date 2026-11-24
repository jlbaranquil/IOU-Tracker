package ioutracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen 1: The Dashboard Panel.
 * Displays the 3 summary boxes, search/filter/sort toolbar, interactive IOU JTable,
 * and bottom record action buttons.
 */
public class DashboardPanel extends JPanel {


    public static final Font FONT_SUMMARY_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SUMMARY_MAIN  = new Font("Consolas", Font.BOLD, 22);
    public static final Font FONT_SUMMARY_SUB   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TOOLBAR_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_INPUT         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TABLE_HEADER  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE_CELL    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BADGE         = new Font("Consolas", Font.BOLD, 12);
    public static final Font FONT_BUTTON        = new Font("Segoe UI", Font.BOLD, 12);

    public static final Color COLOR_BG_MAIN      = new Color(0xEC, 0xEF, 0xF1);
    public static final Color COLOR_PANEL_WHITE  = Color.WHITE;
    public static final Color COLOR_BORDER       = new Color(0xB0, 0xBE, 0xC5);
    public static final Color COLOR_BORDER_DARK  = new Color(0x78, 0x90, 0x9C);

    public static final Color COLOR_GREEN_BG     = new Color(0xE8, 0xF5, 0xE9);
    public static final Color COLOR_GREEN_BORDER = new Color(0x2E, 0x7D, 0x32);
    public static final Color COLOR_GREEN_TEXT   = new Color(0x1B, 0x5E, 0x20);

    public static final Color COLOR_RED_BG       = new Color(0xFF, 0xEB, 0xEE);
    public static final Color COLOR_RED_BORDER   = new Color(0xC6, 0x28, 0x28);
    public static final Color COLOR_RED_TEXT     = new Color(0xB7, 0x1C, 0x1C);

    public static final Color COLOR_BLUE_BG      = new Color(0xE3, 0xF2, 0xFD);
    public static final Color COLOR_BLUE_BORDER  = new Color(0x15, 0x65, 0xC0);
    public static final Color COLOR_BLUE_TEXT    = new Color(0x0D, 0x47, 0xA1);

    public static final Color COLOR_SELECTION_BG = new Color(0xCF, 0xE8, 0xFC);


    private IOUManager manager;
    private MainFrame mainFrame;

    private JLabel sumOwedToMeCashLbl;
    private JLabel sumOwedToMeItemsLbl;
    private JLabel sumIOweCashLbl;
    private JLabel sumIOweItemsLbl;
    private JLabel sumNetCashLbl;
    private JLabel sumNetStatusLbl;

    private JTextField searchField;
    private JComboBox<String> filterDirectionBox;
    private JComboBox<String> filterCategoryBox;
    private JComboBox<String> filterStatusBox;
    private JComboBox<String> sortBox;

    private DefaultTableModel tableModel;
    private JTable iouTable;

    private JLabel selectedRowLabel;
    private JButton btnRecordPayment;
    private JButton btnEditRecord;
    private JButton btnDeleteRecord;
    private JButton btnViewStatement;

    private List<IOURecord> displayedRecords;
    private int selectedRecordId = -1;

    // Constructs the DashboardPanel with references to the manager and mainFrame
    public DashboardPanel(IOUManager manager, MainFrame mainFrame) {
        this.manager = manager;
        this.mainFrame = mainFrame;
        this.displayedRecords = new ArrayList<IOURecord>();
        setLayout(new BorderLayout(0, 12));
        setBackground(COLOR_BG_MAIN);
        initComponents();
    }

    // Assembles the summary bar, toolbar, table scroll pane, and bottom action bar
    private void initComponents() {
        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(createSummaryBar(), BorderLayout.NORTH);
        topContainer.add(createToolbarPanel(), BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomActionBar(), BorderLayout.SOUTH);
    }

    // Creates the top 3 summary boxes (Owed to Me, I Owe Others, Net Balance)
    private JPanel createSummaryBar() {
        JPanel summaryBar = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryBar.setOpaque(false);

        // Box 1: Owed to Me (Green)
        JPanel greenBox = new JPanel(new GridLayout(3, 1, 0, 2));
        greenBox.setBackground(COLOR_GREEN_BG);
        greenBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_GREEN_BORDER, 1),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel greenTitle = new JLabel("OWED TO ME");
        greenTitle.setFont(FONT_SUMMARY_LABEL);
        greenTitle.setForeground(COLOR_GREEN_TEXT);
        this.sumOwedToMeCashLbl = new JLabel("\u20B10.00");
        this.sumOwedToMeCashLbl.setFont(FONT_SUMMARY_MAIN);
        this.sumOwedToMeCashLbl.setForeground(COLOR_GREEN_TEXT);
        this.sumOwedToMeItemsLbl = new JLabel("Borrowed from me: 0 items");
        this.sumOwedToMeItemsLbl.setFont(FONT_SUMMARY_SUB);
        this.sumOwedToMeItemsLbl.setForeground(COLOR_GREEN_TEXT);
        greenBox.add(greenTitle);
        greenBox.add(this.sumOwedToMeCashLbl);
        greenBox.add(this.sumOwedToMeItemsLbl);

        // Box 2: I Owe Others (Red)
        JPanel redBox = new JPanel(new GridLayout(3, 1, 0, 2));
        redBox.setBackground(COLOR_RED_BG);
        redBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_RED_BORDER, 1),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel redTitle = new JLabel("I OWE OTHERS");
        redTitle.setFont(FONT_SUMMARY_LABEL);
        redTitle.setForeground(COLOR_RED_TEXT);
        this.sumIOweCashLbl = new JLabel("\u20B10.00");
        this.sumIOweCashLbl.setFont(FONT_SUMMARY_MAIN);
        this.sumIOweCashLbl.setForeground(COLOR_RED_TEXT);
        this.sumIOweItemsLbl = new JLabel("Items to return: 0 items");
        this.sumIOweItemsLbl.setFont(FONT_SUMMARY_SUB);
        this.sumIOweItemsLbl.setForeground(COLOR_RED_TEXT);
        redBox.add(redTitle);
        redBox.add(this.sumIOweCashLbl);
        redBox.add(this.sumIOweItemsLbl);

        // Box 3: Net Balance (Blue)
        JPanel blueBox = new JPanel(new GridLayout(3, 1, 0, 2));
        blueBox.setBackground(COLOR_BLUE_BG);
        blueBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BLUE_BORDER, 1),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel blueTitle = new JLabel("NET BALANCE");
        blueTitle.setFont(FONT_SUMMARY_LABEL);
        blueTitle.setForeground(COLOR_BLUE_TEXT);
        this.sumNetCashLbl = new JLabel("\u20B10.00");
        this.sumNetCashLbl.setFont(FONT_SUMMARY_MAIN);
        this.sumNetCashLbl.setForeground(COLOR_BLUE_TEXT);
        this.sumNetStatusLbl = new JLabel("All Cash Balances Even");
        this.sumNetStatusLbl.setFont(FONT_SUMMARY_SUB);
        this.sumNetStatusLbl.setForeground(COLOR_BLUE_TEXT);
        blueBox.add(blueTitle);
        blueBox.add(this.sumNetCashLbl);
        blueBox.add(this.sumNetStatusLbl);

        summaryBar.add(greenBox);
        summaryBar.add(redBox);
        summaryBar.add(blueBox);
        return summaryBar;
    }

    // Creates the search, filter, and sort bar
    private JPanel createToolbarPanel() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(COLOR_PANEL_WHITE);
        toolbar.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(FONT_TOOLBAR_LABEL);
        this.searchField = new JTextField(16);
        this.searchField.setFont(FONT_INPUT);
        this.searchField.setToolTipText("Type a name or a keyword...");

        JLabel dirLbl = new JLabel("Direction:");
        dirLbl.setFont(FONT_TOOLBAR_LABEL);
        this.filterDirectionBox = new JComboBox<String>(new String[]{
            "Show All", "They Owe Me", "I Owe Them"
        });
        this.filterDirectionBox.setFont(FONT_INPUT);

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(FONT_TOOLBAR_LABEL);
        this.filterCategoryBox = new JComboBox<String>(new String[]{
            "Show All", "Cash Money", "Borrowed Items"
        });
        this.filterCategoryBox.setFont(FONT_INPUT);

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setFont(FONT_TOOLBAR_LABEL);
        this.filterStatusBox = new JComboBox<String>(new String[]{
            "Show All", "Unsettled", "Settled (Paid/Returned)", "Overdue"
        });
        this.filterStatusBox.setFont(FONT_INPUT);

        JLabel sortLbl = new JLabel("Sort:");
        sortLbl.setFont(FONT_TOOLBAR_LABEL);
        this.sortBox = new JComboBox<String>(new String[]{
            "Due Date (Soonest first)",
            "Amount (Highest to Lowest)",
            "Person's Name (A-Z)"
        });
        this.sortBox.setFont(FONT_INPUT);

        // Attach listeners for instant filtering and sorting
        this.searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { renderDashboardTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { renderDashboardTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { renderDashboardTable(); }
        });

        ActionListener filterListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderDashboardTable();
            }
        };
        this.filterDirectionBox.addActionListener(filterListener);
        this.filterCategoryBox.addActionListener(filterListener);
        this.filterStatusBox.addActionListener(filterListener);
        this.sortBox.addActionListener(filterListener);

        toolbar.add(searchLbl);
        toolbar.add(this.searchField);
        toolbar.add(dirLbl);
        toolbar.add(this.filterDirectionBox);
        toolbar.add(catLbl);
        toolbar.add(this.filterCategoryBox);
        toolbar.add(statusLbl);
        toolbar.add(this.filterStatusBox);
        toolbar.add(sortLbl);
        toolbar.add(this.sortBox);
        return toolbar;
    }

    // Creates the center JTable inside a JScrollPane with custom cell rendering
    private JScrollPane createTablePanel() {
        String[] columns = { "Direction", "Person", "Type", "Detail / Value", "Description", "Due Date", "Status" };
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.iouTable = new JTable(this.tableModel);
        this.iouTable.setFont(FONT_TABLE_CELL);
        this.iouTable.setRowHeight(32);
        this.iouTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.iouTable.setSelectionBackground(COLOR_SELECTION_BG);
        this.iouTable.setSelectionForeground(Color.BLACK);
        this.iouTable.setGridColor(new Color(0xE0, 0xE0, 0xE0));
        this.iouTable.getTableHeader().setFont(FONT_TABLE_HEADER);
        this.iouTable.getTableHeader().setReorderingAllowed(false);

        // Set preferred column widths
        this.iouTable.getColumnModel().getColumn(0).setPreferredWidth(105);
        this.iouTable.getColumnModel().getColumn(0).setMaxWidth(120);
        this.iouTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        this.iouTable.getColumnModel().getColumn(2).setPreferredWidth(75);
        this.iouTable.getColumnModel().getColumn(2).setMaxWidth(90);
        this.iouTable.getColumnModel().getColumn(3).setPreferredWidth(210);
        this.iouTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        this.iouTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        this.iouTable.getColumnModel().getColumn(6).setPreferredWidth(110);

        // Custom cell renderer for Direction badges, Detail/Value formatting, wrapping, Overdue dates, and Status colors
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );
                lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
                lbl.setFont(FONT_TABLE_CELL);

                if (!isSelected) {
                    lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xFA, 0xFB, 0xFC));
                    lbl.setForeground(new Color(0x1C, 0x28, 0x33));
                }

                String text = (value == null) ? "" : value.toString();
                int colWidth = table.getColumnModel().getColumn(column).getWidth();
                if (colWidth <= 0) {
                    colWidth = table.getColumnModel().getColumn(column).getPreferredWidth();
                }
                int wrapWidth = Math.max(50, (int) ((colWidth - 20) * 0.75));

                if (column == 0) {
                    lbl.setFont(FONT_BADGE);
                    if ("THEY OWE".equals(text)) {
                        lbl.setForeground(COLOR_GREEN_TEXT);
                        if (!isSelected) lbl.setBackground(COLOR_GREEN_BG);
                    } else if ("I OWE".equals(text)) {
                        lbl.setForeground(COLOR_RED_TEXT);
                        if (!isSelected) lbl.setBackground(COLOR_RED_BG);
                    }
                } else if (column == 1) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    lbl.setText("<html><div style='width:" + wrapWidth + "px;'>" + text + "</div></html>");
                } else if (column == 3) {
                    String styledText = text;
                    if (text.contains(" [")) {
                        int idx = text.lastIndexOf(" [");
                        String itemName = text.substring(0, idx);
                        String rest = text.substring(idx);
                        styledText = "<b>" + itemName + "</b><font color='#757575'>" + rest + "</font>";
                    } else if (text.contains(" (Paid ")) {
                        int idx = text.indexOf(" (Paid ");
                        String cashAmt = text.substring(0, idx);
                        String paidPart = text.substring(idx + 2, text.length() - 1);
                        styledText = "<b>" + cashAmt + "</b> <font color='#757575'>(</font><font color='#2E7D32'>"
                                + paidPart + "</font><font color='#757575'>)</font>";
                    } else if (text.contains(" (of ")) {
                        int idx = text.indexOf(" (of ");
                        String cashAmt = text.substring(0, idx);
                        String rest = text.substring(idx);
                        styledText = "<b>" + cashAmt + "</b><font color='#757575'>" + rest + "</font>";
                    } else {
                        styledText = "<b>" + text + "</b>";
                    }
                    lbl.setText("<html><div style='width:" + wrapWidth + "px;'>" + styledText + "</div></html>");
                } else if (column == 4) {
                    lbl.setText("<html><div style='width:" + wrapWidth + "px;'>" + text + "</div></html>");
                } else if (column == 5 && text.contains("OVERDUE")) {
                    lbl.setForeground(COLOR_RED_BORDER);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if (column == 6) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if ("Pending".equals(text)) {
                        lbl.setForeground(new Color(0xE6, 0x51, 0x00));
                    } else if ("Partially Paid".equals(text)) {
                        lbl.setForeground(COLOR_BLUE_BORDER);
                    } else if ("Settled".equals(text)) {
                        lbl.setForeground(COLOR_GREEN_BORDER);
                    }
                }

                if (column == 1 || column == 3 || column == 4) {
                    int prefHeight = lbl.getPreferredSize().height + 8;
                    if (table.getRowHeight(row) < prefHeight) {
                        table.setRowHeight(row, prefHeight);
                    }
                }

                return lbl;
            }
        };

        for (int c = 0; c < this.iouTable.getColumnCount(); c++) {
            this.iouTable.getColumnModel().getColumn(c).setCellRenderer(customRenderer);
        }

        // Row selection listener
        this.iouTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int viewRow = iouTable.getSelectedRow();
                    if (viewRow >= 0 && viewRow < displayedRecords.size()) {
                        selectedRecordId = displayedRecords.get(viewRow).getId();
                    } else {
                        selectedRecordId = -1;
                    }
                    updateBottomActionButtons();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(this.iouTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_DARK, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    // Creates the bottom action bar with selection hint and 4 action buttons
    private JPanel createBottomActionBar() {
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setBackground(COLOR_PANEL_WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));

        this.selectedRowLabel = new JLabel("Click any row in the table above to enable record actions.");
        this.selectedRowLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.selectedRowLabel.setForeground(new Color(0x54, 0x6E, 0x7A));
        bottomBar.add(this.selectedRowLabel, BorderLayout.WEST);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        this.btnRecordPayment = new JButton("Record Payment / Return");
        this.btnRecordPayment.setFont(FONT_BUTTON);
        this.btnRecordPayment.setBackground(COLOR_GREEN_BORDER);
        this.btnRecordPayment.setForeground(Color.WHITE);
        this.btnRecordPayment.setFocusPainted(false);

        this.btnEditRecord = new JButton("Edit Record");
        this.btnEditRecord.setFont(FONT_BUTTON);
        this.btnEditRecord.setBackground(COLOR_BLUE_BORDER);
        this.btnEditRecord.setForeground(Color.WHITE);
        this.btnEditRecord.setFocusPainted(false);

        this.btnDeleteRecord = new JButton("Delete Record");
        this.btnDeleteRecord.setFont(FONT_BUTTON);
        this.btnDeleteRecord.setBackground(COLOR_RED_BORDER);
        this.btnDeleteRecord.setForeground(Color.WHITE);
        this.btnDeleteRecord.setFocusPainted(false);

        this.btnViewStatement = new JButton("View Statement");
        this.btnViewStatement.setFont(FONT_BUTTON);
        this.btnViewStatement.setBackground(new Color(0xE0, 0xE6, 0xEA));
        this.btnViewStatement.setForeground(new Color(0x1C, 0x28, 0x33));
        this.btnViewStatement.setFocusPainted(false);

        this.btnRecordPayment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRecordPaymentOrReturn();
            }
        });

        this.btnEditRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditSelected();
            }
        });

        this.btnDeleteRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteSelected();
            }
        });

        this.btnViewStatement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleJumpToPersonReport();
            }
        });

        btnGroup.add(this.btnRecordPayment);
        btnGroup.add(this.btnEditRecord);
        btnGroup.add(this.btnDeleteRecord);
        btnGroup.add(this.btnViewStatement);

        bottomBar.add(btnGroup, BorderLayout.EAST);
        updateBottomActionButtons();
        return bottomBar;
    }

    // Refreshes both the summary boxes and the dashboard table
    public void refreshDashboard() {
        updateSummaryBoxes();
        renderDashboardTable();
    }

    // Calculates totals (using recursion for cash totals) and updates the 3 summary boxes
    public void updateSummaryBoxes() {
        double owedToMeCash = this.manager.calculateTotalCashRecursive(0, "THEY_OWE");
        int owedToMeItems = this.manager.countActiveItemsByDirection("THEY_OWE");

        double iOweCash = this.manager.calculateTotalCashRecursive(0, "I_OWE");
        int iOweItems = this.manager.countActiveItemsByDirection("I_OWE");

        double netCash = owedToMeCash - iOweCash;

        this.sumOwedToMeCashLbl.setText(IOUManager.formatPeso(owedToMeCash));
        this.sumOwedToMeItemsLbl.setText("Borrowed from me: " + owedToMeItems + (owedToMeItems == 1 ? " item" : " items"));

        this.sumIOweCashLbl.setText(IOUManager.formatPeso(iOweCash));
        this.sumIOweItemsLbl.setText("Items to return: " + iOweItems + (iOweItems == 1 ? " item" : " items"));

        String sign = (netCash > 0) ? "+" : (netCash < 0 ? "-" : "");
        this.sumNetCashLbl.setText(sign + IOUManager.formatPeso(Math.abs(netCash)));
        if (netCash > 0) {
            this.sumNetStatusLbl.setText("Overall in the Positive");
        } else if (netCash < 0) {
            this.sumNetStatusLbl.setText("Overall in the Negative");
        } else {
            this.sumNetStatusLbl.setText("All Cash Balances Even");
        }
    }

    // Filters/sorts records and populates the JTable rows
    public void renderDashboardTable() {
        String searchText = this.searchField.getText();

        String dirFilter = "ALL";
        if (this.filterDirectionBox.getSelectedIndex() == 1) dirFilter = "THEY_OWE";
        else if (this.filterDirectionBox.getSelectedIndex() == 2) dirFilter = "I_OWE";

        String catFilter = "ALL";
        if (this.filterCategoryBox.getSelectedIndex() == 1) catFilter = "Cash";
        else if (this.filterCategoryBox.getSelectedIndex() == 2) catFilter = "Item";

        String statusFilter = "ALL";
        if (this.filterStatusBox.getSelectedIndex() == 1) statusFilter = "UNSETTLED";
        else if (this.filterStatusBox.getSelectedIndex() == 2) statusFilter = "SETTLED";
        else if (this.filterStatusBox.getSelectedIndex() == 3) statusFilter = "OVERDUE";

        String sortBy = "DUE_ASC";
        if (this.sortBox.getSelectedIndex() == 1) sortBy = "AMOUNT_DESC";
        else if (this.sortBox.getSelectedIndex() == 2) sortBy = "NAME_ASC";

        int previouslySelectedId = this.selectedRecordId;
        this.displayedRecords = this.manager.searchFilterAndSort(
            searchText, dirFilter, catFilter, statusFilter, sortBy
        );

        this.tableModel.setRowCount(0);
        this.iouTable.setRowHeight(32);
        int rowToSelect = -1;

        for (int i = 0; i < this.displayedRecords.size(); i++) {
            IOURecord rec = this.displayedRecords.get(i);
            String dirBadge = "THEY_OWE".equals(rec.getDirection()) ? "THEY OWE" : "I OWE";
            String dueText = rec.isOverdue(this.manager.getTodayDate())
                ? rec.getDueDate() + " (OVERDUE!)"
                : rec.getDueDate();

            Object[] rowData = {
                dirBadge,
                rec.getPerson(),
                rec.getCategoryName(),
                rec.getSummaryDetails(),
                rec.getReason(),
                dueText,
                rec.getStatus()
            };
            this.tableModel.addRow(rowData);

            if (rec.getId() == previouslySelectedId) {
                rowToSelect = i;
            }
        }

        if (rowToSelect >= 0) {
            this.iouTable.setRowSelectionInterval(rowToSelect, rowToSelect);
            this.selectedRecordId = previouslySelectedId;
        } else {
            this.selectedRecordId = -1;
        }
        updateBottomActionButtons();
    }

    // Enables or disables the bottom action buttons based on the currently selected row
    private void updateBottomActionButtons() {
        IOURecord rec = this.manager.findRecordById(this.selectedRecordId);
        boolean hasSelection = (rec != null);

        this.btnRecordPayment.setEnabled(hasSelection && !"Settled".equals(rec.getStatus()));
        this.btnEditRecord.setEnabled(hasSelection);
        this.btnDeleteRecord.setEnabled(hasSelection);
        this.btnViewStatement.setEnabled(hasSelection);

        if (!hasSelection) {
            this.selectedRowLabel.setText("Click any row in the table above to enable record actions.");
        } else {
            String desc = (rec instanceof CashIOU)
                ? IOUManager.formatPeso(((CashIOU) rec).getRemainingBalance())
                : ((ItemIOU) rec).getItemName();
            this.selectedRowLabel.setText("Selected: " + rec.getPerson()
                + " (" + rec.getCategoryName() + ": " + desc + ") \u2022 Status: " + rec.getStatus());
        }
    }

    // Handles recording a cash payment or marking a borrowed item as returned
    private void handleRecordPaymentOrReturn() {
        IOURecord rec = this.manager.findRecordById(this.selectedRecordId);
        if (rec == null || "Settled".equals(rec.getStatus())) {
            return;
        }

        if (rec instanceof CashIOU) {
            CashIOU cashRec = (CashIOU) rec;
            String promptQuestion = "THEY_OWE".equals(cashRec.getDirection())
                ? "How much did " + cashRec.getPerson() + " pay today?\n(Remaining Balance: "
                    + IOUManager.formatPeso(cashRec.getRemainingBalance()) + ")"
                : "How much did you pay " + cashRec.getPerson() + " today?\n(Remaining Balance: "
                    + IOUManager.formatPeso(cashRec.getRemainingBalance()) + ")";

            String defaultVal = String.format("%.2f", cashRec.getRemainingBalance());
            String input = (String) JOptionPane.showInputDialog(
                this,
                promptQuestion,
                "Record Cash Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                defaultVal
            );

            if (input == null) {
                return;
            }

            String trimmed = input.trim();
            if (!trimmed.matches("^\\d+(\\.\\d{1,2})?$")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid Amount: Please enter a positive number using digits only.",
                    "Invalid Payment Amount",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                double paidNow = Double.parseDouble(trimmed);
                cashRec.recordPayment(paidNow);
                this.mainFrame.triggerStorageSyncIndicator();
                refreshDashboard();
            } catch (InvalidAmountException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } else if (rec instanceof ItemIOU) {
            ItemIOU itemRec = (ItemIOU) rec;
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Mark borrowed item \"" + itemRec.getItemName() + "\" with "
                    + itemRec.getPerson() + " as Returned (Settled)?",
                "Record Item Return",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                itemRec.markAsSettled();
                this.mainFrame.triggerStorageSyncIndicator();
                refreshDashboard();
            }
        }
    }

    // Opens the selected record in the Add/Edit Form screen
    private void handleEditSelected() {
        IOURecord rec = this.manager.findRecordById(this.selectedRecordId);
        if (rec != null) {
            this.mainFrame.openEditForm(rec);
        }
    }

    // Confirms and deletes the selected IOU record
    private void handleDeleteSelected() {
        IOURecord rec = this.manager.findRecordById(this.selectedRecordId);
        if (rec == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this IOU?",
            "Delete Record",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            this.manager.deleteRecordById(rec.getId());
            this.selectedRecordId = -1;
            this.mainFrame.triggerStorageSyncIndicator();
            refreshDashboard();
        }
    }

    // Jumps to the Reports screen for the selected record's contact person
    private void handleJumpToPersonReport() {
        IOURecord rec = this.manager.findRecordById(this.selectedRecordId);
        if (rec != null) {
            this.mainFrame.jumpToPersonReport(rec.getPerson());
        }
    }
}
