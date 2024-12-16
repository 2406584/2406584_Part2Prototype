import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AssetTrackingApp extends JFrame {
    public static final String DATABASE_URL = "jdbc:sqlite:assets.db";
    // User and asset management variables
    public HashMap<String, User> users;
    private static final String[] ASSET_TYPES = {"Desktop", "Laptop", "Tablet", "Mobile Phone", "Other (please specify in notes)"};
    public static User currentUser; // The logged-in user

    // UI components
    public JTextField usernameField;
    public JPasswordField passwordField;
    public final JPanel loginPanel;
    public JPanel mainPanel;
    public JPanel cardPanel;
    public static JTextField assetTagField;
    public static JTextField systemNameField;
    public static JTextField modelField;
    public static JTextField manufacturerField;
    public static JTextField typeField;
    public static JTextField ipAddressField;
    public static JTextField purchaseDateField;
    public static JTextField employeeFirstNameField;
    public static JTextField employeeLastNameField;
    public static JTextField employeeEmailField;
    public static JTextArea notesField;
    public static JComboBox<String> departmentComboBox;
    public JComboBox<String> typeComboBox;
    public DefaultTableModel tableModel;
    public JTable assetTable;



    private static final String[] DEPARTMENTS = {"Finance", "Human Resources", "Operations", "Sales", "Information Technology", "Admin"};

    // Database

    public AssetTrackingApp() {
        // Set up the main frame
        setTitle("Asset Tracking System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the user map and other variables
        users = new HashMap<>();
        initializeUsers();

        // Create the database and assets table
        createNewDatabase();

        // Create UI components
        loginPanel = createLoginPanel();
        add(loginPanel, BorderLayout.CENTER);
        mainPanel = createMainPanel();

        // Set layout and add components
        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);

        // Show the frame
        setVisible(true); // Make sure to make the frame visible after adding components
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");

                String sql = new StringBuilder().append("CREATE TABLE IF NOT EXISTS assets (\n").append(" id TEXT PRIMARY KEY,\n").append(" assetTag TEXT NOT NULL,\n").append(" systemName TEXT NOT NULL,\n").append(" model TEXT NOT NULL,\n").append(" manufacturer TEXT NOT NULL,\n").append(" type TEXT NOT NULL,\n").append(" ipAddress TEXT NOT NULL,\n").append(" purchaseDate TEXT NOT NULL,\n").append(" notes TEXT,\n").append(" employeeFirstName TEXT NOT NULL,\n").append(" employeeLastName TEXT NOT NULL,\n").append(" employeeEmail TEXT NOT NULL,\n").append(" department TEXT NOT NULL\n").append(");").toString();

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Table 'assets' has been created.");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Method to specify users
    private void initializeUsers() {
        // Initialize users and store them in the 'users' map
        users.put("admin", new User("admin", "adminPass", "Admin", "User", "admin@scottishglen.com", "Admin"));
        users.put("finance_user", new User("finance_user", "financePass", "Finance", "User", "finance.user@scottishglen.com", "Finance"));
        users.put("hr_user", new User("hr_user", "hrPass", "HR", "User", "hr.user@scottishglen.com", "Human Resources"));
        users.put("operations_user", new User("operations_user", "operationsPass", "Operations", "User", "operations.user@scottishglen.com", "Operations"));
        users.put("sales_user", new User("sales_user", "salesPass", "Sales", "User", "sales.user@scottishglen.com", "Sales"));
        users.put("it_user", new User("it_user", "itPass", "IT", "User", "it.user@scottishglen.com", "Information Technology"));

        // Check if the current user exists and is logged in
        if (currentUser != null && users.containsKey(currentUser.getUsername())) {
            // If currentUser exists in the map, proceed with the current user
            System.out.println("Logged in as: " + currentUser.getUsername());
        } else {
            // Handle the case where the user is not logged in
            System.out.println("No user logged in or invalid user.");
        }
    }


    // Method to create login panel
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asset Management"));
        panel.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel(new ImageIcon("resources/Logo.png"));
        panel.add(logoLabel, BorderLayout.NORTH); // Place logo at the top

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered layout with spacing
        JButton addButton = new JButton("Add Asset");
        JButton editButton = new JButton("Edit Asset");
        JButton viewButton = new JButton("View Assets");
        JButton logoutButton = new JButton("Logout");

        addButton.addActionListener(e -> switchToPanel("Add"));
        editButton.addActionListener(e -> switchToPanel("Edit"));
        viewButton.addActionListener(e -> switchToPanel("View"));
        logoutButton.addActionListener(e -> logout());

        styleButton(addButton);
        styleButton(editButton);
        styleButton(viewButton);
        styleButton(logoutButton);

        Dimension buttonSize = new Dimension(125, 50); // Adjusted dimensions for usability
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        viewButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.CENTER); // Place button panel in the center

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(createAddAssetPanel(), "Add");

        // Debugging: Check if currentUser is null or not
        System.out.println("currentUser is null: " + (currentUser == null));

        if (currentUser != null) {
            // Debugging: Check if panels are initialized correctly
            System.out.println("Adding EditAssetPanel: " + (currentUser.createEditAssetPanel() != null));
            System.out.println("Adding ViewAssetPanel: " + (currentUser.createViewAssetPanel() != null));

            JPanel editPanel = (JPanel) currentUser.createEditAssetPanel();
            JPanel viewPanel = (JPanel) currentUser.createViewAssetPanel();

            // Add the panels only if they are not null
            if (editPanel != null) {
                cardPanel.add(editPanel, "Edit");
            } else {
                // Add a default panel or message
                JPanel defaultEditPanel = new JPanel();
                defaultEditPanel.add(new JLabel("Please log in to edit assets."));
                cardPanel.add(defaultEditPanel, "Edit");
            }

            if (viewPanel != null) {
                cardPanel.add(viewPanel, "View");
            } else {
                // Add a default panel or message
                JPanel defaultViewPanel = new JPanel();
                defaultViewPanel.add(new JLabel("Please log in to view assets."));
                cardPanel.add(defaultViewPanel, "View");
            }
        } else {
            // Handle null user, maybe show a login prompt or default message
            JPanel defaultPanel = new JPanel();
            defaultPanel.add(new JLabel("Please log in to access asset management features."));
            cardPanel.add(defaultPanel, "Edit");
            cardPanel.add(defaultPanel, "View");
        }

        // Ensure cardPanel is not null before adding to the main panel
        System.out.println("cardPanel is null: " + (cardPanel == null));

        panel.add(cardPanel, BorderLayout.SOUTH); // Place card panel at the bottom

        setFullScreen();

        return panel;
    }






    // Method to set the application to fullscreen
    private void setFullScreen() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(cardPanel);
        if (frame != null) {
            device.setFullScreenWindow(frame);
            frame.setUndecorated(true); // Optional: Remove window decorations
        }
    }


    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JLabel usernameLabel = new JLabel("Username:");
        loginPanel.add(usernameLabel, createConstraints(0, 0));
        loginPanel.add(usernameField, createConstraints(1, 0));

        JLabel passwordLabel = new JLabel("Password:");
        loginPanel.add(passwordLabel, createConstraints(0, 1));
        loginPanel.add(passwordField, createConstraints(1, 1));

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 128, 0)); // Green background
        loginButton.setForeground(Color.WHITE); // White text color
        loginButton.addActionListener(e -> authenticate());
        loginPanel.add(loginButton, createConstraints(0, 2, 2, 1));

        return loginPanel;
    }


    // Check credentials against stored users
    private boolean isValidCredentials(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }


    // Helper method to create constraints for GridBagLayout positioning
    private GridBagConstraints createConstraints(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    // Overloaded helper method for wider components like buttons
    private GridBagConstraints createConstraints(int x, int y, int width, int height) {
        GridBagConstraints gbc = createConstraints(x, y);
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 128, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    // Method to create Add Asset panel
    private JPanel createAddAssetPanel() {
        JPanel addAssetPanel = new JPanel(new GridBagLayout());
        addAssetPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Add Asset & Employee Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel employeeInfoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Employee Information"));

        employeeFirstNameField = new JTextField(10);
        employeeLastNameField = new JTextField(10);
        employeeEmailField = new JTextField(10);
        departmentComboBox = new JComboBox<>(DEPARTMENTS);
        departmentComboBox.setPreferredSize(new Dimension(100, 25)); // Set a preferred size for combo box

        employeeInfoPanel.add(new JLabel("First Name:"));
        employeeInfoPanel.add(employeeFirstNameField);
        employeeInfoPanel.add(new JLabel("Last Name:"));
        employeeInfoPanel.add(employeeLastNameField);
        employeeInfoPanel.add(new JLabel("Email:"));
        employeeInfoPanel.add(employeeEmailField);
        employeeInfoPanel.add(new JLabel("Department:"));
        employeeInfoPanel.add(departmentComboBox);

        JPanel assetInfoPanel = new JPanel(new GridBagLayout());
        assetInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asset Information"));

        GridBagConstraints assetGbc = new GridBagConstraints();
        assetGbc.insets = new Insets(5, 5, 5, 5);
        assetGbc.anchor = GridBagConstraints.WEST;
        assetGbc.fill = GridBagConstraints.HORIZONTAL;
        assetGbc.gridx = 0;
        assetGbc.gridy = GridBagConstraints.RELATIVE;

        assetTagField = new JTextField();
        systemNameField = new JTextField(getSystemProperty("os.name"));
        modelField = new JTextField(getSystemProperty("os.version"));
        manufacturerField = new JTextField("Unknown"); // Java does not provide this property - one for the polished app?
        typeComboBox = new JComboBox<>(ASSET_TYPES);
        ipAddressField = new JTextField(getLocalIPAddress());
        purchaseDateField = new JTextField();
        notesField = new JTextArea(3, 20);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);

        assetInfoPanel.add(new JLabel("Asset Tag:"), assetGbc);
        assetInfoPanel.add(assetTagField, assetGbc);

        assetInfoPanel.add(new JLabel("System Name:"), assetGbc);
        assetInfoPanel.add(systemNameField, assetGbc);

        assetInfoPanel.add(new JLabel("Model:"), assetGbc);
        assetInfoPanel.add(modelField, assetGbc);

        assetInfoPanel.add(new JLabel("Manufacturer:"), assetGbc);
        assetInfoPanel.add(manufacturerField, assetGbc);

        assetInfoPanel.add(new JLabel("Type:"), assetGbc);
        assetInfoPanel.add(typeComboBox, assetGbc);

        assetInfoPanel.add(new JLabel("IP Address:"), assetGbc);
        assetInfoPanel.add(ipAddressField, assetGbc);

        assetInfoPanel.add(new JLabel("Purchase Date:"), assetGbc);
        assetInfoPanel.add(purchaseDateField, assetGbc);

        assetInfoPanel.add(new JLabel("Notes:"), assetGbc);
        JScrollPane notesScrollPane = new JScrollPane(notesField);
        notesScrollPane.setPreferredSize(new Dimension(200, 60));
        assetInfoPanel.add(notesScrollPane, assetGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        addAssetPanel.add(employeeInfoPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        addAssetPanel.add(assetInfoPanel, gbc);


        JButton addAssetButton = new JButton("Add Asset");
        addAssetButton.setPreferredSize(new Dimension(100, 30));  // Set smaller size
        addAssetButton.setBackground(new Color(0, 128, 0)); // Set background to green
        addAssetButton.setForeground(Color.WHITE); // Optional: Set text color to white
        addAssetButton.addActionListener(e -> addAsset());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        addAssetPanel.add(addAssetButton, gbc);

        return addAssetPanel;
    }

    private String getSystemProperty(String property) {
        String unknown = "Unknown";
        if ("system.name".equals(property)) {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return unknown;
            }
        }
        return System.getProperty(property, unknown);
    }

    private String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }



    // Method to switch to a specific panel in card layout
    private void switchToPanel(String panelName) {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, panelName);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve the user object from the user map
        User user = users.get(username);

        if (user != null) {
            if (user.getPassword().equals(password)) {
                // Set the currentUser to the authenticated user
                currentUser = user;

                // Create the main panel for the logged-in user
                JPanel mainPanel = createMainPanel();

                // Clear login fields
                usernameField.setText("");
                passwordField.setText("");

                // Remove the login panel and add the main panel
                if (loginPanel != null) {
                    remove(loginPanel);  // Make sure loginPanel is not null
                }
                add(mainPanel, BorderLayout.CENTER);

                // Refresh the UI to reflect the updated panel
                revalidate();
                repaint();
            } else {
                // Handle incorrect password
                JOptionPane.showMessageDialog(this, "Incorrect password. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // Clear the password field
            }
        } else {
            // Handle non-existent username
            JOptionPane.showMessageDialog(this, "Username not found. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
            usernameField.setText(""); // Optionally clear username field
            passwordField.setText(""); // Clear password field
        }
    }




    private JPanel createWelcomePanel(User user) {

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 128, 0)), "Welcome"));
        welcomePanel.setBackground(Color.WHITE); // Main background color for welcome panel

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFirstName() + " " + user.getLastName() + "! What would you like to do?");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(0, 128, 0)); // Green text color for welcome message
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        // Create buttons with consistent styling
        JButton viewAssetsButton = new JButton("View Assets");
        JButton addAssetButton = new JButton("Add Asset");
        // JButton deleteAssetButton = new JButton("Delete Asset");
        JButton editAssetButton = new JButton("Edit Asset");
        JButton logoutButton = new JButton("Logout");

        // Style and configure each button
        styleButton(viewAssetsButton);
        styleButton(addAssetButton);
        // styleButton(deleteAssetButton);
        styleButton(editAssetButton);
        styleButton(logoutButton);


        viewAssetsButton.addActionListener(e -> showMainPanel("View"));
        addAssetButton.addActionListener(e -> showMainPanel("Add"));
        // deleteAssetButton.addActionListener(e -> showMainPanel("Delete"));
        editAssetButton.addActionListener(e -> showMainPanel("Edit"));
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(viewAssetsButton);
        buttonPanel.add(addAssetButton);
        // buttonPanel.add(deleteAssetButton);
        buttonPanel.add(editAssetButton);
        buttonPanel.add(logoutButton);

        welcomePanel.add(buttonPanel, BorderLayout.CENTER);

        return welcomePanel;
    }

    private void showMainPanel(String panelName) {
        if (mainPanel == null) {
            mainPanel = createMainPanel(); // Initialize mainPanel if null
        }

        getContentPane().removeAll();
        add(mainPanel, BorderLayout.CENTER);

        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, panelName);

        revalidate();
        repaint();
    }

    // Method to add asset to database
    private void addAsset() {
        String assetTag = assetTagField.getText();
        String systemName = systemNameField.getText();
        String model = modelField.getText();
        String manufacturer = manufacturerField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        String ipAddress = ipAddressField.getText();
        String purchaseDate = purchaseDateField.getText();
        String notes = notesField.getText();
        String employeeFirstName = employeeFirstNameField.getText();
        String employeeLastName = employeeLastNameField.getText();
        String employeeEmail = employeeEmailField.getText();
        String department = (String) departmentComboBox.getSelectedItem();

        if (assetTag.isEmpty() || systemName.isEmpty() || model.isEmpty() || manufacturer.isEmpty() ||
                type == null || ipAddress.isEmpty() || purchaseDate.isEmpty() ||
                employeeFirstName.isEmpty() || employeeLastName.isEmpty() || employeeEmail.isEmpty() || department == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = UUID.randomUUID().toString();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "INSERT INTO assets (id, assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes, employeeFirstName, employeeLastName, employeeEmail, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, assetTag);
                pstmt.setString(3, systemName);
                pstmt.setString(4, model);
                pstmt.setString(5, manufacturer);
                pstmt.setString(6, type);
                pstmt.setString(7, ipAddress);
                pstmt.setString(8, purchaseDate);
                pstmt.setString(9, notes);
                pstmt.setString(10, employeeFirstName);
                pstmt.setString(11, employeeLastName);
                pstmt.setString(12, employeeEmail);
                pstmt.setString(13, department);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Asset added successfully!");
                clearAssetFields();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace for debugging
            JOptionPane.showMessageDialog(this, "Failed to add asset: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void clearAssetFields() {
        assetTagField.setText("");
        systemNameField.setText("");
        modelField.setText("");
        manufacturerField.setText("");
        typeComboBox.setSelectedIndex(0);
        ipAddressField.setText("");
        purchaseDateField.setText("");
        notesField.setText("");
        employeeFirstNameField.setText("");
        employeeLastNameField.setText("");
        employeeEmailField.setText("");
        departmentComboBox.setSelectedIndex(0);
    }

    private void logout() {
        System.out.println("Logging out...");
        System.out.println("cardPanel is null: " + (cardPanel == null));

        if (cardPanel != null) {
            System.out.println("Removing all components from cardPanel.");
            cardPanel.removeAll();  // Attempt to remove all components

            // After removing, you might want to reset or replace the panel with a login form
            JPanel loginPanel = createLoginPanel();
            cardPanel.add(loginPanel, "Login");

            cardPanel.revalidate();  // Ensure the layout is updated
            cardPanel.repaint();  // Redraw the component

            System.out.println("cardPanel after reset: " + cardPanel);
        } else {
            System.out.println("cardPanel is null, skipping logout.");
        }

        // Log out user and reset state
        currentUser = null;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssetTrackingApp app = new AssetTrackingApp();
            app.setVisible(true);
        });
    }

    // Method to create Edit Asset panel
    public JPanel createEditAssetPanel() {
        if (AssetTrackingApp.currentUser == null) {
            System.out.println("No user logged in.");
            // Optionally, redirect to a login screen or show an error dialog
            return new JPanel(); // Return an empty panel or appropriate message
        }


    // Method to edit an asset in the database
        // Create the edit panel
        JPanel editPanel = new JPanel(new BorderLayout());

        // Create a table to display assets
        String[] columnNames = {"Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Department"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable assetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assetTable);
        editPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate the table with assets
        try (Connection conn = DriverManager.getConnection(AssetTrackingApp.DATABASE_URL)) {
            String sql = "SELECT assetTag, systemName, model, manufacturer, type, ipAddress, department FROM assets";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("assetTag"),
                            rs.getString("systemName"),
                            rs.getString("model"),
                            rs.getString("manufacturer"),
                            rs.getString("type"),
                            rs.getString("ipAddress"),
                            rs.getString("department")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(editPanel, "Error loading assets: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add an "Edit" button
        JButton editAssetButton = new JButton("Edit Selected Asset");
        editAssetButton.addActionListener(e -> {
            int selectedRow = assetTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(editPanel, "Please select an asset to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get the selected asset's details
            String assetTag = (String) tableModel.getValueAt(selectedRow, 0);

            // Open a dialog for editing the asset
            JDialog editDialog = new JDialog((Frame) null, "Edit Asset", true);
            editDialog.setLayout(new GridLayout(0, 2));

            JTextField systemNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField modelField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField manufacturerField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
            JTextField typeField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));
            JTextField ipAddressField = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
            JComboBox<String> departmentComboBox = new JComboBox<>(new String[]{"HR", "IT", "Finance", "Operations"}); // Example departments
            departmentComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 6));

            // Add fields to dialog
            editDialog.add(new JLabel("System Name:"));
            editDialog.add(systemNameField);
            editDialog.add(new JLabel("Model:"));
            editDialog.add(modelField);
            editDialog.add(new JLabel("Manufacturer:"));
            editDialog.add(manufacturerField);
            editDialog.add(new JLabel("Type:"));
            editDialog.add(typeField);
            editDialog.add(new JLabel("IP Address:"));
            editDialog.add(ipAddressField);
            editDialog.add(new JLabel("Department:"));
            editDialog.add(departmentComboBox);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(saveEvent -> {
                try (Connection conn = DriverManager.getConnection(AssetTrackingApp.DATABASE_URL)) {
                    String updateSql = "UPDATE assets SET systemName=?, model=?, manufacturer=?, type=?, ipAddress=?, department=? WHERE assetTag=?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setString(1, systemNameField.getText());
                        pstmt.setString(2, modelField.getText());
                        pstmt.setString(3, manufacturerField.getText());
                        pstmt.setString(4, typeField.getText());
                        pstmt.setString(5, ipAddressField.getText());
                        pstmt.setString(6, (String) departmentComboBox.getSelectedItem());
                        pstmt.setString(7, assetTag);

                        int rowsUpdated = pstmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(editDialog, "Asset updated successfully!");
                            tableModel.setValueAt(systemNameField.getText(), selectedRow, 1);
                            tableModel.setValueAt(modelField.getText(), selectedRow, 2);
                            tableModel.setValueAt(manufacturerField.getText(), selectedRow, 3);
                            tableModel.setValueAt(typeField.getText(), selectedRow, 4);
                            tableModel.setValueAt(ipAddressField.getText(), selectedRow, 5);
                            tableModel.setValueAt(departmentComboBox.getSelectedItem(), selectedRow, 6);
                            editDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(editDialog, "Asset not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Error updating asset: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            editDialog.add(new JLabel()); // Empty cell for alignment
            editDialog.add(saveButton);

            editDialog.pack();
            editDialog.setLocationRelativeTo(editPanel);
            editDialog.setVisible(true);
        });

        editPanel.add(editAssetButton, BorderLayout.SOUTH);
        return editPanel;
    }
    JPanel createViewAssetPanel() {
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "View Assets"));

        // Create a table to show assets
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{
                "Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address",
                "Purchase Date", "Employee First Name", "Employee Last Name", "Employee Email", "Department"
        }, 0);
        JTable assetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assetTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 128, 0)); // Set background to green
        refreshButton.setForeground(Color.WHITE); // Optional: Set text color to white
        refreshButton.addActionListener(e -> viewAssets());

        viewPanel.add(scrollPane, BorderLayout.CENTER);
        viewPanel.add(refreshButton, BorderLayout.SOUTH);

        return viewPanel;
    }

    // Method to view assets based on the currently logged-in user
    private void viewAssets() {
        // Clear the table before adding new data
        DefaultTableModel tableModel = null;
        tableModel.setRowCount(0);

        // SQL query to fetch all assets
        String sql = "SELECT * FROM assets";

        try (Connection conn = DriverManager.getConnection(AssetTrackingApp.DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterate through the result set and add rows to the table model
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("assetTag"),
                        rs.getString("systemName"),
                        rs.getString("model"),
                        rs.getString("manufacturer"),
                        rs.getString("type"),
                        rs.getString("ipAddress"),
                        rs.getString("purchaseDate"),
                        rs.getString("employeeFirstName"),
                        rs.getString("employeeLastName"),
                        rs.getString("employeeEmail"),
                        rs.getString("department")
                });
            }
        } catch (SQLException e) {
            // Show an error message in case of failure
            JOptionPane.showMessageDialog(this, "Error retrieving assets: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

class User {
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String department;

    public User(String username, String password, String firstName, String lastName, String email, String department) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public Component createEditAssetPanel() {
        return null;
    }

    public Component createViewAssetPanel() {
        return null;
    }
}