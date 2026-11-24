package ioutracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application window (JFrame) featuring the left sidebar navigation menu,
 * storage sync status indicator, and a CardLayout hosting the 3 main screens.
 */
public class MainFrame extends JFrame {


    public static final String APP_TITLE    = "IOU (\"I Owe You\") Tracker";
	public static final String TEXT_SYNCED  = "All data synced to storage.";
	public static final String TEXT_SYNCING = "Syncing data to storage...";

    public static final Font FONT_SIDEBAR_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_NAV_BTN       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STATUS        = new Font("Segoe UI", Font.BOLD, 12);

    public static final Color COLOR_SIDEBAR_BG     = new Color(0x26, 0x32, 0x38);
    public static final Color COLOR_SIDEBAR_BTN    = new Color(0x37, 0x47, 0x4F);
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(0x19, 0x76, 0xD2);
    public static final Color COLOR_SIDEBAR_STATUS = new Color(0x1C, 0x26, 0x2B);
    public static final Color COLOR_MAIN_BG        = new Color(0xEC, 0xEF, 0xF1);
    public static final Color COLOR_STATUS_GREEN   = new Color(0x81, 0xC7, 0x84);
    public static final Color COLOR_STATUS_YELLOW  = new Color(0xFF, 0xF1, 0x76);


    private IOUManager manager;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private DashboardPanel dashboardPanel;
    private FormPanel formPanel;
    private ReportsPanel reportsPanel;

    private JButton navBtnDashboard;
    private JButton navBtnForm;
    private JButton navBtnReports;
    private JLabel statusLabel;

    // Constructs the MainFrame and initializes the sidebar and 3 screen panels
    public MainFrame(IOUManager manager) {
        this.manager = manager;
        setTitle(APP_TITLE);
        setSize(1180, 740);
        setMinimumSize(new Dimension(980, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    // Initializes the BorderLayout, left sidebar, and CardLayout screens
    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_MAIN_BG);

        JPanel sidebar = createSidebarPanel();
        rootPanel.add(sidebar, BorderLayout.WEST);

        this.cardLayout = new CardLayout();
        this.mainContentPanel = new JPanel(this.cardLayout);
        this.mainContentPanel.setBackground(COLOR_MAIN_BG);
        this.mainContentPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        this.dashboardPanel = new DashboardPanel(this.manager, this);
        this.formPanel = new FormPanel(this.manager, this);
        this.reportsPanel = new ReportsPanel(this.manager, this);

        this.mainContentPanel.add(this.dashboardPanel, "dashboard");
        this.mainContentPanel.add(this.formPanel, "form");
        this.mainContentPanel.add(this.reportsPanel, "reports");

        rootPanel.add(this.mainContentPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        switchScreen("dashboard");
    }

    // Creates and returns the dark left navigation sidebar panel
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(COLOR_SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, COLOR_SIDEBAR_STATUS));

        // Top section: Header + Navigation Buttons
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SIDEBAR_BTN),
            new EmptyBorder(20, 18, 16, 18)
        ));
        JLabel titleLabel = new JLabel("<html><div style='width: 155px;'>" + APP_TITLE + "</div></html>");
        titleLabel.setFont(FONT_SIDEBAR_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        topSection.add(headerPanel, BorderLayout.NORTH);

        JPanel navButtonsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        navButtonsPanel.setOpaque(false);
        navButtonsPanel.setBorder(new EmptyBorder(18, 12, 18, 12));

		this.navBtnDashboard = createNavButton("Dashboard");
		this.navBtnForm = createNavButton("Add / Edit");
		this.navBtnReports = createNavButton("Reports");

        this.navBtnDashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchScreen("dashboard");
            }
        });

        this.navBtnForm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddForm();
            }
        });

        this.navBtnReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchScreen("reports");
            }
        });

        navButtonsPanel.add(this.navBtnDashboard);
        navButtonsPanel.add(this.navBtnForm);
        navButtonsPanel.add(this.navBtnReports);
        topSection.add(navButtonsPanel, BorderLayout.CENTER);

        sidebar.add(topSection, BorderLayout.NORTH);

        // Bottom section: Sync status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(COLOR_SIDEBAR_STATUS);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_SIDEBAR_BTN),
            new EmptyBorder(12, 14, 12, 14)
        ));

        this.statusLabel = new JLabel(TEXT_SYNCED);
        this.statusLabel.setFont(FONT_STATUS);
        this.statusLabel.setForeground(COLOR_STATUS_GREEN);
        statusPanel.add(this.statusLabel, BorderLayout.CENTER);

        sidebar.add(statusPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    // Helper method to create a styled sidebar navigation button
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NAV_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_SIDEBAR_BTN);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(210, 44));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x54, 0x6E, 0x7A), 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        return btn;
    }

    // Switches the active screen in the CardLayout ("dashboard", "form", or "reports")
    public void switchScreen(String screenName) {
        if ("dashboard".equals(screenName)) {
            this.dashboardPanel.refreshDashboard();
            this.cardLayout.show(this.mainContentPanel, "dashboard");
            updateNavButtonsStyle(this.navBtnDashboard);
        } else if ("form".equals(screenName)) {
            this.formPanel.populateExistingContactsDropdown();
            this.cardLayout.show(this.mainContentPanel, "form");
            updateNavButtonsStyle(this.navBtnForm);
        } else if ("reports".equals(screenName)) {
            this.reportsPanel.refreshReportsView();
            this.cardLayout.show(this.mainContentPanel, "reports");
            updateNavButtonsStyle(this.navBtnReports);
        }
    }

    // Opens Screen 2 (FormPanel) in "Add New IOU Record" mode
    public void openAddForm() {
        this.formPanel.prepareForAdd();
        switchScreen("form");
    }

    // Opens Screen 2 (FormPanel) in "Edit IOU Record" mode for the given record
    public void openEditForm(IOURecord record) {
        this.formPanel.prepareForEdit(record);
        switchScreen("form");
    }

    // Switches to Screen 3 (ReportsPanel) and selects the specified person
    public void jumpToPersonReport(String personName) {
        this.reportsPanel.setSelectedPerson(personName);
        switchScreen("reports");
    }

    // Saves data to disk and briefly animates the sidebar sync status indicator
    public void triggerStorageSyncIndicator() {
        this.manager.saveAllToDisk();
        this.statusLabel.setText(TEXT_SYNCING);
        this.statusLabel.setForeground(COLOR_STATUS_YELLOW);

        Timer timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(TEXT_SYNCED);
                statusLabel.setForeground(COLOR_STATUS_GREEN);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Updates the visual background/border of the sidebar buttons to show the active screen
    private void updateNavButtonsStyle(JButton activeBtn) {
        JButton[] buttons = { this.navBtnDashboard, this.navBtnForm, this.navBtnReports };
        for (int i = 0; i < buttons.length; i++) {
            JButton b = buttons[i];
            if (b == activeBtn) {
                b.setBackground(COLOR_SIDEBAR_ACTIVE);
                b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x64, 0xB5, 0xF6), 1),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            } else {
                b.setBackground(COLOR_SIDEBAR_BTN);
                b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x54, 0x6E, 0x7A), 1),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            }
        }
    }
}
