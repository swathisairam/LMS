import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Frontend class implementing MVC architecture
 * - View: UI components
 * - Controller: Event handlers that interact with the model
 */
public class frontend extends JFrame implements Observer {
    // MVC Components
    private LMSController controller;
    
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
    private Map<Integer, ButtonGroup> quizAnswerGroups;
    private Map<Integer, Map<String, JRadioButton>> quizOptions;
    private String currentCourse;
    
    // Current user info
    private String currentUsername;
    private String currentUserType;
    
    private static final String[] COURSE_LIST = {
        "OOAD", "Cloud Computing", "Computer Design", "GENAI", "Robotics"
    };
    
    /**
     * Constructor - initializes the UI
     */
    public frontend() {
        // Initialize controller
        controller = new LMSController();
        controller.addObserver(this);
        
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
    
    /**
     * Observer pattern implementation
     * Updates the UI based on model changes
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String message = (String) arg;
            if (message.equals("COURSES_UPDATED")) {
                loadCourses();
                refreshProfessorCoursesList();
            } else if (message.equals("QUIZ_SUBMITTED")) {
                JOptionPane.showMessageDialog(this, 
                    "Quiz submitted! Your score: " + controller.getLastQuizScore() + "%",
                    "Quiz Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Creates the login panel
     */
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
    
    /**
     * Creates the registration panel
     */
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
    
    /**
     * Creates the professor dashboard
     */
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
        subjectComboBox = new JComboBox<>(COURSE_LIST);
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
        String[] columnNames = {"Student", "Quiz", "Score", "Percentage", "Date"};
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
        List<String> courses = controller.getCourses();
        for (String course : courses) {
            scoresCourseComboBox.addItem(course);
        }
    }
    
    /**
     * Loads student scores for a specific course
     */
    private void loadStudentScores(String courseName, DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load scores from controller
        List<String[]> scores = controller.getStudentScores(courseName);
        
        for (String[] score : scores) {
            tableModel.addRow(score);
        }
    }
    
    /**
     * Creates the student dashboard
     */
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
            if (tabbedPane.getSelectedIndex() == 2 && currentUsername != null) {
                loadStudentScoreHistory(studentScoresPanel);
            }
        });
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        studentDashboard.add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Loads score history for the current student
     */
    private void loadStudentScoreHistory(JPanel scoresPanel) {
        scoresPanel.removeAll();
        
        JLabel titleLabel = new JLabel("My Quiz Scores", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scoresPanel.add(titleLabel);
        scoresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        List<String[]> scoreHistory = controller.getStudentScoreHistory(currentUsername);
        
        if (scoreHistory.isEmpty()) {
            JLabel noScoresLabel = new JLabel("You haven't taken any quizzes yet.");
            noScoresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            scoresPanel.add(noScoresLabel);
        } else {
            // Create a table to display scores
            String[] columnNames = {"Course", "Quiz", "Score", "Percentage", "Date"};
            Object[][] data = new Object[scoreHistory.size()][5];
            
            for (int i = 0; i < scoreHistory.size(); i++) {
                data[i][0] = scoreHistory.get(i)[0]; // Course
                data[i][1] = scoreHistory.get(i)[1]; // Quiz
                data[i][2] = scoreHistory.get(i)[2]; // Score
                data[i][3] = scoreHistory.get(i)[3]; // Percentage
                data[i][4] = scoreHistory.get(i)[4]; // Date
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
    
    /**
     * Handles login button click
     */
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String userType = controller.loginUser(username, password);
        
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
    
    /**
     * Refreshes the courses list in the professor dashboard
     */
    private void refreshProfessorCoursesList() {
        JComboBox<String> scoresCourseComboBox = (JComboBox<String>) ((JPanel) scoresPanel.getComponent(0)).getComponent(1);
        scoresCourseComboBox.removeAllItems();
        for (String course : COURSE_LIST) {
            scoresCourseComboBox.addItem(course);
        }
    }
    
    /**
     * Handles register button click
     */
    private void handleRegister() {
        String username = registerUsernameField.getText();
        String password = new String(registerPasswordField.getPassword());
        String userType = (String) userTypeComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = controller.registerUser(username, password, userType);
        
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
    
    /**
     * Opens file browser to select PDF
     */
    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    /**
     * Handles upload button click
     */
    private void handleUpload() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file to upload", "Upload Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String subject = (String) subjectComboBox.getSelectedItem();
        
        boolean success = controller.uploadMaterial(subject, selectedFile);
        
        JOptionPane.showMessageDialog(this, "Successfully uploaded", "Upload", JOptionPane.INFORMATION_MESSAGE);
        filePathField.setText("");
        selectedFile = null;
    }
    
    /**
     * Loads available courses
     */
    private void loadCourses() {
        courseComboBox.removeAllItems();
        for (String course : COURSE_LIST) {
            courseComboBox.addItem(course);
        }
    }
    
    /**
     * Loads materials and quizzes for a course
     */
    private void loadCourseMaterials(String courseName) {
        // Clear panels
        materialsPanel.removeAll();
        quizPanel.removeAll();
        
        // Initialize quiz answers maps
        quizAnswerGroups = new HashMap<>();
        quizOptions = new HashMap<>();
        
        // Load materials
        List<String[]> materials = controller.getCourseMaterials(courseName);
        
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
                
                JButton viewButton = new JButton("View PDF");
                viewButton.addActionListener(e -> controller.openPDF(filePath));
                materialItem.add(viewButton, BorderLayout.EAST);
                
                materialsPanel.add(materialItem);
                materialsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        // Load quizzes
        List<Quiz> quizzes = controller.getCourseQuizzes(courseName);
        
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
            
            for (Quiz quiz : quizzes) {
                int quizId = quiz.getId();
                String question = quiz.getQuestion();
                List<String> options = quiz.getOptions();
                
                JPanel questionPanel = new JPanel();
                questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
                questionPanel.setBorder(BorderFactory.createEtchedBorder());
                questionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
                questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel questionLabel = new JLabel("<html><b>Question:</b> " + question + "</html>");
                questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionPanel.add(questionLabel);
                questionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                // Create radio buttons for options
                ButtonGroup optionGroup = new ButtonGroup();
                Map<String, JRadioButton> optionButtons = new HashMap<>();
                
                JPanel optionsPanel = new JPanel();
                optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
                optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                char optionLetter = 'A';
                for (String option : options) {
                    JRadioButton radioButton = new JRadioButton(optionLetter + ". " + option);
                    radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    optionGroup.add(radioButton);
                    optionsPanel.add(radioButton);
                    optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    
                    // Store the option mapping
                    optionButtons.put(String.valueOf(optionLetter), radioButton);
                    optionLetter++;
                }
                
                questionPanel.add(optionsPanel);
                
                // Store the button group and options for later submission
                quizAnswerGroups.put(quizId, optionGroup);
                quizOptions.put(quizId, optionButtons);
                
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
    
    /**
     * Submits quiz answers for grading
     */
    private void submitQuizAnswers(String courseName) {
        // Collect answers
        Map<Integer, String> answers = new HashMap<>();
        
        for (Map.Entry<Integer, ButtonGroup> entry : quizAnswerGroups.entrySet()) {
            int quizId = entry.getKey();
            ButtonGroup group = entry.getValue();
            Map<String, JRadioButton> options = quizOptions.get(quizId);
            
            String selectedOption = null;
            for (Map.Entry<String, JRadioButton> optionEntry : options.entrySet()) {
                if (optionEntry.getValue().isSelected()) {
                    selectedOption = optionEntry.getKey();
                    break;
                }
            }
            
            // Add answer if selected
            if (selectedOption != null) {
                answers.put(quizId, selectedOption);
            }
        }
        
        if (answers.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one answer before submitting.",
                "Quiz Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Submit answers to controller
        controller.submitQuizAnswers(currentUsername, courseName, answers);
        
        // Clear selections (handled by observer pattern)
        for (ButtonGroup group : quizAnswerGroups.values()) {
            group.clearSelection();
        }
    }
    
    /**
     * Logs out the current user
     */
    private void logout() {
        currentUsername = null;
        currentUserType = null;
        cardLayout.show(mainPanel, "login");
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            frontend app = new frontend();
            app.setVisible(true);
        });
    }
}

/**
 * Controller class for the MVC architecture
 * Mediates between the View (frontend) and Model (backend)
 */
class LMSController extends Observable {
    private backend model;
    private int lastQuizScore;
    
    public LMSController() {
        model = new backend();
    }
    
    public String loginUser(String username, String password) {
        return model.loginUser(username, password);
    }
    
    public boolean registerUser(String username, String password, String userType) {
        return model.registerUser(username, password, userType);
    }
    
    public List<String> getCourses() {
        return model.getCourses();
    }
    
    public boolean uploadMaterial(String courseName, File file) {
        boolean result = model.uploadMaterial(courseName, file);
        if (result) {
            setChanged();
            notifyObservers("COURSES_UPDATED");
        }
        return result;
    }
    
    public List<String[]> getCourseMaterials(String courseName) {
        return model.getCourseMaterials(courseName);
    }
    
    public List<Quiz> getCourseQuizzes(String courseName) {
        return model.getCourseQuizzes(courseName);
    }
    
    public void submitQuizAnswers(String username, String courseName, Map<Integer, String> answers) {
        lastQuizScore = model.submitQuizAnswers(username, courseName, answers);
        setChanged();
        notifyObservers("QUIZ_SUBMITTED");
    }
    
    public int getLastQuizScore() {
        return lastQuizScore;
    }
    
    public List<String[]> getStudentScores(String courseName) {
        return model.getStudentScores(courseName);
    }
    
    public List<String[]> getStudentScoreHistory(String username) {
        return model.getStudentScoreHistory(username);
    }
    
    public void openPDF(String filePath) {
        model.openPDF(filePath);
    }
}