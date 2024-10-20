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
    private JTextField employeeFirstNameField, employeeLastNameField, employeeEmailField, systemNameField, modelField, manufacturerField, typeField, ipAddressField, purchaseDateField, assetTagField;
    private JTextArea notesField;
    private JComboBox<String> departmentComboBox;
    private JTable assetTable;
    private DefaultTableModel tableModel;

    // Authentication fields
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginPanel, mainPanel;

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
        private String assetTag;
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
        public Asset(String assetTag, String systemName, String model, String manufacturer, String type, String ipAddress,
                     String purchaseDate, String notes, String employeeFirstName, String employeeLastName,
                     String employeeEmail, String department) {
            this.id = UUID.randomUUID().toString();  // Generate unique ID
            this.assetTag = assetTag;
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
            return new String[] {id, assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes,
                    employeeFirstName, employeeLastName, employeeEmail, department};
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
        JPanel formPanel = new JPanel(new GridLayout(13, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Asset and Employee Information"));

        // Employee Data Fields
        employeeFirstNameField = new JTextField();
        employeeLastNameField = new JTextField();
        employeeEmailField = new JTextField();

        // Asset Data Fields (Some are auto-filled from the system)
        assetTagField = new JTextField();
        systemNameField = new JTextField(getSystemName());
        modelField = new JTextField("Auto Model");
        manufacturerField = new JTextField("Auto Manufacturer");
        typeField = new JTextField("Auto Type");
        ipAddressField = new JTextField(getSystemIpAddress());
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
        formPanel.add(new JLabel("Asset Tag Number:"));
        formPanel.add(assetTagField);
        formPanel.add(new JLabel("System Name:"));
        formPanel.add(systemNameField);
        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);
        formPanel.add(new JLabel("Manufacturer:"));
        formPanel.add(manufacturerField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeField);
        formPanel.add(new JLabel("IP Address:"));
        formPanel.add(ipAddressField);
        formPanel.add(new JLabel("Purchase Date:"));
        formPanel.add(purchaseDateField);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesField));

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table for displaying asset data
        String[] columnNames = {"ID", "Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Notes", "Employee First Name", "Employee Last Name", "Employee Email", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0);
        assetTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(assetTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Asset");
        JButton logoutButton = new JButton("Logout");
        buttonPanel.add(addButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Action Listener for adding assets
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAsset();
            }
        });

        // Button Action Listener for logging out
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
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
    private void addAsset() {
        Asset asset = new Asset(
                assetTagField.getText(),
                systemNameField.getText(),
                modelField.getText(),
                manufacturerField.getText(),
                typeField.getText(),
                ipAddressField.getText(),
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

    // Function to clear input fields
    private void clearFields() {
        assetTagField.setText("");
        employeeFirstNameField.setText("");
        employeeLastNameField.setText("");
        employeeEmailField.setText("");
        purchaseDateField.setText("");
        notesField.setText("");
    }

    // Method to authenticate user
    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (users.containsKey(username) && users.get(username).equals(password)) {
            loggedInDepartment = userDepartments.get(username);
            showMainPanel();
            JOptionPane.showMessageDialog(this, "Welcome, " + loggedInDepartment + " user!");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }

    // Show login panel
    private void showLoginPanel() {
        remove(mainPanel);
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Show main panel (after login)
    private void showMainPanel() {
        remove(loginPanel);
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Method to get the system name automatically
    private String getSystemName() {
        return System.getenv("COMPUTERNAME");
    }

    // Method to get the IP address automatically
    private String getSystemIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AssetTrackingApp().setVisible(true);
            }
        });
    }
}
