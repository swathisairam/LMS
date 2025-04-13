import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private JTabbedPane professorTabbedPane;
    private JPanel scoresPanel;
    
    // Student dashboard components
    private JComboBox<String> courseComboBox;
    private JPanel materialsPanel;
    private JPanel quizPanel;
    private Map<Integer, JTextField> quizAnswers;
    private String currentCourse;
    
    // Current user info
    private String currentUsername;
    private String currentUserType;
    
    public LMSFrontend() {
        // Initialize backend
        backend = new LMSBackend();
        
        // Setup frame
        setTitle("Learning Management System");
        setSize(900, 700);
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
        
        // Create tabbed pane for different professor functions
        professorTabbedPane = new JTabbedPane();
        
        // Upload panel
        JPanel uploadPanel = new JPanel(new GridBagLayout());
        uploadPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
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
        
        // Create scores panel
        scoresPanel = new JPanel(new BorderLayout());
        scoresPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel scoreControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> scoresCourseComboBox = new JComboBox<>();
        JButton refreshScoresButton = new JButton("Refresh Scores");
        
        scoreControlPanel.add(new JLabel("Select Course:"));
        scoreControlPanel.add(scoresCourseComboBox);
        scoreControlPanel.add(refreshScoresButton);
        
        scoresPanel.add(scoreControlPanel, BorderLayout.NORTH);
        
        // Table to display student scores
        String[] columnNames = {"Student", "Quiz", "Score", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable scoresTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scoresPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Load courses for the scores dropdown
        refreshScoresButton.addActionListener(e -> {
            String selectedCourse = (String) scoresCourseComboBox.getSelectedItem();
            if (selectedCourse != null) {
                loadStudentScores(selectedCourse, tableModel);
            }
        });
        
        // Add tabs to the tabbed pane
        professorTabbedPane.addTab("Upload Materials", uploadPanel);
        professorTabbedPane.addTab("Student Scores", scoresPanel);
        
        professorDashboard.add(professorTabbedPane, BorderLayout.CENTER);
        
        // Load courses for the scores dropdown when professor logs in
        scoresCourseComboBox.addItem("Select a course");
        List<String> courses = backend.getCourses();
        for (String course : courses) {
            scoresCourseComboBox.addItem(course);
        }
    }
    
    private void loadStudentScores(String courseName, DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load scores from backend
        List<String[]> scores = backend.getStudentScores(courseName);
        
        for (String[] score : scores) {
            tableModel.addRow(score);
        }
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
                currentCourse = (String) courseComboBox.getSelectedItem();
                loadCourseMaterials(currentCourse);
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
        
        // Scores panel for students
        JPanel studentScoresPanel = new JPanel();
        studentScoresPanel.setLayout(new BoxLayout(studentScoresPanel, BoxLayout.Y_AXIS));
        JScrollPane scoresScrollPane = new JScrollPane(studentScoresPanel);
        tabbedPane.addTab("My Scores", scoresScrollPane);
        
        // Add listener to refresh scores when tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2 && currentCourse != null) {
                loadStudentScoreHistory(studentScoresPanel);
            }
        });
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        studentDashboard.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadStudentScoreHistory(JPanel scoresPanel) {
        scoresPanel.removeAll();
        
        JLabel titleLabel = new JLabel("My Quiz Scores", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scoresPanel.add(titleLabel);
        scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        List<String[]> scoreHistory = backend.getStudentScoreHistory(currentUsername);
        
        if (scoreHistory.isEmpty()) {
            JLabel noScoresLabel = new JLabel("You haven't taken any quizzes yet.");
            noScoresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            scoresPanel.add(noScoresLabel);
        } else {
            // Create a table to display scores
            String[] columnNames = {"Course", "Quiz", "Score", "Date"};
            Object[][] data = new Object[scoreHistory.size()][4];
            
            for (int i = 0; i < scoreHistory.size(); i++) {
                data[i][0] = scoreHistory.get(i)[0]; // Course
                data[i][1] = scoreHistory.get(i)[1]; // Quiz
                data[i][2] = scoreHistory.get(i)[2]; // Score
                data[i][3] = scoreHistory.get(i)[3]; // Date
            }
            
            JTable scoresTable = new JTable(data, columnNames);
            JScrollPane tableScrollPane = new JScrollPane(scoresTable);
            tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            tableScrollPane.setPreferredSize(new Dimension(500, 300));
            scoresPanel.add(tableScrollPane);
        }
        
        scoresPanel.revalidate();
        scoresPanel.repaint();
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
                // Refresh the courses list in the scores tab
                refreshProfessorCoursesList();
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
    
    private void refreshProfessorCoursesList() {
        JComboBox<String> scoresCourseComboBox = (JComboBox<String>) ((JPanel) ((JPanel) scoresPanel.getComponent(0)).getComponent(0)).getComponent(1);
        scoresCourseComboBox.removeAllItems();
        
        List<String> courses = backend.getCourses();
        for (String course : courses) {
            scoresCourseComboBox.addItem(course);
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
        
        // Initialize quiz answers map
        quizAnswers = new HashMap<>();
        
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
                int fileId = Integer.parseInt(material[2]);
                
                JPanel materialItem = new JPanel(new BorderLayout());
                materialItem.setBorder(BorderFactory.createEtchedBorder());
                materialItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                
                JLabel fileNameLabel = new JLabel(fileName);
                fileNameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
                materialItem.add(fileNameLabel, BorderLayout.WEST);
                
                JButton viewButton = new JButton("View PDF");
                viewButton.addActionListener(e -> openPDF(filePath));
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
                int quizId = Integer.parseInt(quiz[0]);
                String question = quiz[1];
                
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
                
                // Store the answer field reference for later submission
                quizAnswers.put(quizId, answerField);
                
                quizPanel.add(questionPanel);
                quizPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            JButton submitButton = new JButton("Submit Answers");
            submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            submitButton.addActionListener(e -> submitQuizAnswers(courseName));
            quizPanel.add(submitButton);
        }
        
        // Refresh panels
        materialsPanel.revalidate();
        materialsPanel.repaint();
        quizPanel.revalidate();
        quizPanel.repaint();
    }
    
    private void openPDF(String filePath) {
        try {
            File pdfFile = new File(filePath);
            
            if (pdfFile.exists()) {
                // Try to open the PDF with the default system application
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot open PDF automatically. File is located at: " + filePath,
                        "PDF Viewer", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "PDF file not found at: " + filePath,
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening PDF: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void submitQuizAnswers(String courseName) {
        // Collect answers
        Map<Integer, String> answers = new HashMap<>();
        for (Map.Entry<Integer, JTextField> entry : quizAnswers.entrySet()) {
            answers.put(entry.getKey(), entry.getValue().getText());
        }
        
        // Submit answers to backend
        int score = backend.submitQuizAnswers(currentUsername, courseName, answers);
        
        // Show score to student
        JOptionPane.showMessageDialog(this, 
            "Quiz submitted! Your score: " + score + "/" + answers.size(),
            "Quiz Result", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear answer fields
        for (JTextField field : quizAnswers.values()) {
            field.setText("");
        }
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
