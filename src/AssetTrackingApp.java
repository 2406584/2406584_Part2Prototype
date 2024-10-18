import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

public class AssetTrackingApp extends JFrame {
    // Asset Data Fields
    private JTextField employeeFirstNameField, employeeLastNameField, employeeEmailField, systemNameField, modelField, manufacturerField, typeField, ipAddressField, purchaseDateField;
    private JTextArea notesField;
    private JComboBox<String> departmentComboBox;
    private JTable assetTable;
    private DefaultTableModel tableModel;

    // Static department list
    private static final String[] DEPARTMENTS = {"Finance", "Human Resources", "Operations", "Sales", "Information Technology"};

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
            return new String[] {id, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes,
                    employeeFirstName, employeeLastName, employeeEmail, department};
        }

        public String getId() {
            return id;
        }

        // Getter and Setter methods could be added here if needed
    }

    private java.util.List<Asset> assets = new ArrayList<>();  // List to store all assets

    public AssetTrackingApp() {
        // Window setup
        setTitle("Asset Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Form Panel (For Adding Assets)
        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Asset and Employee Information"));

        // Employee Data Fields
        employeeFirstNameField = new JTextField();
        employeeLastNameField = new JTextField();
        employeeEmailField = new JTextField();

        // Asset Data Fields (Some are auto-filled from the system)
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
        String[] columnNames = {"ID", "System Name", "Model", "Manufacturer", "Type", "IP Address", "Purchase Date", "Notes", "Employee First Name", "Employee Last Name", "Employee Email", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0);
        assetTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(assetTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Asset");
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Action Listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAsset();
            }
        });
    }

    // Function to add asset
    private void addAsset() {
        Asset asset = new Asset(
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
        employeeFirstNameField.setText("");
        employeeLastNameField.setText("");
        employeeEmailField.setText("");
        purchaseDateField.setText("");
        notesField.setText("");
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

    // main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AssetTrackingApp().setVisible(true);
            }
        });
    }
}
