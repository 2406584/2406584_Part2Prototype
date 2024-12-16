import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class AssetTrackingApp extends JFrame {
    public static final String DATABASE_URL = "jdbc:sqlite:assets.db";
    public HashMap<String, User> users;
    private static final String[] ASSET_TYPES = {"Desktop", "Laptop", "Tablet", "Mobile Phone", "Other"};
    public static User currentUser;

    // UI components
    public JTextField usernameField;
    public JPasswordField passwordField;
    public JPanel mainPanel;
    public JPanel cardPanel;
    public JLabel logoLabel;

    private static final String[] DEPARTMENTS = {"Finance", "HR", "Operations", "Sales", "IT", "Admin"};

    public AssetTrackingApp() {
        setTitle("Asset Tracking System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        users = new HashMap<>();
        initializeUsers();
        createNewDatabase();

        setLayout(new BorderLayout());
        add(createLoginPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS assets (
                        id TEXT PRIMARY KEY,
                        assetTag TEXT NOT NULL,
                        systemName TEXT NOT NULL,
                        model TEXT NOT NULL,
                        manufacturer TEXT NOT NULL,
                        type TEXT NOT NULL,
                        ipAddress TEXT NOT NULL,
                        purchaseDate TEXT NOT NULL,
                        notes TEXT,
                        employeeFirstName TEXT NOT NULL,
                        employeeLastName TEXT NOT NULL,
                        employeeEmail TEXT NOT NULL,
                        department TEXT NOT NULL
                    );
                """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeUsers() {
        users.put("admin", new User("admin", "adminPass", "Admin", "User", "admin@example.com", "Admin"));
        users.put("it_user", new User("it_user", "itPass", "IT", "User", "it.user@example.com", "IT"));
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        // Load the logo image
        logoLabel = new JLabel(new ImageIcon("resources/logo.png"));
        loginPanel.add(logoLabel, createConstraints(0, 0, 2, 1));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());

        loginPanel.add(usernameLabel, createConstraints(0, 1));
        loginPanel.add(usernameField, createConstraints(1, 1));
        loginPanel.add(passwordLabel, createConstraints(0, 2));
        loginPanel.add(passwordField, createConstraints(1, 2));
        loginPanel.add(loginButton, createConstraints(0, 3, 2, 1));

        return loginPanel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Asset Management"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton viewButton = new JButton("View Assets");
        JButton addButton = new JButton("Add Asset");
        JButton logoutButton = new JButton("Logout");

        viewButton.addActionListener(e -> switchToPanel("View"));
        addButton.addActionListener(e -> switchToPanel("Add"));
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(viewButton);
        buttonPanel.add(addButton);
        buttonPanel.add(logoutButton);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(createViewAssetPanel(), "View");
        cardPanel.add(createAddAssetPanel(), "Add");

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    private void switchToPanel(String view) {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, view);  // Show the panel corresponding to the view name
    }


    private JPanel createViewAssetPanel() {
        JPanel viewAssetPanel = new JPanel(new BorderLayout());
        viewAssetPanel.setBorder(BorderFactory.createTitledBorder("View and Manage Assets"));

        // Expanded column names for additional fields (First Name, Last Name, Purchase Date, etc.)
        String[] columnNames = {"Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Employee First Name", "Employee Last Name", "Employee Email", "Notes", "Department"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable assetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assetTable);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton deleteButton = new JButton("Delete Selected Asset");
        JButton editButton = new JButton("Edit Asset");

        refreshButton.addActionListener(e -> refreshAssetTable(tableModel));
        deleteButton.addActionListener(e -> deleteSelectedAsset(assetTable, tableModel));
        editButton.addActionListener(e -> editAsset(assetTable, tableModel));

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        viewAssetPanel.add(scrollPane, BorderLayout.CENTER);
        viewAssetPanel.add(buttonPanel, BorderLayout.SOUTH);

        return viewAssetPanel;
    }

    private JPanel createAddAssetPanel() {
        JPanel addAssetPanel = new JPanel(new GridBagLayout());
        addAssetPanel.setBorder(BorderFactory.createTitledBorder("Add Asset"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create fields for asset information
        JTextField assetTagField = new JTextField(15);
        JTextField systemNameField = new JTextField(15);
        JTextField modelField = new JTextField(15);
        JTextField manufacturerField = new JTextField(15);
        JComboBox<String> typeComboBox = new JComboBox<>(ASSET_TYPES);
        JTextField ipAddressField = new JTextField(15);
        JTextField purchaseDateField = new JTextField(15);
        JTextArea notesField = new JTextArea(3, 15);
        JScrollPane notesScrollPane = new JScrollPane(notesField);
        JTextField employeeFirstNameField = new JTextField(15);
        JTextField employeeLastNameField = new JTextField(15);
        JTextField employeeEmailField = new JTextField(15);
        JComboBox<String> departmentComboBox = new JComboBox<>(DEPARTMENTS);

        // Add components to the panel
        addAssetPanel.add(new JLabel("Asset Tag:"), gbc);
        addAssetPanel.add(assetTagField, gbc);

        gbc.gridy = 1;
        addAssetPanel.add(new JLabel("System Name:"), gbc);
        addAssetPanel.add(systemNameField, gbc);

        gbc.gridy = 2;
        addAssetPanel.add(new JLabel("Model:"), gbc);
        addAssetPanel.add(modelField, gbc);

        gbc.gridy = 3;
        addAssetPanel.add(new JLabel("Manufacturer:"), gbc);
        addAssetPanel.add(manufacturerField, gbc);

        gbc.gridy = 4;
        addAssetPanel.add(new JLabel("Type:"), gbc);
        addAssetPanel.add(typeComboBox, gbc);

        gbc.gridy = 5;
        addAssetPanel.add(new JLabel("IP Address:"), gbc);
        addAssetPanel.add(ipAddressField, gbc);

        gbc.gridy = 6;
        addAssetPanel.add(new JLabel("Purchase Date:"), gbc);
        addAssetPanel.add(purchaseDateField, gbc);

        gbc.gridy = 7;
        addAssetPanel.add(new JLabel("Notes:"), gbc);
        addAssetPanel.add(notesScrollPane, gbc);

        gbc.gridy = 8;
        addAssetPanel.add(new JLabel("Employee First Name:"), gbc);
        addAssetPanel.add(employeeFirstNameField, gbc);

        gbc.gridy = 9;
        addAssetPanel.add(new JLabel("Employee Last Name:"), gbc);
        addAssetPanel.add(employeeLastNameField, gbc);

        gbc.gridy = 10;
        addAssetPanel.add(new JLabel("Employee Email:"), gbc);
        addAssetPanel.add(employeeEmailField, gbc);

        gbc.gridy = 11;
        addAssetPanel.add(new JLabel("Department:"), gbc);
        addAssetPanel.add(departmentComboBox, gbc);

        JButton saveButton = new JButton("Add Asset");
        saveButton.addActionListener(e -> addAsset(assetTagField, systemNameField, modelField, manufacturerField, typeComboBox, ipAddressField, purchaseDateField, notesField, employeeFirstNameField, employeeLastNameField, employeeEmailField, departmentComboBox));

        gbc.gridy = 12;
        addAssetPanel.add(saveButton, gbc);

        return addAssetPanel;
    }

    private void addAsset(JTextField assetTagField, JTextField systemNameField, JTextField modelField, JTextField manufacturerField, JComboBox<String> typeComboBox, JTextField ipAddressField, JTextField purchaseDateField, JTextArea notesField, JTextField employeeFirstNameField, JTextField employeeLastNameField, JTextField employeeEmailField, JComboBox<String> departmentComboBox) {
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

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "INSERT INTO assets (id, assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes, employeeFirstName, employeeLastName, employeeEmail, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, UUID.randomUUID().toString());
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
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add asset: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAsset(JTable assetTable, DefaultTableModel tableModel) {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetTag = (String) tableModel.getValueAt(selectedRow, 0);
        String systemName = (String) tableModel.getValueAt(selectedRow, 1);
        String model = (String) tableModel.getValueAt(selectedRow, 2);

        // Open an edit dialog with the selected asset's details pre-filled
        JDialog editDialog = new JDialog(this, "Edit Asset", true);
        editDialog.setLayout(new GridLayout(0, 2));

        JTextField systemNameField = new JTextField(systemName);
        JTextField modelField = new JTextField(model);

        editDialog.add(new JLabel("System Name:"));
        editDialog.add(systemNameField);
        editDialog.add(new JLabel("Model:"));
        editDialog.add(modelField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            updateAsset(assetTag, systemNameField.getText(), modelField.getText());
            editDialog.dispose();
        });

        editDialog.add(new JLabel());
        editDialog.add(saveButton);

        editDialog.pack();
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void updateAsset(String assetTag, String systemName, String model) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "UPDATE assets SET systemName = ?, model = ? WHERE assetTag = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, systemName);
                pstmt.setString(2, model);
                pstmt.setString(3, assetTag);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Asset updated successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update asset: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAssetTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "SELECT assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, employeeFirstName, employeeLastName, employeeEmail, notes, department FROM assets";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
                            rs.getString("notes"),
                            rs.getString("department")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve assets: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedAsset(JTable assetTable, DefaultTableModel tableModel) {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetTag = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this asset?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            String sql = "DELETE FROM assets WHERE assetTag = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, assetTag);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Asset deleted successfully.");
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete asset.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete asset: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = users.get(username);

        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            usernameField.setText("");
            passwordField.setText("");
            getContentPane().removeAll();
            add(createMainPanel(), BorderLayout.CENTER);
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        currentUser = null;
        getContentPane().removeAll();
        add(createLoginPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private GridBagConstraints createConstraints(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private GridBagConstraints createConstraints(int x, int y, int width, int height) {
        GridBagConstraints gbc = createConstraints(x, y);
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssetTrackingApp().setVisible(true));
    }

    static class User {
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
    }
}
