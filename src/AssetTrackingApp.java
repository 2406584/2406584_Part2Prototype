import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class AssetTrackingApp extends JFrame {
    private JPanel cardPanel, loginPanel, mainPanel, addAssetPanel, editAssetPanel;
    private JTextField usernameField, passwordField;
    private JTextField assetTagField, systemNameField, modelField, manufacturerField, typeField, ipAddressField, purchaseDateField;
    private JTextArea notesField;
    private JTextField employeeFirstNameField, employeeLastNameField, employeeEmailField;
    private JComboBox<String> departmentComboBox;
    private DefaultTableModel tableModel;
    private JTable assetTable;
    private User loggedInUser;
    private List<Asset> assets = new ArrayList<>();
    private Map<String, User> users = new HashMap<>();

    public AssetTrackingApp() {
        setTitle("Asset Tracking System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUsers(); // Initialize user data
        createUI(); // Create the user interface
    }

    // Create the user interface
    private void createUI() {
        cardPanel = new JPanel(new CardLayout());

        // Login Panel
        loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "Login");

        // Main Panel
        mainPanel = createMainPanel();
        cardPanel.add(mainPanel, "Main");

        add(cardPanel);
    }

    // Create Login Panel
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);

        return panel;
    }

    // Create Main Panel
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        JButton addAssetButton = new JButton("Add Asset");
        addAssetButton.addActionListener(e -> switchToPanel("Add"));

        JButton editAssetButton = new JButton("Edit Asset");
        editAssetButton.addActionListener(e -> switchToPanel("Edit"));

        JButton viewAssetsButton = new JButton("View Assets");
        viewAssetsButton.addActionListener(e -> viewAssets());

        buttonPanel.add(addAssetButton);
        buttonPanel.add(editAssetButton);
        buttonPanel.add(viewAssetsButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(createAddAssetPanel(), BorderLayout.CENTER);
        panel.add(createEditAssetPanel(), BorderLayout.SOUTH);

        return panel;
    }

    // Create Add Asset Panel
    private JPanel createAddAssetPanel() {
        addAssetPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        addAssetPanel.setBorder(BorderFactory.createTitledBorder("Add Asset"));

        assetTagField = new JTextField();
        systemNameField = new JTextField();
        modelField = new JTextField();
        manufacturerField = new JTextField();
        typeField = new JTextField();
        ipAddressField = new JTextField();
        purchaseDateField = new JTextField();
        notesField = new JTextArea(3, 20);

        employeeFirstNameField = new JTextField(loggedInUser.getFirstName());
        employeeLastNameField = new JTextField(loggedInUser.getLastName());
        employeeEmailField = new JTextField(loggedInUser.getEmail());
        departmentComboBox = new JComboBox<>(new String[]{"IT", "HR", "Finance", "Admin"}); // Placeholder options

        addAssetPanel.add(new JLabel("Asset Tag:"));
        addAssetPanel.add(assetTagField);
        addAssetPanel.add(new JLabel("System Name:"));
        addAssetPanel.add(systemNameField);
        addAssetPanel.add(new JLabel("Model:"));
        addAssetPanel.add(modelField);
        addAssetPanel.add(new JLabel("Manufacturer:"));
        addAssetPanel.add(manufacturerField);
        addAssetPanel.add(new JLabel("Type:"));
        addAssetPanel.add(typeField);
        addAssetPanel.add(new JLabel("IP Address:"));
        addAssetPanel.add(ipAddressField);
        addAssetPanel.add(new JLabel("Purchase Date:"));
        addAssetPanel.add(purchaseDateField);
        addAssetPanel.add(new JLabel("Notes:"));
        addAssetPanel.add(new JScrollPane(notesField));
        addAssetPanel.add(new JLabel("First Name:"));
        addAssetPanel.add(employeeFirstNameField);
        addAssetPanel.add(new JLabel("Last Name:"));
        addAssetPanel.add(employeeLastNameField);
        addAssetPanel.add(new JLabel("Email:"));
        addAssetPanel.add(employeeEmailField);
        addAssetPanel.add(new JLabel("Department:"));
        addAssetPanel.add(departmentComboBox);

        // Add button to add asset
        JButton addButton = new JButton("Add Asset");
        addButton.setPreferredSize(new Dimension(100, 30)); // Set smaller button size
        addButton.addActionListener(e -> addAsset());
        addAssetPanel.add(addButton);

        return addAssetPanel;
    }

    // Create Edit Asset Panel
    private JPanel createEditAssetPanel() {
        editAssetPanel = new JPanel(new BorderLayout());

        // Employee Info Panel
        JPanel employeeInfoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        employeeInfoPanel.add(new JLabel("First Name:"));
        employeeInfoPanel.add(employeeFirstNameField);
        employeeInfoPanel.add(new JLabel("Last Name:"));
        employeeInfoPanel.add(employeeLastNameField);
        employeeInfoPanel.add(new JLabel("Email:"));
        employeeInfoPanel.add(employeeEmailField);

        // Right panel for Asset Information
        JPanel assetInfoPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        assetInfoPanel.setBorder(BorderFactory.createTitledBorder("Asset Information"));

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

        // Add both panels to the main edit asset panel
        editAssetPanel.add(employeeInfoPanel, BorderLayout.NORTH);
        editAssetPanel.add(assetInfoPanel, BorderLayout.CENTER);

        // Table to display assets for editing
        String[] columnNames = {"ID", "Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Notes", "First Name", "Last Name", "Email", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0);
        assetTable = new JTable(tableModel);
        loadAssetsForCurrentUser(); // Load assets for the logged-in user's department
        assetTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedAsset();
                }
            }
        });

        editAssetPanel.add(new JScrollPane(assetTable), BorderLayout.SOUTH);

        JButton editButton = new JButton("Edit Asset");
        editButton.addActionListener(e -> editAsset());

        // Add button at the bottom of the main edit asset panel
        editAssetPanel.add(editButton, BorderLayout.SOUTH); // Add button to the panel

        return editAssetPanel;
    }

    // Load assets for the logged-in user's department
    private void loadAssetsForCurrentUser() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Asset asset : assets) {
            if (asset.getDepartment().equals(loggedInUser.getDepartment())) {
                Object[] rowData = {
                        asset.getId(),
                        asset.getAssetTag(),
                        asset.getSystemName(),
                        asset.getModel(),
                        asset.getManufacturer(),
                        asset.getType(),
                        asset.getIpAddress(),
                        asset.getPurchaseDate(),
                        asset.getNotes(),
                        asset.getEmployeeFirstName(),
                        asset.getEmployeeLastName(),
                        asset.getEmployeeEmail(),
                        asset.getDepartment()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    // Method to view assets under the user's department
    private void viewAssets() {
        loadAssetsForCurrentUser();
    }

    // Add Asset Method
    private void addAsset() {
        String assetTag = assetTagField.getText().trim();
        String systemName = systemNameField.getText().trim();
        String model = modelField.getText().trim();
        String manufacturer = manufacturerField.getText().trim();
        String type = typeField.getText().trim();
        String ipAddress = ipAddressField.getText().trim();
        String purchaseDate = purchaseDateField.getText().trim();
        String notes = notesField.getText().trim();

        // Using logged-in user's department
        String employeeFirstName = employeeFirstNameField.getText().trim();
        String employeeLastName = employeeLastNameField.getText().trim();
        String employeeEmail = employeeEmailField.getText().trim();
        String department = loggedInUser.getDepartment(); // Set department automatically

        if (assetTag.isEmpty() || systemName.isEmpty() || model.isEmpty() || manufacturer.isEmpty() || type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Asset newAsset = new Asset(UUID.randomUUID(), assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes, employeeFirstName, employeeLastName, employeeEmail, department);
        assets.add(newAsset);

        // Show confirmation dialog
        JOptionPane.showMessageDialog(this, "Asset added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Clear input fields
        clearAddAssetFields();
    }

    // Clear input fields after adding an asset
    private void clearAddAssetFields() {
        assetTagField.setText("");
        systemNameField.setText("");
        modelField.setText("");
        manufacturerField.setText("");
        typeField.setText("");
        ipAddressField.setText("");
        purchaseDateField.setText("");
        notesField.setText("");
        employeeFirstNameField.setText(loggedInUser.getFirstName()); // Reset to logged-in user info
        employeeLastNameField.setText(loggedInUser.getLastName());
        employeeEmailField.setText(loggedInUser.getEmail());
        // Department will remain as the user's department
    }

    // Edit selected asset from the table
    private void editSelectedAsset() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow >= 0) {
            UUID assetId = (UUID) tableModel.getValueAt(selectedRow, 0);
            Asset asset = findAssetById(assetId);
            if (asset != null) {
                // Load the asset details into the input fields
                assetTagField.setText(asset.getAssetTag());
                systemNameField.setText(asset.getSystemName());
                modelField.setText(asset.getModel());
                manufacturerField.setText(asset.getManufacturer());
                typeField.setText(asset.getType());
                ipAddressField.setText(asset.getIpAddress());
                purchaseDateField.setText(asset.getPurchaseDate());
                notesField.setText(asset.getNotes());
                employeeFirstNameField.setText(asset.getEmployeeFirstName());
                employeeLastNameField.setText(asset.getEmployeeLastName());
                employeeEmailField.setText(asset.getEmployeeEmail());
            }
        }
    }

    // Find asset by ID
    private Asset findAssetById(UUID assetId) {
        for (Asset asset : assets) {
            if (asset.getId().equals(assetId)) {
                return asset;
            }
        }
        return null;
    }

    // Authenticate the user
    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        loggedInUser = users.get(username);

        if (loggedInUser != null && loggedInUser.getPassword().equals(password)) {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "Main");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Initialize user data
    private void initializeUsers() {
        users.put("john.doe", new User("john.doe", "password123", "John", "Doe", "john.doe@scottishglen.com", "IT"));
        users.put("jane.smith", new User("jane.smith", "password123", "Jane", "Smith", "jane.smith@scottishglen.com", "HR"));
        users.put("mark.johnson", new User("mark.johnson", "password123", "Mark", "Johnson", "mark.johnson@scottishglen.com", "Finance"));
        users.put("emily.davis", new User("emily.davis", "password123", "Emily", "Davis", "emily.davis@scottishglen.com", "Admin"));
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssetTrackingApp app = new AssetTrackingApp();
            app.setVisible(true);
        });
    }
}

// User class
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

// Asset class
class Asset {
    private UUID id;
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

    public Asset(UUID id, String assetTag, String systemName, String model, String manufacturer, String type, String ipAddress, String purchaseDate, String notes, String employeeFirstName, String employeeLastName, String employeeEmail, String department) {
        this.id = id;
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

    public UUID getId() {
        return id;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getType() {
        return type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public String getDepartment() {
        return department;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
