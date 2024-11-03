<html>
<head>
<title>AssetTrackingApp.java</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">
.s0 { color: #cf8e6d;}
.s1 { color: #bcbec4;}
.s2 { color: #bcbec4;}
.s3 { color: #7a7e85;}
.s4 { color: #6aab73;}
.s5 { color: #2aacb8;}
</style>
</head>
<body bgcolor="#1e1f22">
<table CELLSPACING=0 CELLPADDING=5 COLS=1 WIDTH="100%" BGCOLOR="#606060" >
<tr><td><center>
<font face="Arial, Helvetica" color="#000000">
AssetTrackingApp.java</font>
</center></td></tr></table>
<pre><span class="s0">import </span><span class="s1">javax</span><span class="s2">.</span><span class="s1">swing</span><span class="s2">.*;</span>
<span class="s0">import </span><span class="s1">javax</span><span class="s2">.</span><span class="s1">swing</span><span class="s2">.</span><span class="s1">table</span><span class="s2">.</span><span class="s1">DefaultTableModel</span><span class="s2">;</span>
<span class="s0">import </span><span class="s1">java</span><span class="s2">.</span><span class="s1">awt</span><span class="s2">.*;</span>
<span class="s0">import </span><span class="s1">java</span><span class="s2">.</span><span class="s1">awt</span><span class="s2">.</span><span class="s1">event</span><span class="s2">.</span><span class="s1">ActionEvent</span><span class="s2">;</span>
<span class="s0">import </span><span class="s1">java</span><span class="s2">.</span><span class="s1">awt</span><span class="s2">.</span><span class="s1">event</span><span class="s2">.</span><span class="s1">ActionListener</span><span class="s2">;</span>
<span class="s0">import </span><span class="s1">java</span><span class="s2">.</span><span class="s1">sql</span><span class="s2">.*;</span>
<span class="s0">import </span><span class="s1">java</span><span class="s2">.</span><span class="s1">util</span><span class="s2">.</span><span class="s1">HashMap</span><span class="s2">;</span>
<span class="s0">import </span><span class="s1">java</span><span class="s2">.</span><span class="s1">util</span><span class="s2">.</span><span class="s1">UUID</span><span class="s2">;</span>

<span class="s0">public class </span><span class="s1">AssetTrackingApp </span><span class="s0">extends </span><span class="s1">JFrame </span><span class="s2">{</span>
    <span class="s3">// User and asset management variables</span>
    <span class="s0">public </span><span class="s1">HashMap</span><span class="s2">&lt;</span><span class="s1">String</span><span class="s2">, </span><span class="s1">User</span><span class="s2">&gt; </span><span class="s1">users</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">DefaultTableModel tableModel</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">User currentUser</span><span class="s2">;  </span><span class="s3">// Stores the logged-in user</span>


    <span class="s3">// UI components</span>
    <span class="s0">private </span><span class="s1">JTextField usernameField</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">JPasswordField passwordField</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">JPanel loginPanel</span><span class="s2">, </span><span class="s1">mainPanel</span><span class="s2">, </span><span class="s1">cardPanel</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">JTextField assetTagField</span><span class="s2">, </span><span class="s1">systemNameField</span><span class="s2">, </span><span class="s1">modelField</span><span class="s2">, </span><span class="s1">manufacturerField</span><span class="s2">, </span><span class="s1">typeField</span><span class="s2">, </span><span class="s1">ipAddressField</span><span class="s2">, </span><span class="s1">purchaseDateField</span><span class="s2">, </span><span class="s1">employeeFirstNameField</span><span class="s2">, </span><span class="s1">employeeLastNameField</span><span class="s2">, </span><span class="s1">employeeEmailField</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">JTextArea notesField</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">JTable assetTable</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">JComboBox</span><span class="s2">&lt;</span><span class="s1">String</span><span class="s2">&gt; </span><span class="s1">departmentComboBox</span><span class="s2">;</span>

    <span class="s0">private static final </span><span class="s1">String</span><span class="s2">[] </span><span class="s1">DEPARTMENTS </span><span class="s2">= {</span><span class="s4">&quot;Finance&quot;</span><span class="s2">, </span><span class="s4">&quot;Human Resources&quot;</span><span class="s2">, </span><span class="s4">&quot;Operations&quot;</span><span class="s2">, </span><span class="s4">&quot;Sales&quot;</span><span class="s2">, </span><span class="s4">&quot;Information Technology&quot;</span><span class="s2">, </span><span class="s4">&quot;Admin&quot;</span><span class="s2">};</span>

    <span class="s0">private static final </span><span class="s1">String DATABASE_URL </span><span class="s2">= </span><span class="s4">&quot;jdbc:sqlite:assets.db&quot;</span><span class="s2">;</span>

    <span class="s0">public </span><span class="s1">AssetTrackingApp</span><span class="s2">() {</span>
        <span class="s1">setTitle</span><span class="s2">(</span><span class="s4">&quot;Asset Tracking System&quot;</span><span class="s2">);</span>
        <span class="s1">setSize</span><span class="s2">(</span><span class="s5">800</span><span class="s2">, </span><span class="s5">600</span><span class="s2">);</span>
        <span class="s1">setDefaultCloseOperation</span><span class="s2">(</span><span class="s1">EXIT_ON_CLOSE</span><span class="s2">);</span>
        <span class="s1">setLocationRelativeTo</span><span class="s2">(</span><span class="s0">null</span><span class="s2">);</span>

        <span class="s1">users </span><span class="s2">= </span><span class="s0">new </span><span class="s1">HashMap</span><span class="s2">&lt;&gt;();</span>
        <span class="s1">initializeUsers</span><span class="s2">();</span>

        <span class="s3">// Create the database and assets table</span>
        <span class="s1">createNewDatabase</span><span class="s2">();</span>

        <span class="s3">// Create UI components</span>
        <span class="s1">loginPanel </span><span class="s2">= </span><span class="s1">createLoginPanel</span><span class="s2">();</span>
        <span class="s1">mainPanel </span><span class="s2">= </span><span class="s1">createMainPanel</span><span class="s2">();</span>

        <span class="s3">// Set layout and add components</span>
        <span class="s1">setLayout</span><span class="s2">(</span><span class="s0">new </span><span class="s1">BorderLayout</span><span class="s2">());</span>
        <span class="s1">add</span><span class="s2">(</span><span class="s1">loginPanel</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">CENTER</span><span class="s2">);</span>
    <span class="s2">}</span>

    <span class="s0">private void </span><span class="s1">createNewDatabase</span><span class="s2">() {</span>
        <span class="s0">try </span><span class="s2">(</span><span class="s1">Connection conn </span><span class="s2">= </span><span class="s1">DriverManager</span><span class="s2">.</span><span class="s1">getConnection</span><span class="s2">(</span><span class="s1">DATABASE_URL</span><span class="s2">)) {</span>
            <span class="s0">if </span><span class="s2">(</span><span class="s1">conn </span><span class="s2">!= </span><span class="s0">null</span><span class="s2">) {</span>
                <span class="s1">System</span><span class="s2">.</span><span class="s1">out</span><span class="s2">.</span><span class="s1">println</span><span class="s2">(</span><span class="s4">&quot;Connection to SQLite has been established.&quot;</span><span class="s2">);</span>

                <span class="s3">// Create a new table</span>
                <span class="s1">String sql </span><span class="s2">= </span><span class="s4">&quot;CREATE TABLE IF NOT EXISTS assets (</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; id TEXT PRIMARY KEY,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; assetTag TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; systemName TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; model TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; manufacturer TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; type TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; ipAddress TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; purchaseDate TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; notes TEXT,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; employeeFirstName TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; employeeLastName TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; employeeEmail TEXT NOT NULL,</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot; department TEXT NOT NULL</span><span class="s0">\n</span><span class="s4">&quot;</span>
                        <span class="s2">+ </span><span class="s4">&quot;);&quot;</span><span class="s2">;</span>

                <span class="s0">try </span><span class="s2">(</span><span class="s1">Statement stmt </span><span class="s2">= </span><span class="s1">conn</span><span class="s2">.</span><span class="s1">createStatement</span><span class="s2">()) {</span>
                    <span class="s1">stmt</span><span class="s2">.</span><span class="s1">execute</span><span class="s2">(</span><span class="s1">sql</span><span class="s2">);</span>
                    <span class="s1">System</span><span class="s2">.</span><span class="s1">out</span><span class="s2">.</span><span class="s1">println</span><span class="s2">(</span><span class="s4">&quot;Table 'assets' has been created.&quot;</span><span class="s2">);</span>
                <span class="s2">} </span><span class="s0">catch </span><span class="s2">(</span><span class="s1">SQLException e</span><span class="s2">) {</span>
                    <span class="s1">System</span><span class="s2">.</span><span class="s1">out</span><span class="s2">.</span><span class="s1">println</span><span class="s2">(</span><span class="s1">e</span><span class="s2">.</span><span class="s1">getMessage</span><span class="s2">());</span>
                <span class="s2">}</span>
            <span class="s2">}</span>
        <span class="s2">} </span><span class="s0">catch </span><span class="s2">(</span><span class="s1">SQLException e</span><span class="s2">) {</span>
            <span class="s1">System</span><span class="s2">.</span><span class="s1">out</span><span class="s2">.</span><span class="s1">println</span><span class="s2">(</span><span class="s1">e</span><span class="s2">.</span><span class="s1">getMessage</span><span class="s2">());</span>
        <span class="s2">}</span>
    <span class="s2">}</span>

    <span class="s0">private void </span><span class="s1">initializeUsers</span><span class="s2">() {</span>
        <span class="s1">users</span><span class="s2">.</span><span class="s1">put</span><span class="s2">(</span><span class="s4">&quot;admin&quot;</span><span class="s2">, </span><span class="s0">new </span><span class="s1">User</span><span class="s2">(</span><span class="s4">&quot;admin&quot;</span><span class="s2">, </span><span class="s4">&quot;adminPass&quot;</span><span class="s2">, </span><span class="s4">&quot;Admin&quot;</span><span class="s2">, </span><span class="s4">&quot;User&quot;</span><span class="s2">, </span><span class="s4">&quot;admin@scottishglen.com&quot;</span><span class="s2">));</span>
        <span class="s1">users</span><span class="s2">.</span><span class="s1">put</span><span class="s2">(</span><span class="s4">&quot;finance_user&quot;</span><span class="s2">, </span><span class="s0">new </span><span class="s1">User</span><span class="s2">(</span><span class="s4">&quot;finance_user&quot;</span><span class="s2">, </span><span class="s4">&quot;financePass&quot;</span><span class="s2">, </span><span class="s4">&quot;Finance&quot;</span><span class="s2">, </span><span class="s4">&quot;User&quot;</span><span class="s2">, </span><span class="s4">&quot;finance.user@scottishglen.com&quot;</span><span class="s2">));</span>
        <span class="s1">users</span><span class="s2">.</span><span class="s1">put</span><span class="s2">(</span><span class="s4">&quot;hr_user&quot;</span><span class="s2">, </span><span class="s0">new </span><span class="s1">User</span><span class="s2">(</span><span class="s4">&quot;hr_user&quot;</span><span class="s2">, </span><span class="s4">&quot;hrPass&quot;</span><span class="s2">, </span><span class="s4">&quot;HR&quot;</span><span class="s2">, </span><span class="s4">&quot;User&quot;</span><span class="s2">, </span><span class="s4">&quot;hr.user@scottishglen.com&quot;</span><span class="s2">));</span>
        <span class="s1">users</span><span class="s2">.</span><span class="s1">put</span><span class="s2">(</span><span class="s4">&quot;operations_user&quot;</span><span class="s2">, </span><span class="s0">new </span><span class="s1">User</span><span class="s2">(</span><span class="s4">&quot;operations_user&quot;</span><span class="s2">, </span><span class="s4">&quot;operationsPass&quot;</span><span class="s2">, </span><span class="s4">&quot;Operations&quot;</span><span class="s2">, </span><span class="s4">&quot;User&quot;</span><span class="s2">, </span><span class="s4">&quot;operations.user@scottishglen.com&quot;</span><span class="s2">));</span>
        <span class="s1">users</span><span class="s2">.</span><span class="s1">put</span><span class="s2">(</span><span class="s4">&quot;sales_user&quot;</span><span class="s2">, </span><span class="s0">new </span><span class="s1">User</span><span class="s2">(</span><span class="s4">&quot;sales_user&quot;</span><span class="s2">, </span><span class="s4">&quot;salesPass&quot;</span><span class="s2">, </span><span class="s4">&quot;Sales&quot;</span><span class="s2">, </span><span class="s4">&quot;User&quot;</span><span class="s2">, </span><span class="s4">&quot;sales.user@scottishglen.com&quot;</span><span class="s2">));</span>
        <span class="s1">users</span><span class="s2">.</span><span class="s1">put</span><span class="s2">(</span><span class="s4">&quot;it_user&quot;</span><span class="s2">, </span><span class="s0">new </span><span class="s1">User</span><span class="s2">(</span><span class="s4">&quot;it_user&quot;</span><span class="s2">, </span><span class="s4">&quot;itPass&quot;</span><span class="s2">, </span><span class="s4">&quot;IT&quot;</span><span class="s2">, </span><span class="s4">&quot;User&quot;</span><span class="s2">, </span><span class="s4">&quot;it.user@scottishglen.com&quot;</span><span class="s2">));</span>
    <span class="s2">}</span>

    <span class="s3">// Method to create login panel</span>
    <span class="s0">private </span><span class="s1">JPanel createLoginPanel</span><span class="s2">() {</span>
        <span class="s1">JPanel panel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">GridLayout</span><span class="s2">(</span><span class="s5">3</span><span class="s2">, </span><span class="s5">2</span><span class="s2">, </span><span class="s5">5</span><span class="s2">, </span><span class="s5">5</span><span class="s2">));</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;Login&quot;</span><span class="s2">));</span>

        <span class="s1">usernameField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">passwordField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPasswordField</span><span class="s2">();</span>

        <span class="s1">JButton loginButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Login&quot;</span><span class="s2">);</span>
        <span class="s1">loginButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; authenticate</span><span class="s2">());</span>

        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Username:&quot;</span><span class="s2">));</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">usernameField</span><span class="s2">);</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Password:&quot;</span><span class="s2">));</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">passwordField</span><span class="s2">);</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">));</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">loginButton</span><span class="s2">);</span>

        <span class="s3">// Styling for buttons</span>
        <span class="s1">loginButton</span><span class="s2">.</span><span class="s1">setBackground</span><span class="s2">(</span><span class="s1">Color</span><span class="s2">.</span><span class="s1">BLUE</span><span class="s2">);</span>
        <span class="s1">loginButton</span><span class="s2">.</span><span class="s1">setForeground</span><span class="s2">(</span><span class="s1">Color</span><span class="s2">.</span><span class="s1">WHITE</span><span class="s2">);</span>
        <span class="s1">loginButton</span><span class="s2">.</span><span class="s1">setFocusPainted</span><span class="s2">(</span><span class="s0">false</span><span class="s2">);</span>

        <span class="s0">return </span><span class="s1">panel</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s3">// Integrate panels into main layout</span>
    <span class="s0">private </span><span class="s1">JPanel createMainPanel</span><span class="s2">() {</span>
        <span class="s1">JPanel panel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">BorderLayout</span><span class="s2">());</span>
        <span class="s1">panel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;Asset Management&quot;</span><span class="s2">));</span>

        <span class="s3">// Button panel for asset management</span>
        <span class="s1">JPanel buttonPanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">FlowLayout</span><span class="s2">());</span>
        <span class="s1">JButton addButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Add Asset&quot;</span><span class="s2">);</span>
        <span class="s1">JButton editButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Edit Asset&quot;</span><span class="s2">);</span>
        <span class="s1">JButton deleteButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Delete Asset&quot;</span><span class="s2">);</span>
        <span class="s1">JButton viewButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;View Assets&quot;</span><span class="s2">);</span>
        <span class="s1">JButton logoutButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Logout&quot;</span><span class="s2">);</span>

        <span class="s1">addButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; switchToPanel</span><span class="s2">(</span><span class="s4">&quot;Add&quot;</span><span class="s2">));</span>
        <span class="s1">editButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; switchToPanel</span><span class="s2">(</span><span class="s4">&quot;Edit&quot;</span><span class="s2">));</span>
        <span class="s1">deleteButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; switchToPanel</span><span class="s2">(</span><span class="s4">&quot;Delete&quot;</span><span class="s2">));</span>
        <span class="s1">viewButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; switchToPanel</span><span class="s2">(</span><span class="s4">&quot;View&quot;</span><span class="s2">));</span>
        <span class="s1">logoutButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; logout</span><span class="s2">());</span>

        <span class="s3">// Styling buttons</span>
        <span class="s1">styleButton</span><span class="s2">(</span><span class="s1">addButton</span><span class="s2">);</span>
        <span class="s1">styleButton</span><span class="s2">(</span><span class="s1">editButton</span><span class="s2">);</span>
        <span class="s1">styleButton</span><span class="s2">(</span><span class="s1">deleteButton</span><span class="s2">);</span>
        <span class="s1">styleButton</span><span class="s2">(</span><span class="s1">viewButton</span><span class="s2">);</span>
        <span class="s1">styleButton</span><span class="s2">(</span><span class="s1">logoutButton</span><span class="s2">);</span>

        <span class="s1">buttonPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">addButton</span><span class="s2">);</span>
        <span class="s1">buttonPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">editButton</span><span class="s2">);</span>
        <span class="s1">buttonPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">deleteButton</span><span class="s2">);</span>
        <span class="s1">buttonPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">viewButton</span><span class="s2">);</span>
        <span class="s1">buttonPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">logoutButton</span><span class="s2">);</span>

        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">buttonPanel</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">NORTH</span><span class="s2">);</span>

        <span class="s1">cardPanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">CardLayout</span><span class="s2">());</span>
        <span class="s1">cardPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">createAddAssetPanel</span><span class="s2">(), </span><span class="s4">&quot;Add&quot;</span><span class="s2">);</span>
       <span class="s3">// cardPanel.add(createEditAssetPanel(), &quot;Edit&quot;);</span>
        <span class="s1">cardPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">createDeleteAssetPanel</span><span class="s2">(), </span><span class="s4">&quot;Delete&quot;</span><span class="s2">);</span>
        <span class="s1">cardPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">createViewAssetPanel</span><span class="s2">(), </span><span class="s4">&quot;View&quot;</span><span class="s2">);</span>

        <span class="s1">panel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">cardPanel</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">CENTER</span><span class="s2">);</span>

        <span class="s0">return </span><span class="s1">panel</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s3">// Method to style buttons</span>
    <span class="s0">private void </span><span class="s1">styleButton</span><span class="s2">(</span><span class="s1">JButton button</span><span class="s2">) {</span>
        <span class="s1">button</span><span class="s2">.</span><span class="s1">setBackground</span><span class="s2">(</span><span class="s1">Color</span><span class="s2">.</span><span class="s1">GRAY</span><span class="s2">);</span>
        <span class="s1">button</span><span class="s2">.</span><span class="s1">setForeground</span><span class="s2">(</span><span class="s1">Color</span><span class="s2">.</span><span class="s1">WHITE</span><span class="s2">);</span>
        <span class="s1">button</span><span class="s2">.</span><span class="s1">setFocusPainted</span><span class="s2">(</span><span class="s0">false</span><span class="s2">);</span>
    <span class="s2">}</span>

    <span class="s3">// Method to create Add Asset panel</span>
    <span class="s0">private </span><span class="s1">JPanel createAddAssetPanel</span><span class="s2">() {</span>
        <span class="s1">JPanel addAssetPanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">GridLayout</span><span class="s2">(</span><span class="s5">3</span><span class="s2">, </span><span class="s5">1</span><span class="s2">, </span><span class="s5">10</span><span class="s2">, </span><span class="s5">10</span><span class="s2">));</span>
        <span class="s1">addAssetPanel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;Add Asset Information&quot;</span><span class="s2">));</span>

        <span class="s3">// Employee Information</span>
        <span class="s1">JPanel employeeInfoPanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">GridLayout</span><span class="s2">(</span><span class="s5">5</span><span class="s2">, </span><span class="s5">2</span><span class="s2">, </span><span class="s5">5</span><span class="s2">, </span><span class="s5">5</span><span class="s2">));</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;Employee Information&quot;</span><span class="s2">));</span>

        <span class="s1">employeeFirstNameField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">employeeLastNameField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">employeeEmailField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">departmentComboBox </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JComboBox</span><span class="s2">&lt;&gt;(</span><span class="s1">DEPARTMENTS</span><span class="s2">);</span>

        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;First Name:&quot;</span><span class="s2">));</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">employeeFirstNameField</span><span class="s2">);</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Last Name:&quot;</span><span class="s2">));</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">employeeLastNameField</span><span class="s2">);</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Email:&quot;</span><span class="s2">));</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">employeeEmailField</span><span class="s2">);</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Department:&quot;</span><span class="s2">));</span>
        <span class="s1">employeeInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">departmentComboBox</span><span class="s2">);</span>

        <span class="s3">// Asset Information</span>
        <span class="s1">JPanel assetInfoPanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">GridLayout</span><span class="s2">(</span><span class="s5">8</span><span class="s2">, </span><span class="s5">2</span><span class="s2">, </span><span class="s5">5</span><span class="s2">, </span><span class="s5">5</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;Asset Information&quot;</span><span class="s2">));</span>

        <span class="s1">assetTagField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">systemNameField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">modelField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">manufacturerField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">typeField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">ipAddressField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">purchaseDateField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">();</span>
        <span class="s1">notesField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextArea</span><span class="s2">(</span><span class="s5">3</span><span class="s2">, </span><span class="s5">20</span><span class="s2">);</span>

        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Asset Tag:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">assetTagField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;System Name:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">systemNameField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Model:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">modelField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Manufacturer:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">manufacturerField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Type:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">typeField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;IP Address:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">ipAddressField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Purchase Date:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">purchaseDateField</span><span class="s2">);</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Notes:&quot;</span><span class="s2">));</span>
        <span class="s1">assetInfoPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JScrollPane</span><span class="s2">(</span><span class="s1">notesField</span><span class="s2">));</span>

        <span class="s3">// Add Asset Button</span>
        <span class="s1">JButton addAssetButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Add Asset&quot;</span><span class="s2">);</span>
        <span class="s1">addAssetButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; addAsset</span><span class="s2">());</span>

        <span class="s1">addAssetPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">employeeInfoPanel</span><span class="s2">);</span>
        <span class="s1">addAssetPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">assetInfoPanel</span><span class="s2">);</span>
        <span class="s1">addAssetPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">addAssetButton</span><span class="s2">);</span>

        <span class="s0">return </span><span class="s1">addAssetPanel</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s3">// Method to switch to a specific panel in card layout</span>
    <span class="s0">private void </span><span class="s1">switchToPanel</span><span class="s2">(</span><span class="s1">String panelName</span><span class="s2">) {</span>
        <span class="s1">CardLayout cl </span><span class="s2">= (</span><span class="s1">CardLayout</span><span class="s2">) (</span><span class="s1">cardPanel</span><span class="s2">.</span><span class="s1">getLayout</span><span class="s2">());</span>
        <span class="s1">cl</span><span class="s2">.</span><span class="s1">show</span><span class="s2">(</span><span class="s1">cardPanel</span><span class="s2">, </span><span class="s1">panelName</span><span class="s2">);</span>
    <span class="s2">}</span>

    <span class="s3">// Method to authenticate user</span>
    <span class="s0">private void </span><span class="s1">authenticate</span><span class="s2">() {</span>
        <span class="s1">String username </span><span class="s2">= </span><span class="s1">usernameField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String password </span><span class="s2">= </span><span class="s0">new </span><span class="s1">String</span><span class="s2">(</span><span class="s1">passwordField</span><span class="s2">.</span><span class="s1">getPassword</span><span class="s2">());</span>

        <span class="s1">User user </span><span class="s2">= </span><span class="s1">users</span><span class="s2">.</span><span class="s1">get</span><span class="s2">(</span><span class="s1">username</span><span class="s2">);</span>
        <span class="s0">if </span><span class="s2">(</span><span class="s1">user </span><span class="s2">!= </span><span class="s0">null </span><span class="s2">&amp;&amp; </span><span class="s1">user</span><span class="s2">.</span><span class="s1">getPassword</span><span class="s2">().</span><span class="s1">equals</span><span class="s2">(</span><span class="s1">password</span><span class="s2">)) {</span>
            <span class="s1">setTitle</span><span class="s2">(</span><span class="s4">&quot;Welcome &quot; </span><span class="s2">+ </span><span class="s1">user</span><span class="s2">.</span><span class="s1">getFirstName</span><span class="s2">() + </span><span class="s4">&quot; &quot; </span><span class="s2">+ </span><span class="s1">user</span><span class="s2">.</span><span class="s1">getLastName</span><span class="s2">());</span>
            <span class="s1">remove</span><span class="s2">(</span><span class="s1">loginPanel</span><span class="s2">);</span>
            <span class="s1">add</span><span class="s2">(</span><span class="s1">mainPanel</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">CENTER</span><span class="s2">);</span>
            <span class="s1">revalidate</span><span class="s2">();</span>
            <span class="s1">repaint</span><span class="s2">();</span>
        <span class="s2">} </span><span class="s0">else </span><span class="s2">{</span>
            <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Invalid username or password&quot;</span><span class="s2">, </span><span class="s4">&quot;Login Failed&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
        <span class="s2">}</span>
    <span class="s2">}</span>

    <span class="s3">// Method to add asset to database</span>
    <span class="s0">private void </span><span class="s1">addAsset</span><span class="s2">() {</span>
        <span class="s1">String assetTag </span><span class="s2">= </span><span class="s1">assetTagField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String systemName </span><span class="s2">= </span><span class="s1">systemNameField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String model </span><span class="s2">= </span><span class="s1">modelField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String manufacturer </span><span class="s2">= </span><span class="s1">manufacturerField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String type </span><span class="s2">= </span><span class="s1">typeField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String ipAddress </span><span class="s2">= </span><span class="s1">ipAddressField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String purchaseDate </span><span class="s2">= </span><span class="s1">purchaseDateField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String notes </span><span class="s2">= </span><span class="s1">notesField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String employeeFirstName </span><span class="s2">= </span><span class="s1">employeeFirstNameField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String employeeLastName </span><span class="s2">= </span><span class="s1">employeeLastNameField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String employeeEmail </span><span class="s2">= </span><span class="s1">employeeEmailField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">();</span>
        <span class="s1">String department </span><span class="s2">= (</span><span class="s1">String</span><span class="s2">) </span><span class="s1">departmentComboBox</span><span class="s2">.</span><span class="s1">getSelectedItem</span><span class="s2">();</span>

        <span class="s0">if </span><span class="s2">(</span><span class="s1">assetTag</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">systemName</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">model</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">manufacturer</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() ||</span>
                <span class="s1">type</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">ipAddress</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">purchaseDate</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() ||</span>
                <span class="s1">employeeFirstName</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">employeeLastName</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">employeeEmail</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">() || </span><span class="s1">department </span><span class="s2">== </span><span class="s0">null</span><span class="s2">) {</span>
            <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Please fill in all fields.&quot;</span><span class="s2">, </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
            <span class="s0">return</span><span class="s2">;</span>
        <span class="s2">}</span>

        <span class="s1">String id </span><span class="s2">= </span><span class="s1">UUID</span><span class="s2">.</span><span class="s1">randomUUID</span><span class="s2">().</span><span class="s1">toString</span><span class="s2">(); </span><span class="s3">// Generate unique asset ID</span>

        <span class="s0">try </span><span class="s2">(</span><span class="s1">Connection conn </span><span class="s2">= </span><span class="s1">DriverManager</span><span class="s2">.</span><span class="s1">getConnection</span><span class="s2">(</span><span class="s1">DATABASE_URL</span><span class="s2">)) {</span>
            <span class="s1">String sql </span><span class="s2">= </span><span class="s4">&quot;INSERT INTO assets (id, assetTag, systemName, model, manufacturer, type, ipAddress, purchaseDate, notes, employeeFirstName, employeeLastName, employeeEmail, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)&quot;</span><span class="s2">;</span>
            <span class="s0">try </span><span class="s2">(</span><span class="s1">PreparedStatement pstmt </span><span class="s2">= </span><span class="s1">conn</span><span class="s2">.</span><span class="s1">prepareStatement</span><span class="s2">(</span><span class="s1">sql</span><span class="s2">)) {</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">1</span><span class="s2">, </span><span class="s1">id</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">2</span><span class="s2">, </span><span class="s1">assetTag</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">3</span><span class="s2">, </span><span class="s1">systemName</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">4</span><span class="s2">, </span><span class="s1">model</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">5</span><span class="s2">, </span><span class="s1">manufacturer</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">6</span><span class="s2">, </span><span class="s1">type</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">7</span><span class="s2">, </span><span class="s1">ipAddress</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">8</span><span class="s2">, </span><span class="s1">purchaseDate</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">9</span><span class="s2">, </span><span class="s1">notes</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">10</span><span class="s2">, </span><span class="s1">employeeFirstName</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">11</span><span class="s2">, </span><span class="s1">employeeLastName</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">12</span><span class="s2">, </span><span class="s1">employeeEmail</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">13</span><span class="s2">, </span><span class="s1">department</span><span class="s2">);</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">executeUpdate</span><span class="s2">();</span>
            <span class="s2">}</span>

            <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Asset added successfully!&quot;</span><span class="s2">);</span>
            <span class="s1">clearAssetFields</span><span class="s2">();</span>
        <span class="s2">} </span><span class="s0">catch </span><span class="s2">(</span><span class="s1">SQLException e</span><span class="s2">) {</span>
            <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Error adding asset: &quot; </span><span class="s2">+ </span><span class="s1">e</span><span class="s2">.</span><span class="s1">getMessage</span><span class="s2">(), </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
        <span class="s2">}</span>
    <span class="s2">}</span>
    <span class="s3">// Method to create View Assets Panel</span>
    <span class="s0">private </span><span class="s1">JPanel createViewAssetPanel</span><span class="s2">() {</span>
        <span class="s1">JPanel viewPanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">BorderLayout</span><span class="s2">());</span>
        <span class="s1">viewPanel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;View Assets&quot;</span><span class="s2">));</span>

        <span class="s3">// Create a table to show assets</span>
        <span class="s1">tableModel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">DefaultTableModel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">String</span><span class="s2">[]{</span><span class="s4">&quot;Asset Tag&quot;</span><span class="s2">, </span><span class="s4">&quot;System Name&quot;</span><span class="s2">, </span><span class="s4">&quot;Model&quot;</span><span class="s2">, </span><span class="s4">&quot;Manufacturer&quot;</span><span class="s2">, </span><span class="s4">&quot;Type&quot;</span><span class="s2">, </span><span class="s4">&quot;IP Address&quot;</span><span class="s2">, </span><span class="s4">&quot;Purchase Date&quot;</span><span class="s2">, </span><span class="s4">&quot;Employee First Name&quot;</span><span class="s2">, </span><span class="s4">&quot;Employee Last Name&quot;</span><span class="s2">, </span><span class="s4">&quot;Employee Email&quot;</span><span class="s2">, </span><span class="s4">&quot;Department&quot;</span><span class="s2">}, </span><span class="s5">0</span><span class="s2">);</span>
        <span class="s1">assetTable </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTable</span><span class="s2">(</span><span class="s1">tableModel</span><span class="s2">);</span>
        <span class="s1">JScrollPane scrollPane </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JScrollPane</span><span class="s2">(</span><span class="s1">assetTable</span><span class="s2">);</span>

        <span class="s1">JButton refreshButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Refresh&quot;</span><span class="s2">);</span>
        <span class="s1">refreshButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; viewAssets</span><span class="s2">());</span>

        <span class="s1">viewPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">scrollPane</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">CENTER</span><span class="s2">);</span>
        <span class="s1">viewPanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">refreshButton</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">SOUTH</span><span class="s2">);</span>

        <span class="s0">return </span><span class="s1">viewPanel</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s3">// Method to view assets from the database</span>
    <span class="s0">private void </span><span class="s1">viewAssets</span><span class="s2">() {</span>
        <span class="s0">try </span><span class="s2">(</span><span class="s1">Connection conn </span><span class="s2">= </span><span class="s1">DriverManager</span><span class="s2">.</span><span class="s1">getConnection</span><span class="s2">(</span><span class="s1">DATABASE_URL</span><span class="s2">)) {</span>
            <span class="s1">String sql</span><span class="s2">;</span>
            <span class="s0">if </span><span class="s2">(</span><span class="s1">currentUser</span><span class="s2">.</span><span class="s1">getDepartment</span><span class="s2">().</span><span class="s1">equals</span><span class="s2">(</span><span class="s4">&quot;Admin&quot;</span><span class="s2">)) {</span>
                <span class="s1">sql </span><span class="s2">= </span><span class="s4">&quot;SELECT * FROM assets&quot;</span><span class="s2">;</span>
            <span class="s2">} </span><span class="s0">else </span><span class="s2">{</span>
                <span class="s1">sql </span><span class="s2">= </span><span class="s4">&quot;SELECT * FROM assets WHERE department = ?&quot;</span><span class="s2">;</span>
            <span class="s2">}</span>

            <span class="s0">try </span><span class="s2">(</span><span class="s1">PreparedStatement pstmt </span><span class="s2">= </span><span class="s1">conn</span><span class="s2">.</span><span class="s1">prepareStatement</span><span class="s2">(</span><span class="s1">sql</span><span class="s2">)) {</span>
                <span class="s0">if </span><span class="s2">(!</span><span class="s1">currentUser</span><span class="s2">.</span><span class="s1">getDepartment</span><span class="s2">().</span><span class="s1">equals</span><span class="s2">(</span><span class="s4">&quot;Admin&quot;</span><span class="s2">)) {</span>
                    <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">1</span><span class="s2">, </span><span class="s1">currentUser</span><span class="s2">.</span><span class="s1">getDepartment</span><span class="s2">());</span>
                <span class="s2">}</span>

                <span class="s1">ResultSet rs </span><span class="s2">= </span><span class="s1">pstmt</span><span class="s2">.</span><span class="s1">executeQuery</span><span class="s2">();</span>

                <span class="s3">// Set up table model to display assets</span>
                <span class="s1">tableModel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">DefaultTableModel</span><span class="s2">(</span>
                        <span class="s0">new </span><span class="s1">String</span><span class="s2">[]{</span><span class="s4">&quot;Asset Tag&quot;</span><span class="s2">, </span><span class="s4">&quot;System Name&quot;</span><span class="s2">, </span><span class="s4">&quot;Model&quot;</span><span class="s2">, </span><span class="s4">&quot;Manufacturer&quot;</span><span class="s2">, </span><span class="s4">&quot;Type&quot;</span><span class="s2">, </span><span class="s4">&quot;IP Address&quot;</span><span class="s2">, </span><span class="s4">&quot;Purchase Date&quot;</span><span class="s2">, </span><span class="s4">&quot;Notes&quot;</span><span class="s2">, </span><span class="s4">&quot;Department&quot;</span><span class="s2">}, </span><span class="s5">0</span><span class="s2">);</span>
                <span class="s0">while </span><span class="s2">(</span><span class="s1">rs</span><span class="s2">.</span><span class="s1">next</span><span class="s2">()) {</span>
                    <span class="s1">tableModel</span><span class="s2">.</span><span class="s1">addRow</span><span class="s2">(</span><span class="s0">new </span><span class="s1">Object</span><span class="s2">[]{</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;assetTag&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;systemName&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;model&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;manufacturer&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;type&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;ipAddress&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;purchaseDate&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;notes&quot;</span><span class="s2">),</span>
                            <span class="s1">rs</span><span class="s2">.</span><span class="s1">getString</span><span class="s2">(</span><span class="s4">&quot;department&quot;</span><span class="s2">)</span>
                    <span class="s2">});</span>
                <span class="s2">}</span>

                <span class="s1">assetTable </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTable</span><span class="s2">(</span><span class="s1">tableModel</span><span class="s2">);</span>
                <span class="s1">JScrollPane scrollPane </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JScrollPane</span><span class="s2">(</span><span class="s1">assetTable</span><span class="s2">);</span>
                <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s1">scrollPane</span><span class="s2">, </span><span class="s4">&quot;Assets&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">INFORMATION_MESSAGE</span><span class="s2">);</span>

            <span class="s2">} </span><span class="s0">catch </span><span class="s2">(</span><span class="s1">SQLException e</span><span class="s2">) {</span>
                <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Error retrieving assets: &quot; </span><span class="s2">+ </span><span class="s1">e</span><span class="s2">.</span><span class="s1">getMessage</span><span class="s2">(), </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
            <span class="s2">}</span>
        <span class="s2">} </span><span class="s0">catch </span><span class="s2">(</span><span class="s1">SQLException e</span><span class="s2">) {</span>
            <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Database connection error: &quot; </span><span class="s2">+ </span><span class="s1">e</span><span class="s2">.</span><span class="s1">getMessage</span><span class="s2">(), </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
        <span class="s2">}</span>
    <span class="s2">}</span>

<span class="s3">//    // Method to create Edit Asset panel</span>
<span class="s3">//    private JPanel createEditAssetPanel() {</span>
<span class="s3">//        JPanel editPanel = createAddAssetPanel(); // Reuse Add Asset Panel</span>
<span class="s3">//        JButton editAssetButton = new JButton(&quot;Edit Asset&quot;);</span>
<span class="s3">//</span>
<span class="s3">//        editAssetButton.addActionListener(e -&gt; editAsset());</span>
<span class="s3">//</span>
<span class="s3">//        editPanel.add(editAssetButton, BorderLayout.SOUTH);</span>
<span class="s3">//        return editPanel;</span>
<span class="s3">//    }</span>

    <span class="s3">// Method to edit an asset in the database</span>
<span class="s3">//    private void editAsset() {</span>
<span class="s3">//        String assetTag = assetTagField.getText();</span>
<span class="s3">//</span>
<span class="s3">//        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {</span>
<span class="s3">//            String sql = &quot;UPDATE assets SET systemName=?, model=?, manufacturer=?, type=?, ipAddress=?, purchaseDate=?, notes=?, employeeFirstName=?, employeeLastName=?, employeeEmail=?, department=? WHERE assetTag=?&quot;;</span>
<span class="s3">//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {</span>
<span class="s3">//                pstmt.setString(1, systemNameField.getText());</span>
<span class="s3">//                pstmt.setString(2, modelField.getText());</span>
<span class="s3">//                pstmt.setString(3, manufacturerField.getText());</span>
<span class="s3">//                pstmt.setString(4, typeField.getText());</span>
<span class="s3">//                pstmt.setString(5, ipAddressField.getText());</span>
<span class="s3">//                pstmt.setString(6, purchaseDateField.getText());</span>
<span class="s3">//                pstmt.setString(7, notesField.getText());</span>
<span class="s3">//                pstmt.setString(8, employeeFirstNameField.getText());</span>
<span class="s3">//                pstmt.setString(9, employeeLastNameField.getText());</span>
<span class="s3">//                pstmt.setString(10, employeeEmailField.getText());</span>
<span class="s3">//                pstmt.setString(11, (String) departmentComboBox.getSelectedItem());</span>
<span class="s3">//                pstmt.setString(12, assetTag);</span>
<span class="s3">//</span>
<span class="s3">//                int rowsUpdated = pstmt.executeUpdate();</span>
<span class="s3">//                if (rowsUpdated &gt; 0) {</span>
<span class="s3">//                    JOptionPane.showMessageDialog(this, &quot;Asset updated successfully!&quot;);</span>
<span class="s3">//                    clearAssetFields();</span>
<span class="s3">//                } else {</span>
<span class="s3">//                    JOptionPane.showMessageDialog(this, &quot;Asset not found!&quot;, &quot;Error&quot;, JOptionPane.ERROR_MESSAGE);</span>
<span class="s3">//                }</span>
<span class="s3">//            }</span>
<span class="s3">//        } catch (SQLException e) {</span>
<span class="s3">//            JOptionPane.showMessageDialog(this, &quot;Error updating asset: &quot; + e.getMessage(), &quot;Error&quot;, JOptionPane.ERROR_MESSAGE);</span>
<span class="s3">//        }</span>
<span class="s3">//    }</span>

    <span class="s0">private </span><span class="s1">JPanel createDeleteAssetPanel</span><span class="s2">() {</span>
        <span class="s1">JPanel deletePanel </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JPanel</span><span class="s2">(</span><span class="s0">new </span><span class="s1">BorderLayout</span><span class="s2">());</span>
        <span class="s1">deletePanel</span><span class="s2">.</span><span class="s1">setBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createTitledBorder</span><span class="s2">(</span><span class="s1">BorderFactory</span><span class="s2">.</span><span class="s1">createEtchedBorder</span><span class="s2">(), </span><span class="s4">&quot;Delete Asset&quot;</span><span class="s2">));</span>

        <span class="s3">// Resize the Asset Tag input field</span>
        <span class="s1">JTextField assetTagToDeleteField </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JTextField</span><span class="s2">(</span><span class="s5">20</span><span class="s2">);  </span><span class="s3">// Resized for better visibility</span>
        <span class="s1">JButton deleteAssetButton </span><span class="s2">= </span><span class="s0">new </span><span class="s1">JButton</span><span class="s2">(</span><span class="s4">&quot;Delete Asset&quot;</span><span class="s2">);</span>

        <span class="s1">deleteAssetButton</span><span class="s2">.</span><span class="s1">addActionListener</span><span class="s2">(</span><span class="s1">e -&gt; </span><span class="s2">{</span>
            <span class="s1">String assetTag </span><span class="s2">= </span><span class="s1">assetTagToDeleteField</span><span class="s2">.</span><span class="s1">getText</span><span class="s2">().</span><span class="s1">trim</span><span class="s2">();</span>
            <span class="s0">if </span><span class="s2">(!</span><span class="s1">assetTag</span><span class="s2">.</span><span class="s1">isEmpty</span><span class="s2">()) {</span>
                <span class="s0">int </span><span class="s1">response </span><span class="s2">= </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showConfirmDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">,</span>
                        <span class="s4">&quot;Are you sure you want to delete the asset with Asset Tag: &quot; </span><span class="s2">+ </span><span class="s1">assetTag </span><span class="s2">+ </span><span class="s4">&quot;?&quot;</span><span class="s2">,</span>
                        <span class="s4">&quot;Confirm Deletion&quot;</span><span class="s2">,</span>
                        <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">YES_NO_OPTION</span><span class="s2">,</span>
                        <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">WARNING_MESSAGE</span><span class="s2">);</span>

                <span class="s0">if </span><span class="s2">(</span><span class="s1">response </span><span class="s2">== </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">YES_OPTION</span><span class="s2">) {</span>
                    <span class="s1">deleteAsset</span><span class="s2">(</span><span class="s1">assetTag</span><span class="s2">);</span>
                <span class="s2">}</span>
            <span class="s2">} </span><span class="s0">else </span><span class="s2">{</span>
                <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Please enter an Asset Tag to delete.&quot;</span><span class="s2">, </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
            <span class="s2">}</span>
        <span class="s2">});</span>

        <span class="s1">deletePanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s0">new </span><span class="s1">JLabel</span><span class="s2">(</span><span class="s4">&quot;Enter Asset Tag to Delete:&quot;</span><span class="s2">), </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">NORTH</span><span class="s2">);</span>
        <span class="s1">deletePanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">assetTagToDeleteField</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">CENTER</span><span class="s2">);</span>
        <span class="s1">deletePanel</span><span class="s2">.</span><span class="s1">add</span><span class="s2">(</span><span class="s1">deleteAssetButton</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">SOUTH</span><span class="s2">);</span>

        <span class="s0">return </span><span class="s1">deletePanel</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s3">// Method to delete an asset from the database</span>
    <span class="s0">private void </span><span class="s1">deleteAsset</span><span class="s2">(</span><span class="s1">String assetTag</span><span class="s2">) {</span>
        <span class="s0">try </span><span class="s2">(</span><span class="s1">Connection conn </span><span class="s2">= </span><span class="s1">DriverManager</span><span class="s2">.</span><span class="s1">getConnection</span><span class="s2">(</span><span class="s1">DATABASE_URL</span><span class="s2">)) {</span>
            <span class="s1">String sql </span><span class="s2">= </span><span class="s4">&quot;DELETE FROM assets WHERE assetTag = ?&quot;</span><span class="s2">;</span>
            <span class="s0">try </span><span class="s2">(</span><span class="s1">PreparedStatement pstmt </span><span class="s2">= </span><span class="s1">conn</span><span class="s2">.</span><span class="s1">prepareStatement</span><span class="s2">(</span><span class="s1">sql</span><span class="s2">)) {</span>
                <span class="s1">pstmt</span><span class="s2">.</span><span class="s1">setString</span><span class="s2">(</span><span class="s5">1</span><span class="s2">, </span><span class="s1">assetTag</span><span class="s2">);</span>

                <span class="s0">int </span><span class="s1">rowsDeleted </span><span class="s2">= </span><span class="s1">pstmt</span><span class="s2">.</span><span class="s1">executeUpdate</span><span class="s2">();</span>
                <span class="s0">if </span><span class="s2">(</span><span class="s1">rowsDeleted </span><span class="s2">&gt; </span><span class="s5">0</span><span class="s2">) {</span>
                    <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Asset with Asset Tag: &quot; </span><span class="s2">+ </span><span class="s1">assetTag </span><span class="s2">+ </span><span class="s4">&quot; has been successfully deleted!&quot;</span><span class="s2">, </span><span class="s4">&quot;Deletion Successful&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">INFORMATION_MESSAGE</span><span class="s2">);</span>
                <span class="s2">} </span><span class="s0">else </span><span class="s2">{</span>
                    <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Asset with Asset Tag: &quot; </span><span class="s2">+ </span><span class="s1">assetTag </span><span class="s2">+ </span><span class="s4">&quot; not found!&quot;</span><span class="s2">, </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
                <span class="s2">}</span>
            <span class="s2">}</span>
        <span class="s2">} </span><span class="s0">catch </span><span class="s2">(</span><span class="s1">SQLException e</span><span class="s2">) {</span>
            <span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">showMessageDialog</span><span class="s2">(</span><span class="s0">this</span><span class="s2">, </span><span class="s4">&quot;Error deleting asset: &quot; </span><span class="s2">+ </span><span class="s1">e</span><span class="s2">.</span><span class="s1">getMessage</span><span class="s2">(), </span><span class="s4">&quot;Error&quot;</span><span class="s2">, </span><span class="s1">JOptionPane</span><span class="s2">.</span><span class="s1">ERROR_MESSAGE</span><span class="s2">);</span>
        <span class="s2">}</span>
    <span class="s2">}</span>


    <span class="s0">private void </span><span class="s1">clearAssetFields</span><span class="s2">() {</span>
        <span class="s1">assetTagField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">systemNameField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">modelField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">manufacturerField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">typeField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">ipAddressField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">purchaseDateField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">notesField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">employeeFirstNameField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">employeeLastNameField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">employeeEmailField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">departmentComboBox</span><span class="s2">.</span><span class="s1">setSelectedIndex</span><span class="s2">(</span><span class="s5">0</span><span class="s2">);</span>
    <span class="s2">}</span>

    <span class="s0">private void </span><span class="s1">logout</span><span class="s2">() {</span>
        <span class="s1">remove</span><span class="s2">(</span><span class="s1">mainPanel</span><span class="s2">);</span>
        <span class="s1">add</span><span class="s2">(</span><span class="s1">loginPanel</span><span class="s2">, </span><span class="s1">BorderLayout</span><span class="s2">.</span><span class="s1">CENTER</span><span class="s2">);</span>
        <span class="s1">usernameField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">passwordField</span><span class="s2">.</span><span class="s1">setText</span><span class="s2">(</span><span class="s4">&quot;&quot;</span><span class="s2">);</span>
        <span class="s1">revalidate</span><span class="s2">();</span>
        <span class="s1">repaint</span><span class="s2">();</span>
    <span class="s2">}</span>

    <span class="s0">public static void </span><span class="s1">main</span><span class="s2">(</span><span class="s1">String</span><span class="s2">[] </span><span class="s1">args</span><span class="s2">) {</span>
        <span class="s1">SwingUtilities</span><span class="s2">.</span><span class="s1">invokeLater</span><span class="s2">(() </span><span class="s1">-&gt; </span><span class="s2">{</span>
            <span class="s1">AssetTrackingApp app </span><span class="s2">= </span><span class="s0">new </span><span class="s1">AssetTrackingApp</span><span class="s2">();</span>
            <span class="s1">app</span><span class="s2">.</span><span class="s1">setVisible</span><span class="s2">(</span><span class="s0">true</span><span class="s2">);</span>
        <span class="s2">});</span>
    <span class="s2">}</span>
<span class="s2">}</span>

<span class="s0">class </span><span class="s1">User </span><span class="s2">{</span>
    <span class="s0">private </span><span class="s1">String username</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">String password</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">String firstName</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">String lastName</span><span class="s2">;</span>
    <span class="s0">private </span><span class="s1">String email</span><span class="s2">;</span>

    <span class="s0">public </span><span class="s1">User</span><span class="s2">(</span><span class="s1">String username</span><span class="s2">, </span><span class="s1">String password</span><span class="s2">, </span><span class="s1">String firstName</span><span class="s2">, </span><span class="s1">String lastName</span><span class="s2">, </span><span class="s1">String email</span><span class="s2">) {</span>
        <span class="s0">this</span><span class="s2">.</span><span class="s1">username </span><span class="s2">= </span><span class="s1">username</span><span class="s2">;</span>
        <span class="s0">this</span><span class="s2">.</span><span class="s1">password </span><span class="s2">= </span><span class="s1">password</span><span class="s2">;</span>
        <span class="s0">this</span><span class="s2">.</span><span class="s1">firstName </span><span class="s2">= </span><span class="s1">firstName</span><span class="s2">;</span>
        <span class="s0">this</span><span class="s2">.</span><span class="s1">lastName </span><span class="s2">= </span><span class="s1">lastName</span><span class="s2">;</span>
        <span class="s0">this</span><span class="s2">.</span><span class="s1">email </span><span class="s2">= </span><span class="s1">email</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s0">public </span><span class="s1">String getUsername</span><span class="s2">() {</span>
        <span class="s0">return </span><span class="s1">username</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s0">public </span><span class="s1">String getPassword</span><span class="s2">() {</span>
        <span class="s0">return </span><span class="s1">password</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s0">public </span><span class="s1">String getFirstName</span><span class="s2">() {</span>
        <span class="s0">return </span><span class="s1">firstName</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s0">public </span><span class="s1">String getLastName</span><span class="s2">() {</span>
        <span class="s0">return </span><span class="s1">lastName</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s0">public </span><span class="s1">String getEmail</span><span class="s2">() {</span>
        <span class="s0">return </span><span class="s1">email</span><span class="s2">;</span>
    <span class="s2">}</span>

    <span class="s0">public </span><span class="s1">String getDepartment</span><span class="s2">() {</span>
        <span class="s0">return </span><span class="s1">department</span><span class="s2">;</span>
    <span class="s2">}</span>
<span class="s2">}</span>
</pre>
</body>
</html>