import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AssetTrackingApp extends JFrame {
    // Asset Data Fields
    private JTextField employeeFirstNameField, employeeLastNameField, employeeEmailField, purchaseDateField;
    private JTextArea notesField;
    private JTable assetTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> departmentComboBox;

    // Authentication fields
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginPanel, mainPanel;

    // Buttons
    private JButton addButton, logoutButton, viewAssetsButton, editAssetButton, deleteAssetButton;

    // Static department list
    private static final String[] DEPARTMENTS = {"Finance", "Human Resources", "Operations", "Sales", "Information Technology"};

    // Example users for authentication
    private static final HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, String> userDepartments = new HashMap<>();

    static {
        // Adding example users with their passwords
        users.put("admin", "adminpass");
        users.put("finance_user", "financepass");
        users.put("hr_user", "hrpass");
        users.put("operations_user", "operationspass");
        users.put("sales_user", "salespass");
        users.put("it_user", "itpass");

        // Mapping users to their departments
        userDepartments.put("admin", "Admin");
        userDepartments.put("finance_user", "Finance");
        userDepartments.put("hr_user", "Human Resources");
        userDepartments.put("operations_user", "Operations");
        userDepartments.put("sales_user", "Sales");
        userDepartments.put("it_user", "Information Technology");
    }

    private String loggedInDepartment;

    // Asset class representing individual assets
    static class Asset {
        private String id;  // Unique identifier (UUID)
        private String systemName;
        private String model;
        private String manufacturer;
        private String type;
        private String ipAddress;
        private String purchaseDate;
        private String notes;
        private String employeeFirstName;
        private String employeeLastName;
        private String employeeEmail;
        private String department;

        // Constructor
        public Asset(String systemName, String model, String manufacturer, String type, String ipAddress,
                     String purchaseDate, String notes, String employeeFirstName, String employeeLastName,
                     String employeeEmail, String department) {
            this.id = UUID.randomUUID().toString();  // Generate unique ID
            this.systemName = systemName;
            this.model = model;
            this.manufacturer = manufacturer;
            this.type = type;
            this.ipAddress = ipAddress;
            this.purchaseDate = purchaseDate;
            this.notes = notes;
            this.employeeFirstName = employeeFirstName;
            this.employeeLastName = employeeLastName;
            this.employeeEmail = employeeEmail;
            this.department = department;
        }

        public String[] toArray() {
            return new String[]{id, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes,
                    employeeFirstName, employeeLastName, employeeEmail, department};
        }

        public String getDepartment() {
            return department;
        }
    }

    private java.util.List<Asset> assets = new ArrayList<>();  // List to store all assets

    public AssetTrackingApp() {
        // Window setup
        setTitle("Asset Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Authentication Panel (Login)
        loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.CENTER);

        // Main Panel (After login)
        mainPanel = new JPanel(new BorderLayout());

        // Form Panel (For Adding Assets)
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Asset and Employee Information"));

        // Employee Data Fields
        employeeFirstNameField = new JTextField();
        employeeLastNameField = new JTextField();
        employeeEmailField = new JTextField();

        // Asset Data Fields (Auto-filled from the system)
        String systemName = getSystemName();
        String ipAddress = getSystemIpAddress();
        String model = "Auto Model";
        String manufacturer = "Auto Manufacturer";
        String type = "Auto Type";

        purchaseDateField = new JTextField();
        notesField = new JTextArea(3, 20);

        // Department Combo Box (Pre-defined, static)
        departmentComboBox = new JComboBox<>(DEPARTMENTS);

        // Add form fields to the panel
        formPanel.add(new JLabel("Employee First Name:"));
        formPanel.add(employeeFirstNameField);
        formPanel.add(new JLabel("Employee Last Name:"));
        formPanel.add(employeeLastNameField);
        formPanel.add(new JLabel("Employee Email:"));
        formPanel.add(employeeEmailField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(departmentComboBox);
        formPanel.add(new JLabel("System Name: " + systemName));
        formPanel.add(new JLabel("IP Address: " + ipAddress));
        formPanel.add(new JLabel("Model: " + model));
        formPanel.add(new JLabel("Manufacturer: " + manufacturer));
        formPanel.add(new JLabel("Type: " + type));
        formPanel.add(new JLabel("Purchase Date:"));
        formPanel.add(purchaseDateField);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesField));

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table for displaying asset data
        String[] columnNames = {"ID", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Notes", "Employee First Name", "Employee Last Name", "Employee Email", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0);
        assetTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(assetTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Asset");
        logoutButton = new JButton("Logout");
        viewAssetsButton = new JButton("View My Department's Assets");
        editAssetButton = new JButton("Edit Asset");
        deleteAssetButton = new JButton("Delete Asset");

        buttonPanel.add(addButton);
        buttonPanel.add(viewAssetsButton);
        buttonPanel.add(editAssetButton);
        buttonPanel.add(deleteAssetButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Initially hide the buttons until login
        addButton.setVisible(false);
        logoutButton.setVisible(false);
        viewAssetsButton.setVisible(false);
        editAssetButton.setVisible(false);
        deleteAssetButton.setVisible(false);

        // Button Action Listener for adding assets
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAsset(systemName, model, manufacturer, type, ipAddress);
            }
        });

        // Button Action Listener for editing assets
        editAssetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editAsset();
            }
        });

        // Button Action Listener for deleting assets
        deleteAssetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAsset();
            }
        });

        // Button Action Listener for logging out
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
            }
        });

        // Button Action Listener for viewing assets by department
        viewAssetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAssetsByDepartment();
            }
        });

        // Login Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });
    }

    // Function to add asset
    private void addAsset(String systemName, String model, String manufacturer, String type, String ipAddress) {
        Asset asset = new Asset(
                systemName,
                model,
                manufacturer,
                type,
                ipAddress,
                purchaseDateField.getText(),
                notesField.getText(),
                employeeFirstNameField.getText(),
                employeeLastNameField.getText(),
                employeeEmailField.getText(),
                departmentComboBox.getSelectedItem().toString()
        );
        assets.add(asset);
        tableModel.addRow(asset.toArray());  // Add to the table
        clearFields();
    }

    // Function to edit an asset
    private void editAsset() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow >= 0) {
            Asset asset = assets.get(selectedRow);
            asset.employeeFirstName = employeeFirstNameField.getText();
            asset.employeeLastName = employeeLastNameField.getText();
            asset.employeeEmail = employeeEmailField.getText();
            asset.purchaseDate = purchaseDateField.getText();
            asset.notes = notesField.getText();
            tableModel.setValueAt(asset.employeeFirstName, selectedRow, 8);
            tableModel.setValueAt(asset.employeeLastName, selectedRow, 9);
            tableModel.setValueAt(asset.employeeEmail, selectedRow, 10);
            tableModel.setValueAt(asset.purchaseDate, selectedRow, 6);
            tableModel.setValueAt(asset.notes, selectedRow, 7);
        } else {
            JOptionPane.showMessageDialog(this, "Select an asset to edit");
        }
    }

    // Function to delete an asset
    private void deleteAsset() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow >= 0) {
            assets.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Select an asset to delete");
        }
    }

    // Filter assets by department
    private void filterAssetsByDepartment() {
        tableModel.setRowCount(0);  // Clear the table
        for (Asset asset : assets) {
            if (asset.getDepartment().equals(loggedInDepartment) || loggedInDepartment.equals("Admin")) {
                tableModel.addRow(asset.toArray());
            }
        }
    }

    // Show login panel
    private void showLoginPanel() {
        setTitle("Login");
        mainPanel.setVisible(false);
        loginPanel.setVisible(true);
        addButton.setVisible(false);
        logoutButton.setVisible(false);
        viewAssetsButton.setVisible(false);
        editAssetButton.setVisible(false);
        deleteAssetButton.setVisible(false);
    }

    // Show main panel
    private void showMainPanel() {
        setTitle("Asset Tracking System");
        loginPanel.setVisible(false);
        mainPanel.setVisible(true);
        addButton.setVisible(true);
        logoutButton.setVisible(true);
        viewAssetsButton.setVisible(true);
        editAssetButton.setVisible(true);
        deleteAssetButton.setVisible(true);
    }

    // Authenticate the user
    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (users.containsKey(username) && users.get(username).equals(password)) {
            loggedInDepartment = userDepartments.get(username);
            showMainPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid login credentials");
        }
    }

    // Utility to clear input fields
    private void clearFields() {
        employeeFirstNameField.setText("");
        employeeLastNameField.setText("");
        employeeEmailField.setText("");
        purchaseDateField.setText("");
        notesField.setText("");
    }

    // Retrieve the system's name
    private String getSystemName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    // Retrieve the system's IP address
    private String getSystemIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssetTrackingApp app = new AssetTrackingApp();
            app.setVisible(true);
        });
    }
}
