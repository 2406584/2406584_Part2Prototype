import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AssetTrackingApp extends JFrame {
    // User and asset management variables
    public HashMap<String, User> users;
    private static final String[] ASSET_TYPES = {"Desktop", "Laptop", "Tablet", "Mobile Phone", "Other (please specify in notes)"};

    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final JPanel loginPanel;
    private final JPanel mainPanel;
    private JPanel cardPanel;
    private JTextField assetTagField, systemNameField, modelField, manufacturerField, typeField, ipAddressField, purchaseDateField, employeeFirstNameField, employeeLastNameField, employeeEmailField;
    private JTextArea notesField;
    private JComboBox<String> departmentComboBox, typeComboBox;


    private static final String[] DEPARTMENTS = {"Finance", "Human Resources", "Operations", "Sales", "Information Technology", "Admin"};

    private static final String DATABASE_URL = "jdbc:sqlite:assets.db";

    public AssetTrackingApp() {
        // Set up the main frame
        setTitle("Asset Tracking System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set the frame to be maximized
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen

        // Initialize the user map and other variables
        users = new HashMap<>();
        initializeUsers();

        // Create the database and assets table
        createNewDatabase();

        // Create UI components
        loginPanel = createLoginPanel();
        mainPanel = createMainPanel();

        // Set layout and add components
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Optionally set background color of the main panel if needed
        mainPanel.setBackground(Color.WHITE); // Set a default background color if desired

        // Show the frame
        setVisible(true); // Make sure to make the frame visible after adding components
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");

                // Create a new table
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

    private void initializeUsers() {
        users.put("admin", new User("admin", "adminPass", "Admin", "User", "admin@scottishglen.com", "Admin"));
        users.put("finance_user", new User("finance_user", "financePass", "Finance", "User", "finance.user@scottishglen.com", "Finance"));
        users.put("hr_user", new User("hr_user", "hrPass", "HR", "User", "hr.user@scottishglen.com", "Human Resources"));
        users.put("operations_user", new User("operations_user", "operationsPass", "Operations", "User", "operations.user@scottishglen.com", "Operations"));
        users.put("sales_user", new User("sales_user", "salesPass", "Sales", "User", "sales.user@scottishglen.com", "Sales"));
        users.put("it_user", new User("it_user", "itPass", "IT", "User", "it.user@scottishglen.com", "Information Technology"));
    }


    // Method to create login panel
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asset Management"));

        // Set the background color of the main panel to white
        panel.setBackground(Color.WHITE);

        // Add Logo at the Top
        JLabel logoLabel = new JLabel(new ImageIcon("resources/Logo.png"));
        panel.add(logoLabel, BorderLayout.NORTH); // Place logo at the top

        // Create Button Panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered layout with spacing
        JButton addButton = new JButton("Add Asset");
        // JButton editButton = new JButton("Edit Asset");
        // JButton deleteButton = new JButton("Delete Asset");
        // JButton viewButton = new JButton("View Assets");
        JButton logoutButton = new JButton("Logout");

        // Action Listeners for Buttons
        addButton.addActionListener(e -> switchToPanel("Add"));
        // editButton.addActionListener(e -> switchToPanel("Edit"));
        // deleteButton.addActionListener(e -> switchToPanel("Delete"));
        // viewButton.addActionListener(e -> switchToPanel("View"));
        logoutButton.addActionListener(e -> logout());

        // Styling Buttons
        styleButton(addButton);
        // styleButton(editButton);
        // styleButton(deleteButton);
        // styleButton(viewButton);
        styleButton(logoutButton);

        // Set a fixed size for buttons (adjust the width and height as necessary)
        Dimension buttonSize = new Dimension(125, 50); // Adjusted dimensions for usability
        addButton.setPreferredSize(buttonSize);
        // editButton.setPreferredSize(buttonSize);
        // deleteButton.setPreferredSize(buttonSize);
        // viewButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        // Add Buttons to the Button Panel
        buttonPanel.add(addButton);
        // buttonPanel.add(editButton);
        // buttonPanel.add(deleteButton);
        // buttonPanel.add(viewButton);
        buttonPanel.add(logoutButton);

        // Add the button panel to the main panel
        panel.add(buttonPanel, BorderLayout.CENTER); // Place button panel in the center

        // Main content panel (card layout)
        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(createAddAssetPanel(), "Add");
        // cardPanel.add(createEditAssetPanel(), "Edit");
        // cardPanel.add(createDeleteAssetPanel(), "Delete");
        // cardPanel.add(createViewAssetPanel(), "View");

        // Add the card panel below the buttons with stretching capability
        panel.add(cardPanel, BorderLayout.SOUTH); // Place card panel at the bottom

        // Set the frame to fullscreen
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

        // Initialize the username and password fields and assign them to the instance variables
        usernameField = new JTextField(15);  // Assign to the instance variable
        passwordField = new JPasswordField(15);  // Assign to the instance variable

        // Label and field for username
        JLabel usernameLabel = new JLabel("Username:");
        loginPanel.add(usernameLabel, createConstraints(0, 0));
        loginPanel.add(usernameField, createConstraints(1, 0));

        // Label and field for password
        JLabel passwordLabel = new JLabel("Password:");
        loginPanel.add(passwordLabel, createConstraints(0, 1));
        loginPanel.add(passwordField, createConstraints(1, 1));

        // Login button with action
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
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Set font and size
        button.setBackground(new Color(0, 128, 0));    // Set background color (steel blue)
        button.setForeground(Color.WHITE);                // Set text color
        button.setFocusPainted(false);                    // Remove focus outline
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Add padding
    }

    // Method to create Add Asset panel
    private JPanel createAddAssetPanel() {
        JPanel addAssetPanel = new JPanel(new GridBagLayout());
        addAssetPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Add Asset & Employee Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Employee Information Panel
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

        // Asset Information Panel with more spacing
        JPanel assetInfoPanel = new JPanel(new GridBagLayout());
        assetInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asset Information"));

        GridBagConstraints assetGbc = new GridBagConstraints();
        assetGbc.insets = new Insets(5, 5, 5, 5);
        assetGbc.anchor = GridBagConstraints.WEST;
        assetGbc.fill = GridBagConstraints.HORIZONTAL;
        assetGbc.gridx = 0;
        assetGbc.gridy = GridBagConstraints.RELATIVE; // Allow auto-increment of rows

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

        // Add each field with label and component in its own row
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

        // Add Employee and Asset Panels Side by Side
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        addAssetPanel.add(employeeInfoPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        addAssetPanel.add(assetInfoPanel, gbc);

        // Add Asset Button with smaller size
        JButton addAssetButton = new JButton("Add Asset");
        addAssetButton.setPreferredSize(new Dimension(100, 30));  // Set smaller size
        addAssetButton.setBackground(new Color(0, 128, 0)); // Set background to green
        addAssetButton.setForeground(Color.WHITE); // Optional: Set text color to white
        addAssetButton.addActionListener(e -> addAsset());

        // Position the Add Asset Button below the panels
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
                return unknown;  // Return "Unknown" if hostname cannot be determined
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

    // Method to authenticate user
    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = users.get(username);

        if (user != null && user.getPassword().equals(password)) {
            // Switch to the main panel upon successful login
            JPanel mainPanel = createMainPanel(); // Create main panel on successful login
            remove(loginPanel); // Remove login panel
            add(mainPanel, BorderLayout.CENTER); // Add main panel to the frame
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
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

        // Validation
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

                // Debugging: Print the values being inserted
                System.out.println("Inserting asset: " + id + ", " + assetTag + ", " + systemName + ", " + model + ", " + manufacturer + ", " + type + ", " + ipAddress + ", " + purchaseDate + ", " + notes + ", " + employeeFirstName + ", " + employeeLastName + ", " + employeeEmail + ", " + department);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Asset added successfully!");
                clearAssetFields();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace for debugging
            JOptionPane.showMessageDialog(this, "Failed to add asset: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to create View Assets Panel
//    private JPanel createViewAssetPanel() {
//        JPanel viewPanel = new JPanel(new BorderLayout());
//        viewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "View Assets"));
//
//        // Create a table to show assets
//        tableModel = new DefaultTableModel(new String[]{"Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Employee First Name", "Employee Last Name", "Employee Email", "Department"}, 0);
//        assetTable = new JTable(tableModel);
//        JScrollPane scrollPane = new JScrollPane(assetTable);
//
//        JButton refreshButton = new JButton("Refresh");
//        refreshButton.setBackground(new Color(0, 128, 0)); // Set background to green
//        refreshButton.setForeground(Color.WHITE); // Optional: Set text color to white
//        refreshButton.addActionListener(e -> viewAssets());
//
//        viewPanel.add(scrollPane, BorderLayout.CENTER);
//        viewPanel.add(refreshButton, BorderLayout.SOUTH);
//
//        return viewPanel;
//    }

    // Method to view assets from the database
//    private void viewAssets() {
//        if (currentUser == null) {
//            JOptionPane.showMessageDialog(this, "Please log in to view assets.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        tableModel.setRowCount(0); // Clear existing data
//
//        String sql;
//        if ("Admin".equals(currentUser.getDepartment())) {
//            // Admin sees all assets
//            sql = "SELECT * FROM assets";
//        } else {
//            // Other users see only assets in their department
//            sql = "SELECT * FROM assets WHERE department = ?";
//        }
//
//        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            if (!"Admin".equals(currentUser.getDepartment())) {
//                pstmt.setString(1, currentUser.getDepartment());
//            }
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    tableModel.addRow(new Object[]{
//                            rs.getString("assetTag"),
//                            rs.getString("systemName"),
//                            rs.getString("model"),
//                            rs.getString("manufacturer"),
//                            rs.getString("type"),
//                            rs.getString("ipAddress"),
//                            rs.getString("purchaseDate"),
//                            rs.getString("employeeFirstName"),
//                            rs.getString("employeeLastName"),
//                            rs.getString("employeeEmail"),
//                            rs.getString("department")
//                    });
//                }
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error retrieving assets: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }



//    // Method to create Edit Asset panel
//    private JPanel createEditAssetPanel() {
//        JPanel editPanel = createAddAssetPanel(); // Reuse Add Asset Panel
//        JButton editAssetButton = new JButton("Edit Asset");
//
//        editAssetButton.addActionListener(e -> editAsset());
//
//        editPanel.add(editAssetButton, BorderLayout.SOUTH);
//        return editPanel;
//    }
//
//    // Method to edit an asset in the database
//    private void editAsset() {
//        String assetTag = assetTagField.getText();
//
//        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
//            String sql = "UPDATE assets SET systemName=?, model=?, manufacturer=?, type=?, ipAddress=?, purchaseDate=?, notes=?, employeeFirstName=?, employeeLastName=?, employeeEmail=?, department=? WHERE assetTag=?";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, systemNameField.getText());
//                pstmt.setString(2, modelField.getText());
//                pstmt.setString(3, manufacturerField.getText());
//                pstmt.setString(4, typeField.getText());
//                pstmt.setString(5, ipAddressField.getText());
//                pstmt.setString(6, purchaseDateField.getText());
//                pstmt.setString(7, notesField.getText());
//                pstmt.setString(8, employeeFirstNameField.getText());
//                pstmt.setString(9, employeeLastNameField.getText());
//                pstmt.setString(10, employeeEmailField.getText());
//                pstmt.setString(11, (String) departmentComboBox.getSelectedItem());
//                pstmt.setString(12, assetTag);
//
//                int rowsUpdated = pstmt.executeUpdate();
//                if (rowsUpdated > 0) {
//                    JOptionPane.showMessageDialog(this, "Asset updated successfully!");
//                    clearAssetFields();
//                } else {
//                    JOptionPane.showMessageDialog(this, "Asset not found!", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error updating asset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

//    private JPanel createDeleteAssetPanel() {
//        JPanel deletePanel = new JPanel(new BorderLayout());
//        deletePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Delete Asset"));
//
//        // Resize the Asset Tag input field to be smaller
//        JTextField assetTagToDeleteField = new JTextField(10);  // Size reduced for better visibility
//        JButton deleteAssetButton = new JButton("Delete Asset");
//
//        deleteAssetButton.addActionListener(e -> {
//            String assetTag = assetTagToDeleteField.getText().trim(); // Get the asset tag input
//            if (!assetTag.isEmpty()) {
//                int response = JOptionPane.showConfirmDialog(this,
//                        "Are you sure you want to delete the asset with Asset Tag: " + assetTag + "?",
//                        "Confirm Deletion",
//                        JOptionPane.YES_NO_OPTION,
//                        JOptionPane.WARNING_MESSAGE);
//
//                if (response == JOptionPane.YES_OPTION) {
//                    boolean success = deleteAsset(assetTag); // Call the deleteAsset method
//                    if (success) {
//                        JOptionPane.showMessageDialog(this, "Asset deletion successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                    } else {
//                        JOptionPane.showMessageDialog(this, "Asset deletion failed. Please check the Asset Tag.", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Please enter an Asset Tag to delete.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        // Add components to the panel
//        deletePanel.add(new JLabel("Enter Asset Tag to Delete:"), BorderLayout.NORTH);
//        deletePanel.add(assetTagToDeleteField, BorderLayout.CENTER);
//        deletePanel.add(deleteAssetButton, BorderLayout.SOUTH);
//
//        return deletePanel;
//    }


    // Method to delete an asset from the database
//    private boolean deleteAsset(String assetTag) {
//        // Establish database connection and prepare the deletion statement
//        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
//            String sql = "DELETE FROM assets WHERE assetTag = ?";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, assetTag);
//                int rowsAffected = pstmt.executeUpdate(); // Returns number of rows affected
//                return rowsAffected > 0; // Returns true if an asset was deleted
//            }
//        } catch (SQLException e) {
//            e.printStackTrace(); // Print the stack trace for debugging
//            return false; // Return false in case of an error
//        }
//    }



    private void clearAssetFields() {
        assetTagField.setText("");
        systemNameField.setText("");
        modelField.setText("");
        manufacturerField.setText("");
        typeComboBox.setSelectedIndex(0); // Reset dropdown to first item
        ipAddressField.setText("");
        purchaseDateField.setText("");
        notesField.setText("");
        employeeFirstNameField.setText("");
        employeeLastNameField.setText("");
        employeeEmailField.setText("");
        departmentComboBox.setSelectedIndex(0);
    }

    private void logout() {
        remove(mainPanel);
        add(loginPanel, BorderLayout.CENTER);
        usernameField.setText("");
        passwordField.setText("");
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssetTrackingApp app = new AssetTrackingApp();
            app.setVisible(true);
        });
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




}
