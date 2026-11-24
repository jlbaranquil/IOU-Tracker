package ioutracker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Screen 2: The Add / Edit IOU Form Panel.
 * Provides a 4-step form with dynamic Cash/Item sub-panels and input validation.
 */
public class FormPanel extends JPanel {


    public static final Font FONT_SCREEN_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LEGEND       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL        = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_INPUT        = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 13);

    public static final Color COLOR_BG_MAIN      = new Color(0xEC, 0xEF, 0xF1);
    public static final Color COLOR_PRIMARY_BLUE = new Color(0x15, 0x65, 0xC0);
    public static final Color COLOR_BORDER_DARK  = new Color(0x78, 0x90, 0x9C);
    public static final Color COLOR_ERROR_BORDER = new Color(0xC6, 0x28, 0x28);
    public static final Color COLOR_ERROR_BG     = new Color(0xFF, 0xF8, 0xF8);


    private IOUManager manager;
    private MainFrame mainFrame;

    private JLabel formTitleLabel;
    private JRadioButton radioTheyOwe;
    private JRadioButton radioIOwe;
    private JPanel radioWrapTheyOwe;
    private JPanel radioWrapIOwe;

    private JTextField personNameField;
    private JComboBox<String> existingContactsBox;

    private JButton toggleBtnCash;
    private JButton toggleBtnItem;
    private CardLayout subFormCardLayout;
    private JPanel subFormContainer;

    private JTextField cashAmountField;
    private JTextField itemNameField;
    private JComboBox<String> itemConditionBox;
    private JTextField itemEstValueField;

    private JTextField dateCreatedField;
    private JTextField dueDateField;
    private JTextField reasonField;

    private Integer editingRecordId = null;
    private String currentDirection = "THEY_OWE";
    private String currentCategory = "Cash";

    private Border normalFieldBorder;
    private Border errorFieldBorder;

    // Constructs the FormPanel with references to the manager and mainFrame
    public FormPanel(IOUManager manager, MainFrame mainFrame) {
        this.manager = manager;
        this.mainFrame = mainFrame;
        this.normalFieldBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x90, 0xA4, 0xAE), 1),
            new EmptyBorder(4, 8, 4, 8)
        );
        this.errorFieldBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ERROR_BORDER, 2),
            new EmptyBorder(3, 7, 3, 7)
        );
        setLayout(new BorderLayout(0, 12));
        setBackground(COLOR_BG_MAIN);
        initComponents();
    }

    // Builds the header and the 4 step fieldsets inside a scroll pane
    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xB0, 0xBE, 0xC5)),
            new EmptyBorder(0, 0, 6, 0)
        ));
        this.formTitleLabel = new JLabel("Add New IOU Record");
        this.formTitleLabel.setFont(FONT_SCREEN_TITLE);
        this.formTitleLabel.setForeground(new Color(0x26, 0x32, 0x38));
        headerPanel.add(this.formTitleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(COLOR_BG_MAIN);

		formContent.add(createStep1Panel());
        formContent.add(Box.createVerticalStrut(12));
        formContent.add(createStep2Panel());
        formContent.add(Box.createVerticalStrut(12));
        formContent.add(createStep3Panel());
        formContent.add(Box.createVerticalStrut(12));
        formContent.add(createStep4Panel());

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(COLOR_BG_MAIN);
        scrollWrapper.add(formContent, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollWrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_BG_MAIN);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Creates Step 1 fieldset ("1. Who is this with?")
    private JPanel createStep1Panel() {
        JPanel panel = createFieldsetPanel("Who is this with?");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Direction
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel dirLbl = new JLabel("Direction:");
        dirLbl.setFont(FONT_LABEL);
        dirLbl.setPreferredSize(new Dimension(190, 28));
        panel.add(dirLbl, gbc);

		gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        JPanel radioGroupPanel = new JPanel();
        radioGroupPanel.setLayout(new BoxLayout(radioGroupPanel, BoxLayout.X_AXIS));
        radioGroupPanel.setOpaque(false);

        this.radioTheyOwe = new JRadioButton("Someone owes me");
        this.radioTheyOwe.setFont(FONT_LABEL);
        this.radioTheyOwe.setOpaque(false);
        this.radioTheyOwe.setFocusPainted(false);

        this.radioIOwe = new JRadioButton("I owe someone");
        this.radioIOwe.setFont(FONT_LABEL);
        this.radioIOwe.setOpaque(false);
        this.radioIOwe.setFocusPainted(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(this.radioTheyOwe);
        bg.add(this.radioIOwe);

		this.radioWrapTheyOwe = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        this.radioWrapTheyOwe.add(this.radioTheyOwe);

        this.radioWrapIOwe = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        this.radioWrapIOwe.add(this.radioIOwe);

        this.radioTheyOwe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFormDirection("THEY_OWE");
            }
        });
        this.radioIOwe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFormDirection("I_OWE");
            }
        });

		radioGroupPanel.add(this.radioWrapTheyOwe);
        radioGroupPanel.add(Box.createHorizontalStrut(10));
        radioGroupPanel.add(this.radioWrapIOwe);
        panel.add(radioGroupPanel, gbc);

        // Row 1: Person's Name + Existing Contact Dropdown
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel nameLbl = new JLabel("Person's Name:");
        nameLbl.setFont(FONT_LABEL);
        nameLbl.setPreferredSize(new Dimension(190, 28));
        panel.add(nameLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        JPanel personInputRow = new JPanel(new BorderLayout(8, 0));
        personInputRow.setOpaque(false);

        this.personNameField = createStyledTextField();
        this.existingContactsBox = new JComboBox<String>();
        this.existingContactsBox.setFont(FONT_INPUT);
        this.existingContactsBox.setPreferredSize(new Dimension(220, 30));
        this.existingContactsBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = existingContactsBox.getSelectedIndex();
                if (idx > 0) {
                    String picked = (String) existingContactsBox.getSelectedItem();
                    personNameField.setText(picked);
                    personNameField.setBorder(normalFieldBorder);
                    personNameField.setBackground(Color.WHITE);
                }
            }
        });

        personInputRow.add(this.personNameField, BorderLayout.CENTER);
        personInputRow.add(this.existingContactsBox, BorderLayout.EAST);
        panel.add(personInputRow, gbc);

        return panel;
    }

    // Creates Step 2 fieldset ("2. What kind of IOU is it?") with dynamic Cash/Item toggle
    private JPanel createStep2Panel() {
		JPanel panel = createFieldsetPanel("What kind of IOU is it?");
        panel.setLayout(new BorderLayout(0, 0));

        // Top Toggle Row
        JPanel toggleRow = new JPanel(new GridBagLayout());
        toggleRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLbl = new JLabel("Type:");
        typeLbl.setFont(FONT_LABEL);
        typeLbl.setPreferredSize(new Dimension(190, 28));
        toggleRow.add(typeLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        JPanel toggleBtnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleBtnBar.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_DARK, 1));

        this.toggleBtnCash = new JButton("Cash Money");
        this.toggleBtnCash.setFont(FONT_LABEL);
        this.toggleBtnCash.setFocusPainted(false);
        this.toggleBtnCash.setBorder(new EmptyBorder(7, 18, 7, 18));

        this.toggleBtnItem = new JButton("Physical Item");
        this.toggleBtnItem.setFont(FONT_LABEL);
        this.toggleBtnItem.setFocusPainted(false);
        this.toggleBtnItem.setBorder(new EmptyBorder(7, 18, 7, 18));

        this.toggleBtnCash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFormCategory("Cash");
            }
        });
        this.toggleBtnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFormCategory("Item");
            }
        });

        toggleBtnBar.add(this.toggleBtnCash);
        toggleBtnBar.add(this.toggleBtnItem);
        toggleRow.add(toggleBtnBar, gbc);
        panel.add(toggleRow, BorderLayout.NORTH);

		// Dynamic Sub-Panels Container (CardLayout)
        this.subFormCardLayout = new CardLayout();
        this.subFormContainer = new JPanel(this.subFormCardLayout) {
            @Override
            public Dimension getPreferredSize() {
                for (Component comp : getComponents()) {
                    if (comp.isVisible()) {
                        return comp.getPreferredSize();
                    }
                }
                return super.getPreferredSize();
            }
        };
        this.subFormContainer.setOpaque(false);

        // Sub-Panel A: Cash Money
        JPanel cashSubPanel = new JPanel(new GridBagLayout());
        cashSubPanel.setOpaque(false);
        GridBagConstraints cGbc = new GridBagConstraints();
        cGbc.insets = new Insets(6, 6, 6, 12);
        cGbc.anchor = GridBagConstraints.WEST;
        cGbc.fill = GridBagConstraints.HORIZONTAL;

        cGbc.gridx = 0; cGbc.gridy = 0; cGbc.weightx = 0;
        JLabel amtLbl = new JLabel("Amount (\u20B1):");
        amtLbl.setFont(FONT_LABEL);
        amtLbl.setPreferredSize(new Dimension(190, 28));
        cashSubPanel.add(amtLbl, cGbc);

        cGbc.gridx = 1; cGbc.gridy = 0; cGbc.weightx = 1.0;
        this.cashAmountField = createStyledTextField();
        cashSubPanel.add(this.cashAmountField, cGbc);

        // Sub-Panel B: Physical Item
        JPanel itemSubPanel = new JPanel(new GridBagLayout());
        itemSubPanel.setOpaque(false);
        GridBagConstraints iGbc = new GridBagConstraints();
        iGbc.insets = new Insets(6, 6, 6, 12);
        iGbc.anchor = GridBagConstraints.WEST;
        iGbc.fill = GridBagConstraints.HORIZONTAL;

        iGbc.gridx = 0; iGbc.gridy = 0; iGbc.weightx = 0;
        JLabel itemNameLbl = new JLabel("Item Name:");
        itemNameLbl.setFont(FONT_LABEL);
        itemNameLbl.setPreferredSize(new Dimension(190, 28));
        itemSubPanel.add(itemNameLbl, iGbc);

        iGbc.gridx = 1; iGbc.gridy = 0; iGbc.weightx = 1.0;
        this.itemNameField = createStyledTextField();
        itemSubPanel.add(this.itemNameField, iGbc);

        iGbc.gridx = 0; iGbc.gridy = 1; iGbc.weightx = 0;
        JLabel condLbl = new JLabel("Item Condition:");
        condLbl.setFont(FONT_LABEL);
        condLbl.setPreferredSize(new Dimension(190, 28));
        itemSubPanel.add(condLbl, iGbc);

        iGbc.gridx = 1; iGbc.gridy = 1; iGbc.weightx = 1.0;
        this.itemConditionBox = new JComboBox<String>(new String[]{
            "Brand New", "Good", "Slightly Worn"
        });
        this.itemConditionBox.setFont(FONT_INPUT);
        itemSubPanel.add(this.itemConditionBox, iGbc);

        iGbc.gridx = 0; iGbc.gridy = 2; iGbc.weightx = 0;
        JLabel estLbl = new JLabel("Estimated Value (\u20B1):");
        estLbl.setFont(FONT_LABEL);
        estLbl.setPreferredSize(new Dimension(190, 28));
        itemSubPanel.add(estLbl, iGbc);

        iGbc.gridx = 1; iGbc.gridy = 2; iGbc.weightx = 1.0;
        this.itemEstValueField = createStyledTextField();
        itemSubPanel.add(this.itemEstValueField, iGbc);

        this.subFormContainer.add(cashSubPanel, "Cash");
        this.subFormContainer.add(itemSubPanel, "Item");

        panel.add(this.subFormContainer, BorderLayout.CENTER);
        return panel;
    }

    // Creates Step 3 fieldset ("Dates and Notes")
    private JPanel createStep3Panel() {
        JPanel panel = createFieldsetPanel("Dates and Notes");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel createdLbl = new JLabel("Date Created (YYYY-MM-DD):");
        createdLbl.setFont(FONT_LABEL);
        createdLbl.setPreferredSize(new Dimension(190, 28));
        panel.add(createdLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        this.dateCreatedField = createStyledTextField();
        panel.add(this.dateCreatedField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel dueLbl = new JLabel("Due Date (YYYY-MM-DD):");
        dueLbl.setFont(FONT_LABEL);
        dueLbl.setPreferredSize(new Dimension(190, 28));
        panel.add(dueLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        this.dueDateField = createStyledTextField();
        panel.add(this.dueDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel reasonLbl = new JLabel("Reason / Description:");
        reasonLbl.setFont(FONT_LABEL);
        reasonLbl.setPreferredSize(new Dimension(190, 28));
        panel.add(reasonLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        this.reasonField = createStyledTextField();
        panel.add(this.reasonField, gbc);

        return panel;
    }

    // Creates Step 4 fieldset ("4. Save or Cancel")
    private JPanel createStep4Panel() {
        JPanel panel = createFieldsetPanel("Save or Cancel");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));

        JButton btnSave = new JButton("Save IOU");
        btnSave.setFont(FONT_BUTTON);
        btnSave.setBackground(COLOR_PRIMARY_BLUE);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(130, 34));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(FONT_BUTTON);
        btnCancel.setBackground(new Color(0xE0, 0xE6, 0xEA));
        btnCancel.setForeground(new Color(0x1C, 0x28, 0x33));
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(110, 34));

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveIOU();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.switchScreen("dashboard");
            }
        });

        panel.add(btnSave);
        panel.add(btnCancel);
        return panel;
    }

    // Helper method to create a titled fieldset panel
    private JPanel createFieldsetPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDER_DARK, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FONT_LEGEND,
            COLOR_PRIMARY_BLUE
        );
        panel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            new EmptyBorder(8, 12, 10, 12)
        ));
        return panel;
    }

    // Helper method to create a styled JTextField that clears its red error state on typing
    private JTextField createStyledTextField() {
        final JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setBorder(this.normalFieldBorder);
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                field.setBorder(normalFieldBorder);
                field.setBackground(Color.WHITE);
            }
        });
        return field;
    }

    // Resets the form fields for creating a brand-new IOU record
    public void prepareForAdd() {
        this.editingRecordId = null;
        this.formTitleLabel.setText("Add New IOU Record");
        clearAllFieldErrors();

        setFormDirection("THEY_OWE");
        this.personNameField.setText("");
        populateExistingContactsDropdown();

        setFormCategory("Cash");
        this.cashAmountField.setText("");
        this.itemNameField.setText("");
        this.itemConditionBox.setSelectedItem("Good");
        this.itemEstValueField.setText("");

        this.dateCreatedField.setText(this.manager.getTodayDate());
        this.dueDateField.setText("");
        this.reasonField.setText("");
    }

    // Populates the form fields with an existing IOURecord's data for editing
    public void prepareForEdit(IOURecord record) {
        if (record == null) {
            prepareForAdd();
            return;
        }
        this.editingRecordId = record.getId();
        this.formTitleLabel.setText("Edit IOU Record \u2022 " + record.getPerson());
        clearAllFieldErrors();

        setFormDirection(record.getDirection());
        populateExistingContactsDropdown();
        this.personNameField.setText(record.getPerson());

        setFormCategory(record.getCategoryName());
        if (record instanceof CashIOU) {
            CashIOU cashRec = (CashIOU) record;
            double amtToShow = "Settled".equals(cashRec.getStatus())
                ? cashRec.getOriginalAmount()
                : cashRec.getRemainingBalance();
            this.cashAmountField.setText(String.format("%.2f", amtToShow));
            this.itemNameField.setText("");
            this.itemConditionBox.setSelectedItem("Good");
            this.itemEstValueField.setText("");
        } else if (record instanceof ItemIOU) {
            ItemIOU itemRec = (ItemIOU) record;
            this.itemNameField.setText(itemRec.getItemName());
            this.itemConditionBox.setSelectedItem(itemRec.getItemCondition());
            this.itemEstValueField.setText(String.format("%.2f", itemRec.getEstimatedValue()));
            this.cashAmountField.setText("");
        }

        this.dateCreatedField.setText(record.getDateCreated());
        this.dueDateField.setText(record.getDueDate());
        this.reasonField.setText(record.getReason());
    }

    // Populates the "-- Pick Existing Contact --" dropdown from the manager's unique contacts Set
    public void populateExistingContactsDropdown() {
        this.existingContactsBox.removeAllItems();
        this.existingContactsBox.addItem("-- Pick Existing Contact --");
        List<String> contacts = this.manager.getAllUniqueContacts();
        for (int i = 0; i < contacts.size(); i++) {
            this.existingContactsBox.addItem(contacts.get(i));
        }
    }

    // Updates the selected direction ("THEY_OWE" or "I_OWE") and radio button styling
    private void setFormDirection(String direction) {
        this.currentDirection = direction;
        boolean theyOwe = "THEY_OWE".equals(direction);
        this.radioTheyOwe.setSelected(theyOwe);
        this.radioIOwe.setSelected(!theyOwe);

        if (theyOwe) {
            this.radioWrapTheyOwe.setBackground(DashboardPanel.COLOR_GREEN_BG);
            this.radioWrapTheyOwe.setBorder(BorderFactory.createLineBorder(DashboardPanel.COLOR_GREEN_BORDER, 1));
            this.radioTheyOwe.setForeground(DashboardPanel.COLOR_GREEN_TEXT);

            this.radioWrapIOwe.setBackground(new Color(0xF5, 0xF7, 0xF8));
            this.radioWrapIOwe.setBorder(BorderFactory.createLineBorder(DashboardPanel.COLOR_BORDER, 1));
            this.radioIOwe.setForeground(new Color(0x1C, 0x28, 0x33));
        } else {
            this.radioWrapIOwe.setBackground(DashboardPanel.COLOR_RED_BG);
            this.radioWrapIOwe.setBorder(BorderFactory.createLineBorder(DashboardPanel.COLOR_RED_BORDER, 1));
            this.radioIOwe.setForeground(DashboardPanel.COLOR_RED_TEXT);

            this.radioWrapTheyOwe.setBackground(new Color(0xF5, 0xF7, 0xF8));
            this.radioWrapTheyOwe.setBorder(BorderFactory.createLineBorder(DashboardPanel.COLOR_BORDER, 1));
            this.radioTheyOwe.setForeground(new Color(0x1C, 0x28, 0x33));
        }
    }

    // Switches the dynamic sub-panel between "Cash" and "Item"
    private void setFormCategory(String category) {
        this.currentCategory = category;
        boolean isCash = "Cash".equals(category);

        if (isCash) {
            this.toggleBtnCash.setBackground(COLOR_PRIMARY_BLUE);
            this.toggleBtnCash.setForeground(Color.WHITE);
            this.toggleBtnItem.setBackground(COLOR_BG_MAIN);
            this.toggleBtnItem.setForeground(new Color(0x37, 0x47, 0x4F));
            this.subFormCardLayout.show(this.subFormContainer, "Cash");
        } else {
            this.toggleBtnItem.setBackground(COLOR_PRIMARY_BLUE);
            this.toggleBtnItem.setForeground(Color.WHITE);
            this.toggleBtnCash.setBackground(COLOR_BG_MAIN);
            this.toggleBtnCash.setForeground(new Color(0x37, 0x47, 0x4F));
            this.subFormCardLayout.show(this.subFormContainer, "Item");
        }
		
		this.subFormContainer.revalidate();
        this.subFormContainer.repaint();
    }

    // Resets all text fields to their normal border and background color
    private void clearAllFieldErrors() {
        JTextField[] fields = {
            this.personNameField, this.cashAmountField, this.itemNameField,
            this.itemEstValueField, this.dateCreatedField, this.dueDateField, this.reasonField
        };
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                fields[i].setBorder(this.normalFieldBorder);
                fields[i].setBackground(Color.WHITE);
            }
        }
    }

    // Highlights a specific text field in red and displays an error JOptionPane
    private void markFieldError(JTextField field, String title, String message) {
        if (field != null) {
            field.setBorder(this.errorFieldBorder);
            field.setBackground(COLOR_ERROR_BG);
            field.requestFocusInWindow();
        }
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Validates form inputs, catches custom exceptions, saves or updates the IOU, and returns to Dashboard
    private void handleSaveIOU() {
        clearAllFieldErrors();

        String personName = this.personNameField.getText().trim();
        String dateCreated = this.dateCreatedField.getText().trim();
        String dueDate = this.dueDateField.getText().trim();
        String reason = this.reasonField.getText().trim();

        if (personName.isEmpty()) {
            markFieldError(
                this.personNameField,
                "Validation Error: Missing Name",
                "Person's Name cannot be blank. Please select an existing contact or type a name."
            );
            return;
        }

        double cashAmount = 0.0;
        String itemName = "";
        String itemCondition = "Good";
        double itemEstValue = 0.0;

        if ("Cash".equals(this.currentCategory)) {
            String rawAmt = this.cashAmountField.getText().trim();
            if (rawAmt.isEmpty() || !rawAmt.matches("^\\d+(\\.\\d{1,2})?$")) {
                markFieldError(
                    this.cashAmountField,
                    "InvalidAmountException",
                    "Invalid Amount: Please enter a positive number using digits only."
                );
                return;
            }
            cashAmount = Double.parseDouble(rawAmt);
        } else {
            itemName = this.itemNameField.getText().trim();
            if (itemName.isEmpty()) {
                markFieldError(
                    this.itemNameField,
                    "Validation Error: Missing Item Name",
                    "Please enter the name of the borrowed physical item."
                );
                return;
            }
            itemCondition = (String) this.itemConditionBox.getSelectedItem();
            String rawEst = this.itemEstValueField.getText().trim();
            if (rawEst.isEmpty() || !rawEst.matches("^\\d+(\\.\\d{1,2})?$")) {
                markFieldError(
                    this.itemEstValueField,
                    "InvalidAmountException",
                    "Invalid Estimated Value: Please enter zero or a positive number using digits only."
                );
                return;
            }
            itemEstValue = Double.parseDouble(rawEst);
        }

        // Validate Date Created
        try {
            IOURecord.validateDateString(dateCreated, "Date Created");
        } catch (InvalidDateFormatException ex) {
            markFieldError(this.dateCreatedField, "InvalidDateFormatException", ex.getMessage());
            return;
        }

        // Validate Due Date
        try {
            IOURecord.validateDateString(dueDate, "Due Date");
        } catch (InvalidDateFormatException ex) {
            markFieldError(this.dueDateField, "InvalidDateFormatException", ex.getMessage());
            return;
        }

        // Validate Reason
        if (reason.isEmpty()) {
            markFieldError(
                this.reasonField,
                "Validation Error: Missing Reason",
                "Please enter a short Reason / Description for this IOU."
            );
            return;
        }

        // Create or Update Record
        try {
            if (this.editingRecordId != null) {
                IOURecord existing = this.manager.findRecordById(this.editingRecordId);
                if (existing != null) {
                    int id = existing.getId();
                    String status = existing.getStatus();
                    int indexInList = this.manager.getAllRecords().indexOf(existing);

                    IOURecord updatedRec;
                    if ("Cash".equals(this.currentCategory)) {
                        double origAmt = cashAmount;
                        if (existing instanceof CashIOU) {
                            origAmt = Math.max(((CashIOU) existing).getOriginalAmount(), cashAmount);
                        }
                        if (cashAmount > 0 && "Settled".equals(status)) {
                            status = "Pending";
                        }
                        updatedRec = new CashIOU(
                            id, this.currentDirection, personName,
                            origAmt, cashAmount, dateCreated, dueDate, reason, status
                        );
                    } else {
                        updatedRec = new ItemIOU(
                            id, this.currentDirection, personName,
                            itemName, itemCondition, itemEstValue, dateCreated, dueDate, reason, status
                        );
                    }

                    this.manager.getAllRecords().set(indexInList, updatedRec);
                }
            } else {
                int newId = this.manager.getNextRecordId();
                IOURecord newRec;
                if ("Cash".equals(this.currentCategory)) {
                    newRec = new CashIOU(
                        newId, this.currentDirection, personName,
                        cashAmount, cashAmount, dateCreated, dueDate, reason, "Pending"
                    );
                } else {
                    newRec = new ItemIOU(
                        newId, this.currentDirection, personName,
                        itemName, itemCondition, itemEstValue, dateCreated, dueDate, reason, "Pending"
                    );
                }
                this.manager.addRecord(newRec);
            }

            this.mainFrame.triggerStorageSyncIndicator();
            this.mainFrame.switchScreen("dashboard");

        } catch (InvalidAmountException amtEx) {
            JTextField targetField = "Cash".equals(this.currentCategory) ? this.cashAmountField : this.itemEstValueField;
            markFieldError(targetField, "InvalidAmountException", amtEx.getMessage());
        } catch (InvalidDateFormatException dateEx) {
            markFieldError(this.dateCreatedField, "InvalidDateFormatException", dateEx.getMessage());
        } catch (IllegalArgumentException argEx) {
            JOptionPane.showMessageDialog(this, argEx.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
