import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AssetTrackingApp extends JFrame {
    // User and asset management variables
    private HashMap<String, User> users;
    private ArrayList<Asset> assets;
    private DefaultTableModel tableModel;

    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginPanel, mainPanel, cardPanel;
    private JTextField assetTagField, systemNameField, modelField, manufacturerField, typeField, ipAddressField, purchaseDateField, employeeFirstNameField, employeeLastNameField, employeeEmailField;
    private JTextArea notesField;
    private JTable assetTable;
    private JComboBox<String> departmentComboBox;

    private static final String[] DEPARTMENTS = {"Finance", "Human Resources", "Operations", "Sales", "Information Technology", "Admin"};

    public AssetTrackingApp() {
        setTitle("Asset Tracking System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        users = new HashMap<>();
        assets = new ArrayList<>();

        // Sample users for authentication
        initializeUsers();

        // Create UI components
        loginPanel = createLoginPanel();
        mainPanel = createMainPanel();

        // Set layout and add components
        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);
    }

    private void initializeUsers() {
        users.put("admin", new User("admin", "adminPass", "Admin", "User", "admin@scottishglen.com"));
        users.put("finance_user", new User("finance_user", "financePass", "Finance", "User", "finance.user@scottishglen.com"));
        users.put("hr_user", new User("hr_user", "hrPass", "HR", "User", "hr.user@scottishglen.com"));
        users.put("operations_user", new User("operations_user", "operationsPass", "Operations", "User", "operations.user@scottishglen.com"));
        users.put("sales_user", new User("sales_user", "salesPass", "Sales", "User", "sales.user@scottishglen.com"));
        users.put("it_user", new User("it_user", "itPass", "IT", "User", "it.user@scottishglen.com"));
    }

    // Method to create login panel
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Login"));

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

        return panel;
    }

    // Method to create main panel
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Asset Management"));

        // Button panel for asset management
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Asset");
        JButton deleteButton = new JButton("Delete Asset");
        JButton editButton = new JButton("Edit Asset");
        JButton logoutButton = new JButton("Logout");

        addButton.addActionListener(e -> switchToPanel("Add"));
        deleteButton.addActionListener(e -> switchToPanel("Delete"));
        editButton.addActionListener(e -> switchToPanel("Edit"));
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(logoutButton); // Add logout button

        panel.add(buttonPanel, BorderLayout.NORTH);
        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(createAddAssetPanel(), "Add");
        cardPanel.add(createDeleteAssetPanel(), "Delete");
        cardPanel.add(createEditAssetPanel(), "Edit");

        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    // Method to create Add Asset panel
    private JPanel createAddAssetPanel() {
        JPanel addAssetPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        addAssetPanel.setBorder(BorderFactory.createTitledBorder("Add Asset Information"));

        // Left panel for Employee Information
        JPanel employeeInfoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));

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

        // Add both panels to the main add asset panel
        addAssetPanel.add(employeeInfoPanel);
        addAssetPanel.add(assetInfoPanel);

        JButton addButton = new JButton("Add Asset");
        addButton.addActionListener(e -> addAsset());

        // Add button at the bottom of the main add asset panel
        addAssetPanel.add(addButton); // Add button to the panel

        return addAssetPanel;
    }

    // Method to create Delete Asset panel
    private JPanel createDeleteAssetPanel() {
        JPanel deleteAssetPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Asset Tag", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Notes", "First Name", "Last Name", "Email", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0);
        assetTable = new JTable(tableModel);
        deleteAssetPanel.add(new JScrollPane(assetTable), BorderLayout.CENTER);

        JButton deleteButton = new JButton("Delete Selected Asset");
        deleteButton.addActionListener(e -> deleteAsset());
        deleteAssetPanel.add(deleteButton, BorderLayout.SOUTH);

        return deleteAssetPanel;
    }

    // Method to create Edit Asset panel
    private JPanel createEditAssetPanel() {
        JPanel editAssetPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        editAssetPanel.setBorder(BorderFactory.createTitledBorder("Edit Asset Information"));

        // Fields for editing
        assetTagField = new JTextField();
        systemNameField = new JTextField();
        modelField = new JTextField();
        manufacturerField = new JTextField();
        typeField = new JTextField();
        ipAddressField = new JTextField();
        purchaseDateField = new JTextField();
        notesField = new JTextArea(3, 20);
        employeeFirstNameField = new JTextField();
        employeeLastNameField = new JTextField();
        employeeEmailField = new JTextField();
        departmentComboBox = new JComboBox<>(DEPARTMENTS);

        JButton editButton = new JButton("Edit Asset");
        editButton.addActionListener(e -> editAsset());

        editAssetPanel.add(new JLabel("Asset Tag:"));
        editAssetPanel.add(assetTagField);
        editAssetPanel.add(new JLabel("System Name:"));
        editAssetPanel.add(systemNameField);
        editAssetPanel.add(new JLabel("Model:"));
        editAssetPanel.add(modelField);
        editAssetPanel.add(new JLabel("Manufacturer:"));
        editAssetPanel.add(manufacturerField);
        editAssetPanel.add(new JLabel("Type:"));
        editAssetPanel.add(typeField);
        editAssetPanel.add(new JLabel("IP Address:"));
        editAssetPanel.add(ipAddressField);
        editAssetPanel.add(new JLabel("Purchase Date:"));
        editAssetPanel.add(purchaseDateField);
        editAssetPanel.add(new JLabel("Notes:"));
        editAssetPanel.add(new JScrollPane(notesField));
        editAssetPanel.add(new JLabel("First Name:"));
        editAssetPanel.add(employeeFirstNameField);
        editAssetPanel.add(new JLabel("Last Name:"));
        editAssetPanel.add(employeeLastNameField);
        editAssetPanel.add(new JLabel("Email:"));
        editAssetPanel.add(employeeEmailField);
        editAssetPanel.add(new JLabel("Department:"));
        editAssetPanel.add(departmentComboBox);
        editAssetPanel.add(new JLabel("")); // Placeholder for grid
        editAssetPanel.add(editButton);

        return editAssetPanel;
    }

    // Method to switch between panels
    private void switchToPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, panelName);
    }

    // Method to authenticate user
    private void authenticate() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
            showMainPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Show main panel after successful login
    private void showMainPanel() {
        remove(loginPanel);
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Logout method to return to login panel
    private void logout() {
        remove(mainPanel);
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        JOptionPane.showMessageDialog(this, "You have been logged out.");
    }

    // Add asset to the list
    private void addAsset() {
        String assetTag = assetTagField.getText();
        String systemName = systemNameField.getText();
        String model = modelField.getText();
        String manufacturer = manufacturerField.getText();
        String type = typeField.getText();
        String ipAddress = ipAddressField.getText();
        String purchaseDate = purchaseDateField.getText();
        String notes = notesField.getText();
        String firstName = employeeFirstNameField.getText();
        String lastName = employeeLastNameField.getText();
        String email = employeeEmailField.getText();
        String department = (String) departmentComboBox.getSelectedItem();

        // Add the asset to the table
        Object[] assetData = {UUID.randomUUID(), assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes, firstName, lastName, email, department};
        tableModel.addRow(assetData);

        // Clear input fields after adding
        clearInputFields();
    }

    // Delete selected asset from the list
    private void deleteAsset() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Asset deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an asset to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Edit selected asset information
    private void editAsset() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.setValueAt(assetTagField.getText(), selectedRow, 1);
            tableModel.setValueAt(systemNameField.getText(), selectedRow, 2);
            tableModel.setValueAt(modelField.getText(), selectedRow, 3);
            tableModel.setValueAt(manufacturerField.getText(), selectedRow, 4);
            tableModel.setValueAt(typeField.getText(), selectedRow, 5);
            tableModel.setValueAt(ipAddressField.getText(), selectedRow, 6);
            tableModel.setValueAt(purchaseDateField.getText(), selectedRow, 7);
            tableModel.setValueAt(notesField.getText(), selectedRow, 8);
            tableModel.setValueAt(employeeFirstNameField.getText(), selectedRow, 9);
            tableModel.setValueAt(employeeLastNameField.getText(), selectedRow, 10);
            tableModel.setValueAt(employeeEmailField.getText(), selectedRow, 11);
            tableModel.setValueAt(departmentComboBox.getSelectedItem(), selectedRow, 12);

            JOptionPane.showMessageDialog(this, "Asset updated successfully.");
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an asset to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clear input fields
    private void clearInputFields() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssetTrackingApp app = new AssetTrackingApp();
            app.setVisible(true);
        });
    }
}

// User class to hold user details
class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    public User(String username, String password, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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
}

// Asset class to hold asset details (optional, can be omitted if not used)
class Asset {
    // Define asset properties here (optional, can be omitted if not used)
}
