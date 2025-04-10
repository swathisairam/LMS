import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LMSFrontend extends JFrame {
    // Backend reference
    private LMSBackend backend;
    
    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Login/Register panels
    private JPanel loginPanel;
    private JPanel registerPanel;
    
    // Dashboard panels
    private JPanel professorDashboard;
    private JPanel studentDashboard;
    
    // Login components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    
    // Register components
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JComboBox<String> userTypeComboBox;
    
    // Professor dashboard components
    private JComboBox<String> subjectComboBox;
    private JTextField filePathField;
    private JButton browseButton;
    private File selectedFile;
    
    // Student dashboard components
    private JComboBox<String> courseComboBox;
    private JPanel materialsPanel;
    private JPanel quizPanel;
    
    // Current user info
    private String currentUsername;
    private String currentUserType;
    
    public LMSFrontend() {
        // Initialize backend
        backend = new LMSBackend();
        
        // Setup frame
        setTitle("Learning Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize main panel with card layout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Create panels
        createLoginPanel();
        createRegisterPanel();
        createProfessorDashboard();
        createStudentDashboard();
        
        // Add panels to main panel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        mainPanel.add(professorDashboard, "professor");
        mainPanel.add(studentDashboard, "student");
        
        // Show login panel first
        cardLayout.show(mainPanel, "login");
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Learning Management System - Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        loginPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        loginUsernameField = new JTextField(20);
        formPanel.add(loginUsernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        loginPasswordField = new JPasswordField(20);
        formPanel.add(loginPasswordField, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        formPanel.add(loginButton, gbc);
        
        // Register link
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton registerLink = new JButton("Don't have an account? Register");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(Color.BLUE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        formPanel.add(registerLink, gbc);
        
        // Add form to center
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(formPanel);
        loginPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createRegisterPanel() {
        registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Learning Management System - Register", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        registerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        registerUsernameField = new JTextField(20);
        formPanel.add(registerUsernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        registerPasswordField = new JPasswordField(20);
        formPanel.add(registerPasswordField, gbc);
        
        // User Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("User Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        userTypeComboBox = new JComboBox<>(new String[]{"Student", "Professor"});
        formPanel.add(userTypeComboBox, gbc);
        
        // Register button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        formPanel.add(registerButton, gbc);
        
        // Login link
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton loginLink = new JButton("Already have an account? Login");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        formPanel.add(loginLink, gbc);
        
        // Add form to center
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(formPanel);
        registerPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createProfessorDashboard() {
        professorDashboard = new JPanel(new BorderLayout());
        professorDashboard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Professor Dashboard", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        professorDashboard.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Upload panel
        JPanel uploadPanel = new JPanel(new GridBagLayout());
        uploadPanel.setBorder(BorderFactory.createTitledBorder("Upload Course Material"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Subject selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        uploadPanel.add(new JLabel("Select Subject:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        String[] subjects = {"OOAD", "Compiler Construction", "Computer Design", "Elective 3", "Elective 4"};
        subjectComboBox = new JComboBox<>(subjects);
        uploadPanel.add(subjectComboBox, gbc);
        
        // File selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        uploadPanel.add(new JLabel("Select PDF File:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        filePathField = new JTextField(20);
        filePathField.setEditable(false);
        uploadPanel.add(filePathField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseFile());
        uploadPanel.add(browseButton, gbc);
        
        // Upload button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        JButton uploadButton = new JButton("Upload Material");
        uploadButton.addActionListener(e -> handleUpload());
        uploadPanel.add(uploadButton, gbc);
        
        contentPanel.add(uploadPanel, BorderLayout.NORTH);
        professorDashboard.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createStudentDashboard() {
        studentDashboard = new JPanel(new BorderLayout());
        studentDashboard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Student Dashboard", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        studentDashboard.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Course selection panel
        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        coursePanel.add(new JLabel("Select Course:"));
        
        courseComboBox = new JComboBox<>();
        courseComboBox.addActionListener(e -> {
            if (courseComboBox.getSelectedItem() != null) {
                loadCourseMaterials((String) courseComboBox.getSelectedItem());
            }
        });
        coursePanel.add(courseComboBox);
        
        contentPanel.add(coursePanel, BorderLayout.NORTH);
        
        // Tab panel for materials and quizzes
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Materials panel
        materialsPanel = new JPanel();
        materialsPanel.setLayout(new BoxLayout(materialsPanel, BoxLayout.Y_AXIS));
        JScrollPane materialsScrollPane = new JScrollPane(materialsPanel);
        tabbedPane.addTab("Materials", materialsScrollPane);
        
        // Quiz panel
        quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));
        JScrollPane quizScrollPane = new JScrollPane(quizPanel);
        tabbedPane.addTab("Quizzes", quizScrollPane);
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        studentDashboard.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String userType = backend.loginUser(username, password);
        
        if (userType != null) {
            currentUsername = username;
            currentUserType = userType;
            
            if (userType.equals("Professor")) {
                cardLayout.show(mainPanel, "professor");
            } else {
                // Load courses for student
                loadCourses();
                cardLayout.show(mainPanel, "student");
            }
            
            // Clear login fields
            loginUsernameField.setText("");
            loginPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegister() {
        String username = registerUsernameField.getText();
        String password = new String(registerPasswordField.getPassword());
        String userType = (String) userTypeComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = backend.registerUser(username, password, userType);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.", "Registration", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "login");
            
            // Clear registration fields
            registerUsernameField.setText("");
            registerPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    private void handleUpload() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file to upload", "Upload Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String subject = (String) subjectComboBox.getSelectedItem();
        
        boolean success = backend.uploadMaterial(subject, selectedFile);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "File uploaded successfully and quiz generated!", "Upload", JOptionPane.INFORMATION_MESSAGE);
            filePathField.setText("");
            selectedFile = null;
        } else {
            JOptionPane.showMessageDialog(this, "File upload failed", "Upload Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadCourses() {
        List<String> courses = backend.getCourses();
        courseComboBox.removeAllItems();
        
        for (String course : courses) {
            courseComboBox.addItem(course);
        }
    }
    
    private void loadCourseMaterials(String courseName) {
        // Clear panels
        materialsPanel.removeAll();
        quizPanel.removeAll();
        
        // Load materials
        List<String[]> materials = backend.getCourseMaterials(courseName);
        
        if (materials.isEmpty()) {
            JLabel noMaterialsLabel = new JLabel("No materials available for this course");
            noMaterialsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            materialsPanel.add(noMaterialsLabel);
        } else {
            for (String[] material : materials) {
                String fileName = material[0];
                String filePath = material[1];
                
                JPanel materialItem = new JPanel(new BorderLayout());
                materialItem.setBorder(BorderFactory.createEtchedBorder());
                materialItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                
                JLabel fileNameLabel = new JLabel(fileName);
                fileNameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
                materialItem.add(fileNameLabel, BorderLayout.WEST);
                
                JButton viewButton = new JButton("View");
                viewButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Viewing file: " + filePath, "View File", JOptionPane.INFORMATION_MESSAGE);
                });
                materialItem.add(viewButton, BorderLayout.EAST);
                
                materialsPanel.add(materialItem);
                materialsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        // Load quizzes
        List<String[]> quizzes = backend.getCourseQuizzes(courseName);
        
        if (quizzes.isEmpty()) {
            JLabel noQuizzesLabel = new JLabel("No quizzes available for this course");
            noQuizzesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            quizPanel.add(noQuizzesLabel);
        } else {
            JLabel quizTitleLabel = new JLabel(courseName + " Quiz");
            quizTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            quizTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            quizPanel.add(quizTitleLabel);
            quizPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            for (String[] quiz : quizzes) {
                String question = quiz[0];
                
                JPanel questionPanel = new JPanel();
                questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
                questionPanel.setBorder(BorderFactory.createEtchedBorder());
                questionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel questionLabel = new JLabel(question);
                questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionPanel.add(questionLabel);
                
                JTextField answerField = new JTextField();
                answerField.setAlignmentX(Component.LEFT_ALIGNMENT);
                answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                questionPanel.add(answerField);
                
                quizPanel.add(questionPanel);
                quizPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            JButton submitButton = new JButton("Submit Answers");
            submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            submitButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Quiz submitted successfully!", "Quiz Submission", JOptionPane.INFORMATION_MESSAGE);
            });
            quizPanel.add(submitButton);
        }
        
        // Refresh panels
        materialsPanel.revalidate();
        materialsPanel.repaint();
        quizPanel.revalidate();
        quizPanel.repaint();
    }
    
    private void logout() {
        currentUsername = null;
        currentUserType = null;
        cardLayout.show(mainPanel, "login");
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LMSFrontend app = new LMSFrontend();
            app.setVisible(true);
        });
    }
}