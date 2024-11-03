import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class AssetTrackingApp extends JFrame {
    // User and asset management variables
    public HashMap<String, User> users;
    private DefaultTableModel tableModel;
    private User currentUser;  // Stores the logged-in user


    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginPanel, mainPanel, cardPanel;
    private JTextField assetTagField, systemNameField, modelField, manufacturerField, typeField, ipAddressField, purchaseDateField, employeeFirstNameField, employeeLastNameField, employeeEmailField;
    private JTextArea notesField;
    private JTable assetTable;
    private JComboBox<String> departmentComboBox;

    private static final String[] DEPARTMENTS = {"Finance", "Human Resources", "Operations", "Sales", "Information Technology", "Admin"};

    private static final String DATABASE_URL = "jdbc:sqlite:assets.db";

    public AssetTrackingApp() {
        setTitle("Asset Tracking System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        users = new HashMap<>();
        initializeUsers();

        // Create the database and assets table
        createNewDatabase();

        // Create UI components
        loginPanel = createLoginPanel();
        mainPanel = createMainPanel();

        // Set layout and add components
        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");

                // Create a new table
                String sql = "CREATE TABLE IF NOT EXISTS assets (\n"
                        + " id TEXT PRIMARY KEY,\n"
                        + " assetTag TEXT NOT NULL,\n"
                        + " systemName TEXT NOT NULL,\n"
                        + " model TEXT NOT NULL,\n"
                        + " manufacturer TEXT NOT NULL,\n"
                        + " type TEXT NOT NULL,\n"
                        + " ipAddress TEXT NOT NULL,\n"
                        + " purchaseDate TEXT NOT NULL,\n"
                        + " notes TEXT,\n"
                        + " employeeFirstName TEXT NOT NULL,\n"
                        + " employeeLastName TEXT NOT NULL,\n"
                        + " employeeEmail TEXT NOT NULL,\n"
                        + " department TEXT NOT NULL\n"
                        + ");";

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
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Login"));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        // Styling for buttons
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        return panel;
    }

    // Integrate panels into main layout
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asset Management"));

        // Button panel for asset management
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Asset");
        JButton editButton = new JButton("Edit Asset");
        JButton deleteButton = new JButton("Delete Asset");
        JButton viewButton = new JButton("View Assets");
        JButton logoutButton = new JButton("Logout");

        addButton.addActionListener(e -> switchToPanel("Add"));
        editButton.addActionListener(e -> switchToPanel("Edit"));
        deleteButton.addActionListener(e -> switchToPanel("Delete"));
        viewButton.addActionListener(e -> switchToPanel("View"));
        logoutButton.addActionListener(e -> logout());

        // Styling buttons
        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(viewButton);
        styleButton(logoutButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(createAddAssetPanel(), "Add");
       // cardPanel.add(createEditAssetPanel(), "Edit");
        cardPanel.add(createDeleteAssetPanel(), "Delete");
        cardPanel.add(createViewAssetPanel(), "View");

        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    // Method to style buttons
    private void styleButton(JButton button) {
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    // Method to create Add Asset panel
    private JPanel createAddAssetPanel() {
        JPanel addAssetPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        addAssetPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Add Asset Information"));

        // Employee Information
        JPanel employeeInfoPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Employee Information"));

        employeeFirstNameField = new JTextField();
        employeeLastNameField = new JTextField();
        employeeEmailField = new JTextField();
        departmentComboBox = new JComboBox<>(DEPARTMENTS);

        employeeInfoPanel.add(new JLabel("First Name:"));
        employeeInfoPanel.add(employeeFirstNameField);
        employeeInfoPanel.add(new JLabel("Last Name:"));
        employeeInfoPanel.add(employeeLastNameField);
        employeeInfoPanel.add(new JLabel("Email:"));
        employeeInfoPanel.add(employeeEmailField);
        employeeInfoPanel.add(new JLabel("Department:"));
        employeeInfoPanel.add(departmentComboBox);

        // Asset Information
        JPanel assetInfoPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        assetInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asset Information"));

        assetTagField = new JTextField();
        systemNameField = new JTextField();
        modelField = new JTextField();
        manufacturerField = new JTextField();
        typeField = new JTextField();
        ipAddressField = new JTextField();
        purchaseDateField = new JTextField();
        notesField = new JTextArea(3, 20);

        assetInfoPanel.add(new JLabel("Asset Tag:"));
        assetInfoPanel.add(assetTagField);
        assetInfoPanel.add(new JLabel("System Name:"));
        assetInfoPanel.add(systemNameField);
        assetInfoPanel.add(new JLabel("Model:"));
        assetInfoPanel.add(modelField);
        assetInfoPanel.add(new JLabel("Manufacturer:"));
        assetInfoPanel.add(manufacturerField);
        assetInfoPanel.add(new JLabel("Type:"));
        assetInfoPanel.add(typeField);
        assetInfoPanel.add(new JLabel("IP Address:"));
        assetInfoPanel.add(ipAddressField);
        assetInfoPanel.add(new JLabel("Purchase Date:"));
        assetInfoPanel.add(purchaseDateField);
        assetInfoPanel.add(new JLabel("Notes:"));
        assetInfoPanel.add(new JScrollPane(notesField));

        // Add Asset Button
        JButton addAssetButton = new JButton("Add Asset");
        addAssetButton.addActionListener(e -> addAsset());

        addAssetPanel.add(employeeInfoPanel);
        addAssetPanel.add(assetInfoPanel);
        addAssetPanel.add(addAssetButton);

        return addAssetPanel;
    }

    // Method to switch to a specific panel in card layout
    private void switchToPanel(String panelName) {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, panelName);
    }

    // Method to authenticate user
    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user; // Set the current user after successful login
            setTitle("Welcome " + user.getFirstName() + " " + user.getLastName());
            remove(loginPanel);
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to add asset to database
    private void addAsset() {
        String assetTag = assetTagField.getText();
        String systemName = systemNameField.getText();
        String model = modelField.getText();
        String manufacturer = manufacturerField.getText();
        String type = typeField.getText();
        String ipAddress = ipAddressField.getText();
        String purchaseDate = purchaseDateField.getText();
        String notes = notesField.getText();
        String employeeFirstName = employeeFirstNameField.getText();
        String employeeLastName = employeeLastNameField.getText();
        String employeeEmail = employeeEmailField.getText();
        String department = (String) departmentComboBox.getSelectedItem();

        if (assetTag.isEmpty() || systemName.isEmpty() || model.isEmpty() || manufacturer.isEmpty() ||
                type.isEmpty() || ipAddress.isEmpty() || purchaseDate.isEmpty() ||
                employeeFirstName.isEmpty() || employeeLastName.isEmpty() || employeeEmail.isEmpty() || department == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = UUID.randomUUID().toString(); // Generate unique asset ID

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
            }

            JOptionPane.showMessageDialog(this, "Asset added successfully!");
            clearAssetFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding asset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Method to create View Assets Panel
    private JPanel createViewAssetPanel() {
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "View Assets"));

        // Create a table to show assets
        tableModel = new DefaultTableModel(new String[]{"Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Employee First Name", "Employee Last Name", "Employee Email", "Department"}, 0);
        assetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assetTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> viewAssets());

        viewPanel.add(scrollPane, BorderLayout.CENTER);
        viewPanel.add(refreshButton, BorderLayout.SOUTH);

        return viewPanel;
    }

    // Method to view assets from the database
    // Method to view assets from the database
    private void viewAssets() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in to view assets.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Clear existing data

        String sql;
        if ("Admin".equals(currentUser.getDepartment())) {
            // Admin sees all assets
            sql = "SELECT * FROM assets";
        } else {
            // Other users see only assets in their department
            sql = "SELECT * FROM assets WHERE department = ?";
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!"Admin".equals(currentUser.getDepartment())) {
                pstmt.setString(1, currentUser.getDepartment());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving assets: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    // Method to create Edit Asset panel
    private JPanel createEditAssetPanel() {
        JPanel editPanel = createAddAssetPanel(); // Reuse Add Asset Panel
        JButton editAssetButton = new JButton("Edit Asset");

        editAssetButton.addActionListener(e -> editAsset());

        editPanel.add(editAssetButton, BorderLayout.SOUTH);
        return editPanel;
    }

    // Method to edit an asset in the database
    private void editAsset() {
        String assetTag = assetTagField.getText();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "UPDATE assets SET systemName=?, model=?, manufacturer=?, type=?, ipAddress=?, purchaseDate=?, notes=?, employeeFirstName=?, employeeLastName=?, employeeEmail=?, department=? WHERE assetTag=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, systemNameField.getText());
                pstmt.setString(2, modelField.getText());
                pstmt.setString(3, manufacturerField.getText());
                pstmt.setString(4, typeField.getText());
                pstmt.setString(5, ipAddressField.getText());
                pstmt.setString(6, purchaseDateField.getText());
                pstmt.setString(7, notesField.getText());
                pstmt.setString(8, employeeFirstNameField.getText());
                pstmt.setString(9, employeeLastNameField.getText());
                pstmt.setString(10, employeeEmailField.getText());
                pstmt.setString(11, (String) departmentComboBox.getSelectedItem());
                pstmt.setString(12, assetTag);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Asset updated successfully!");
                    clearAssetFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Asset not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating asset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDeleteAssetPanel() {
        JPanel deletePanel = new JPanel(new BorderLayout());
        deletePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Delete Asset"));

        // Resize the Asset Tag input field
        JTextField assetTagToDeleteField = new JTextField(20);  // Resized for better visibility
        JButton deleteAssetButton = new JButton("Delete Asset");

        deleteAssetButton.addActionListener(e -> {
            String assetTag = assetTagToDeleteField.getText().trim();
            if (!assetTag.isEmpty()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete the asset with Asset Tag: " + assetTag + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    deleteAsset(assetTag);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an Asset Tag to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deletePanel.add(new JLabel("Enter Asset Tag to Delete:"), BorderLayout.NORTH);
        deletePanel.add(assetTagToDeleteField, BorderLayout.CENTER);
        deletePanel.add(deleteAssetButton, BorderLayout.SOUTH);

        return deletePanel;
    }

    // Method to delete an asset from the database
    private void deleteAsset(String assetTag) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "DELETE FROM assets WHERE assetTag = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, assetTag);

                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Asset with Asset Tag: " + assetTag + " has been successfully deleted!", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Asset with Asset Tag: " + assetTag + " not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting asset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void clearAssetFields() {
        assetTagField.setText("");
        systemNameField.setText("");
        modelField.setText("");
        manufacturerField.setText("");
        typeField.setText("");
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
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String department;

    public User(String username, String password, String firstName, String lastName, String email, String department) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
    }

    public String getUsername() {
        return username;
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

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }
}
