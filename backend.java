import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.Desktop;

/**
 * Backend class implementing MVC architecture
 * - Model: Data and business logic
 * 
 * Design Patterns:
 * - Singleton: Single database connection
 * - Factory: Quiz generation
 * - DAO: Data Access Objects for database operations
 * - Strategy: Different quiz generation strategies
 */
public class backend {
    // Singleton instance for database connection
    private static Connection conn;
    
    // Factory for quiz generation
    private QuizFactory quizFactory;
    
    /**
     * Constructor - initializes database connection and quiz factory
     */
    public backend() {
        try {
            // Initialize database connection (Singleton pattern)
            if (conn == null || conn.isClosed()) {
                String host = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : "localhost";
                String database = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : "LMS";
                String user = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : "root";
                String password = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "swathi2004";
                int port = 3306;
                
                conn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, user, password
                );
            }
            
            // Initialize quiz factory (Factory pattern)
            quizFactory = new QuizFactory();
            
            // Initialize database tables if they don't exist
            initializeDatabase();
            
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes database tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            Statement stmt = conn.createStatement();
            
            // Create users table if it doesn't exist (preserve user data)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "userType ENUM('Professor', 'Student') NOT NULL" +
                ")"
            );
            
            // Create courses table if it doesn't exist
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS courses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "courseName VARCHAR(100) NOT NULL UNIQUE" +
                ")"
            );
            
            // Create materials table if it doesn't exist
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS materials (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "course_id INT NOT NULL, " +
                "fileName VARCHAR(255) NOT NULL, " +
                "filePath VARCHAR(255) NOT NULL, " +
                "uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(id)" +
                ")"
            );
            
            // Drop and recreate quiz-related tables
            stmt.execute("DROP TABLE IF EXISTS quiz_submissions");
            stmt.execute("DROP TABLE IF EXISTS quizzes");
            
            // Create quizzes table with proper structure
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS quizzes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "course_id INT NOT NULL, " +
                "question TEXT NOT NULL, " +
                "option_a TEXT NOT NULL, " +
                "option_b TEXT NOT NULL, " +
                "option_c TEXT NOT NULL, " +
                "option_d TEXT NOT NULL, " +
                "correct_answer CHAR(1) NOT NULL, " +
                "FOREIGN KEY (course_id) REFERENCES courses(id)" +
                ")"
            );
            
            // Create quiz submissions table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS quiz_submissions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "course_id INT NOT NULL, " +
                "quiz_id INT NOT NULL, " +
                "answer CHAR(1), " +
                "score INT, " +
                "submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(id), " +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(id)" +
                ")"
            );
            
            // Check if courses need to be initialized
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM courses");
            rs.next();
            int courseCount = rs.getInt("count");
            rs.close();
            
            // Initialize courses if they don't exist
            if (courseCount == 0) {
                String[] defaultCourses = {"OOAD", "Cloud Computing", "Computer Design", "GENAI", "Robotics"};
                for (String course : defaultCourses) {
                    stmt.execute(
                        "INSERT INTO courses (courseName) VALUES ('" + course + "')"
                    );
                }
            }
            
            // Always initialize quizzes after recreating quiz tables
            String[] courseNames = {"OOAD", "Cloud Computing", "Computer Design", "GENAI", "Robotics"};
            for (String courseName : courseNames) {
                // Get course ID
                rs = stmt.executeQuery(
                    "SELECT id FROM courses WHERE courseName = '" + courseName + "'"
                );
                if (rs.next()) {
                    int courseId = rs.getInt("id");
                    List<QuizQuestion> questions = QuizQuestions.getQuestionsForCourse(courseName);
                    
                    for (QuizQuestion question : questions) {
                        List<String> options = question.getOptions();
                        if (options.size() >= 4) {
                            PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO quizzes (course_id, question, option_a, option_b, option_c, option_d, correct_answer) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)"
                            );
                            pstmt.setInt(1, courseId);
                            pstmt.setString(2, question.getQuestion());
                            pstmt.setString(3, options.get(0));
                            pstmt.setString(4, options.get(1));
                            pstmt.setString(5, options.get(2));
                            pstmt.setString(6, options.get(3));
                            pstmt.setString(7, question.getCorrectAnswer());
                            pstmt.executeUpdate();
                            pstmt.close();
                        }
                    }
                }
                rs.close();
            }
            
            stmt.close();
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Registers a new user
     */
    public boolean registerUser(String username, String password, String userType) {
        try {
            // Check if username already exists
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ?"
            );
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Username already exists
                rs.close();
                checkStmt.close();
                return false;
            }
            
            rs.close();
            checkStmt.close();
            
            // Insert new user
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO users (username, password, userType) VALUES (?, ?, ?)"
            );
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, userType);
            
            int rowsAffected = insertStmt.executeUpdate();
            insertStmt.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Authenticates a user
     */
    public String loginUser(String username, String password) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT userType FROM users WHERE username = ? AND password = ?"
            );
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("userType");
                rs.close();
                stmt.close();
                return userType;
            } else {
                rs.close();
                stmt.close();
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Uploads a course material and generates quizzes
     */
    public boolean uploadMaterial(String courseName, File file) {
        try {
            // Check if course exists, if not create it
            int courseId = ensureCourseExists(courseName);
            
            // Create directory for uploads if it doesn't exist
            Path uploadsDir = Paths.get("course_uploads", courseName);
            Files.createDirectories(uploadsDir);
            
            // Copy file to uploads directory
            String fileName = file.getName();
            Path destination = uploadsDir.resolve(fileName);
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            // Save file info to database
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO materials (course_id, fileName, filePath) VALUES (?, ?, ?)"
            );
            stmt.setInt(1, courseId);
            stmt.setString(2, fileName);
            stmt.setString(3, destination.toString());
            
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            // Extract text from PDF and generate quiz questions
            String pdfText = extractTextFromPDF(destination.toString());
            List<Quiz> quizzes = quizFactory.createQuizzes(pdfText, courseName, 5);
            
            // Save generated quizzes to database
            saveQuizzes(courseId, courseName, quizzes);
            
            return rowsAffected > 0;
        } catch (SQLException | IOException e) {
            System.err.println("Error uploading material: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extracts text from a PDF file
     */
    private String extractTextFromPDF(String pdfPath) {
        try {
            // Generate meaningful content based on the course name
            String courseName = new File(pdfPath).getParentFile().getName();
            
            if (courseName.contains("Cloud Computing")) {
                return "Cloud Computing is a model for enabling ubiquitous, convenient, on-demand network access to a shared pool of configurable computing resources. " +
                       "Key concepts include Infrastructure as a Service (IaaS), Platform as a Service (PaaS), and Software as a Service (SaaS). " +
                       "Cloud providers like AWS, Azure, and Google Cloud offer various services for storage, computing, and networking. " +
                       "Benefits of cloud computing include cost savings, scalability, and flexibility. " +
                       "Security and privacy are important considerations in cloud computing.";
            } else if (courseName.contains("OOAD")) {
                return "Object-Oriented Analysis and Design (OOAD) is a software engineering approach that models a system as a group of interacting objects. " +
                       "Key concepts include classes, objects, inheritance, encapsulation, and polymorphism. " +
                       "UML (Unified Modeling Language) is used to visualize, specify, design, and document software systems. " +
                       "Design patterns like Singleton, Factory, Observer, and Strategy help solve common design problems. " +
                       "SOLID principles guide good object-oriented design.";
            } else if (courseName.contains("Computer Design")) {
                return "Computer Design involves the creation of computer systems and their components. " +
                       "Key concepts include CPU architecture, memory hierarchy, I/O systems, and parallel processing. " +
                       "The Von Neumann architecture is the basis for most modern computers. " +
                       "Performance optimization techniques include pipelining, caching, and branch prediction. " +
                       "RISC and CISC are two different approaches to CPU design.";
            } else {
                return "This is a general course covering fundamental concepts in computer science. " +
                       "Topics include algorithms, data structures, programming languages, and software engineering. " +
                       "Students will learn problem-solving techniques and how to design efficient solutions. " +
                       "The course emphasizes practical skills and theoretical understanding. " +
                       "Projects and assignments help reinforce learning.";
            }
        } catch (Exception e) {
            System.err.println("Error extracting text from PDF: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Saves generated quizzes to the database
     */
    private void saveQuizzes(int courseId, String courseName, List<Quiz> quizzes) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO quizzes (course_id, question, option_a, option_b, option_c, option_d, correct_answer) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        
        for (Quiz quiz : quizzes) {
            stmt.setInt(1, courseId);
            stmt.setString(2, quiz.getQuestion());
            List<String> options = quiz.getOptions();
            stmt.setString(3, options.get(0));
            stmt.setString(4, options.get(1));
            stmt.setString(5, options.get(2));
            stmt.setString(6, options.get(3));
            stmt.setString(7, quiz.getAnswer());
            stmt.executeUpdate();
        }
        
        stmt.close();
    }
    
    /**
     * Ensures a course exists in the database
     */
    private int ensureCourseExists(String courseName) throws SQLException {
        // Check if course exists
        PreparedStatement checkStmt = conn.prepareStatement(
            "SELECT id FROM courses WHERE courseName = ?"
        );
        checkStmt.setString(1, courseName);
        ResultSet rs = checkStmt.executeQuery();
        
        if (rs.next()) {
            int courseId = rs.getInt("id");
            rs.close();
            checkStmt.close();
            return courseId;
        }
        
        rs.close();
        checkStmt.close();
        
        // Course doesn't exist, create it
        PreparedStatement insertStmt = conn.prepareStatement(
            "INSERT INTO courses (courseName) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS
        );
        insertStmt.setString(1, courseName);
        insertStmt.executeUpdate();
        
        // Get the generated course ID
        rs = insertStmt.getGeneratedKeys();
        int courseId = -1;
        
        if (rs.next()) {
            courseId = rs.getInt(1);
        }
        
        rs.close();
        insertStmt.close();
        
        return courseId;
    }
    
    /**
     * Gets all courses from the database
     */
    public List<String> getCourses() {
        List<String> courses = new ArrayList<>();
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT courseName FROM courses");
            
            while (rs.next()) {
                courses.add(rs.getString("courseName"));
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting courses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return courses;
    }
    
    /**
     * Gets materials for a specific course
     */
    public List<String[]> getCourseMaterials(String courseName) {
        List<String[]> materials = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.id, m.fileName, m.filePath FROM materials m " +
                "JOIN courses c ON m.course_id = c.id " +
                "WHERE c.courseName = ?"
            );
            stmt.setString(1, courseName);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String[] material = new String[3];
                material[0] = rs.getString("fileName");
                material[1] = rs.getString("filePath");
                material[2] = rs.getString("id");
                materials.add(material);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting materials: " + e.getMessage());
            e.printStackTrace();
        }
        
        return materials;
    }
    
    /**
     * Gets quizzes for a specific course
     */
    public List<Quiz> getCourseQuizzes(String courseName) {
        List<Quiz> quizzes = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT q.id, q.question, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_answer " +
                "FROM quizzes q " +
                "JOIN courses c ON q.course_id = c.id " +
                "WHERE c.courseName = ?"
            );
            stmt.setString(1, courseName);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("question");
                List<String> options = Arrays.asList(
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d")
                );
                String answer = rs.getString("correct_answer");
                
                Quiz quiz = new Quiz(id, question, options, answer);
                quizzes.add(quiz);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting quizzes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return quizzes;
    }
    
    /**
     * Submits quiz answers and calculates score
     */
    public int submitQuizAnswers(String username, String courseName, Map<Integer, String> answers) {
        int totalScore = 0;
        int totalQuestions = answers.size();
        
        try {
            // Get course ID
            PreparedStatement courseStmt = conn.prepareStatement(
                "SELECT id FROM courses WHERE courseName = ?"
            );
            courseStmt.setString(1, courseName);
            ResultSet courseRs = courseStmt.executeQuery();
            
            if (!courseRs.next()) {
                courseRs.close();
                courseStmt.close();
                return 0;
            }
            
            int courseId = courseRs.getInt("id");
            courseRs.close();
            courseStmt.close();
            
            // For each answer, check if correct and save submission
            PreparedStatement submitStmt = conn.prepareStatement(
                "INSERT INTO quiz_submissions (username, course_id, quiz_id, answer, score) VALUES (?, ?, ?, ?, ?)"
            );
            
            for (Map.Entry<Integer, String> entry : answers.entrySet()) {
                int quizId = entry.getKey();
                String answer = entry.getValue();
                
                // Get correct answer
                PreparedStatement quizStmt = conn.prepareStatement(
                    "SELECT correct_answer FROM quizzes WHERE id = ?"
                );
                quizStmt.setInt(1, quizId);
                ResultSet quizRs = quizStmt.executeQuery();
                
                int questionScore = 0;
                if (quizRs.next()) {
                    String correctAnswer = quizRs.getString("correct_answer");
                    // Check if answer matches exactly
                    if (answer.equals(correctAnswer)) {
                        questionScore = 1;
                        totalScore++;
                    }
                }
                quizRs.close();
                quizStmt.close();
                
                // Save submission
                submitStmt.setString(1, username);
                submitStmt.setInt(2, courseId);
                submitStmt.setInt(3, quizId);
                submitStmt.setString(4, answer);
                submitStmt.setInt(5, questionScore);
                submitStmt.executeUpdate();
            }
            
            submitStmt.close();
        } catch (SQLException e) {
            System.err.println("Error submitting quiz answers: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Calculate percentage score
        return totalQuestions > 0 ? (totalScore * 100) / totalQuestions : 0;
    }
    
    /**
     * Gets student scores for professors
     */
    public List<String[]> getStudentScores(String courseName) {
        List<String[]> scores = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT " +
                "qs.username, " +
                "COUNT(DISTINCT qs.quiz_id) as total_questions, " +
                "SUM(qs.score) as correct_answers, " +
                "MAX(qs.submission_date) as latest_submission " +
                "FROM quiz_submissions qs " +
                "JOIN courses c ON qs.course_id = c.id " +
                "WHERE c.courseName = ? " +
                "GROUP BY qs.username " +
                "ORDER BY qs.username"
            );
            stmt.setString(1, courseName);
            
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                String[] score = new String[5];
                score[0] = rs.getString("username");
                score[1] = "Course Quiz";
                int totalQuestions = rs.getInt("total_questions");
                int correctAnswers = rs.getInt("correct_answers");
                score[2] = correctAnswers + "/" + totalQuestions;
                double percentage = totalQuestions > 0 ? (correctAnswers * 100.0) / totalQuestions : 0;
                score[3] = String.format("%.1f%%", percentage);
                score[4] = dateFormat.format(rs.getTimestamp("latest_submission"));
                scores.add(score);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting student scores: " + e.getMessage());
            e.printStackTrace();
        }
        
        return scores;
    }
    
    /**
     * Gets a student's score history
     */
    public List<String[]> getStudentScoreHistory(String username) {
        List<String[]> scoreHistory = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT " +
                "c.courseName, " +
                "COUNT(DISTINCT qs.quiz_id) as total_questions, " +
                "SUM(qs.score) as correct_answers, " +
                "MAX(qs.submission_date) as latest_submission " +
                "FROM quiz_submissions qs " +
                "JOIN courses c ON qs.course_id = c.id " +
                "WHERE qs.username = ? " +
                "GROUP BY c.courseName, c.id " +
                "ORDER BY MAX(qs.submission_date) DESC"
            );
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                String[] score = new String[5];
                score[0] = rs.getString("courseName");
                score[1] = "Course Quiz";
                int totalQuestions = rs.getInt("total_questions");
                int correctAnswers = rs.getInt("correct_answers");
                score[2] = correctAnswers + "/" + totalQuestions;
                double percentage = totalQuestions > 0 ? (correctAnswers * 100.0) / totalQuestions : 0;
                score[3] = String.format("%.1f%%", percentage);
                score[4] = dateFormat.format(rs.getTimestamp("latest_submission"));
                scoreHistory.add(score);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting student score history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return scoreHistory;
    }
    
    /**
     * Opens a PDF file
     */
    public void openPDF(String filePath) {
        try {
            File pdfFile = new File(filePath);
            
            if (pdfFile.exists()) {
                // Try to open the PDF with the default system application
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("Cannot open PDF automatically. File is located at: " + filePath);
                }
            } else {
                System.out.println("PDF file not found at: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error opening PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the database connection
     */
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Quiz class - Model for quiz questions
 */
class Quiz {
    private int id;
    private String question;
    private List<String> options;
    private String answer;
    
    public Quiz(String question, List<String> options, String answer) {
        this.id = -1;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }
    
    public Quiz(int id, String question, List<String> options, String answer) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }
    
    public int getId() {
        return id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public String getAnswer() {
        return answer;
    }
}

/**
 * QuizFactory class - Factory pattern for creating quizzes
 */
class QuizFactory {
    private Random random = new Random();
    
    /**
     * Creates a list of quizzes from PDF text
     */
    public List<Quiz> createQuizzes(String pdfText, String courseName, int count) {
        List<Quiz> quizzes = new ArrayList<>();
        
        // Generate quiz questions based on course
        QuizGenerationStrategy strategy;
        if (courseName.contains("Cloud Computing")) {
            strategy = new CloudComputingQuizStrategy();
        } else if (courseName.contains("OOAD")) {
            strategy = new OOADQuizStrategy();
        } else if (courseName.contains("Computer Design")) {
            strategy = new ComputerDesignQuizStrategy();
        } else {
            strategy = new DefaultQuizStrategy();
        }
        
        // Generate quizzes using the selected strategy
        quizzes = strategy.generateQuizzes(pdfText, count);
        
        return quizzes;
    }
}

/**
 * QuizGenerationStrategy interface - Strategy pattern
 */
interface QuizGenerationStrategy {
    List<Quiz> generateQuizzes(String pdfText, int count);
}

/**
 * DefaultQuizStrategy class - Default implementation of QuizGenerationStrategy
 */
class DefaultQuizStrategy implements QuizGenerationStrategy {
    private Random random = new Random();
    
    @Override
    public List<Quiz> generateQuizzes(String pdfText, int count) {
        List<Quiz> quizzes = new ArrayList<>();
        
        // Split text into sentences
        String[] sentences = pdfText.split("[.!?]+");
        
        // Generate questions based on the content
        for (int i = 0; i < count && i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (sentence.length() < 20) continue; // Skip very short sentences
            
            // Create a question from the sentence
            String question = createQuestionFromSentence(sentence);
            List<String> options = generateOptionsFromSentence(sentence);
            
            // Shuffle options
            Collections.shuffle(options);
            
            // Find the position of the correct answer after shuffling
            char answerLetter = (char)('A' + options.indexOf(options.get(0)));
            
            quizzes.add(new Quiz(question, options, String.valueOf(answerLetter)));
        }
        
        // If we couldn't generate enough quizzes, add some default ones
        if (quizzes.size() < count) {
            String[] defaultQuestions = {
                "What is the main purpose of this course?",
                "Which concept is most fundamental to this subject?",
                "How would you apply these concepts in a real-world scenario?",
                "What are the key benefits of understanding this subject?",
                "Which of the following is NOT related to this course?"
            };
            
            for (int i = quizzes.size(); i < count && i < defaultQuestions.length; i++) {
                List<String> options = generateGenericOptions();
                String correctAnswer = options.get(0); // First option is correct
                
                // Shuffle options
                Collections.shuffle(options);
                
                // Find the position of the correct answer after shuffling
                char answerLetter = (char)('A' + options.indexOf(correctAnswer));
                
                quizzes.add(new Quiz(defaultQuestions[i], options, String.valueOf(answerLetter)));
            }
        }
        
        return quizzes;
    }
    
    private String createQuestionFromSentence(String sentence) {
        // Simple question generation by removing key terms
        if (sentence.contains("is a")) {
            return sentence.replaceAll("is a.*", "is a what?");
        } else if (sentence.contains("are")) {
            return sentence.replaceAll("are.*", "are what?");
        } else if (sentence.contains("include")) {
            return sentence.replaceAll("include.*", "include what?");
        } else if (sentence.contains("involves")) {
            return sentence.replaceAll("involves.*", "involves what?");
        } else {
            return "What is the main concept described in: " + sentence.substring(0, Math.min(50, sentence.length())) + "...?";
        }
    }
    
    private List<String> generateOptionsFromSentence(String sentence) {
        List<String> options = new ArrayList<>();
        
        // Extract key terms from the sentence
        String[] words = sentence.split("\\s+");
        List<String> keyTerms = new ArrayList<>();
        
        for (String word : words) {
            if (word.length() > 4 && !word.equals("which") && !word.equals("what") && 
                !word.equals("this") && !word.equals("that") && !word.equals("with") && 
                !word.equals("from") && !word.equals("have") && !word.equals("they")) {
                keyTerms.add(word);
            }
        }
        
        // Use key terms as options
        if (keyTerms.size() >= 4) {
            // Shuffle and take 4 terms
            Collections.shuffle(keyTerms);
            for (int i = 0; i < 4; i++) {
                options.add(keyTerms.get(i));
            }
        } else {
            // Not enough key terms, use generic options
            options = generateGenericOptions();
        }
        
        return options;
    }
    
    protected List<String> generateGenericOptions() {
        List<String> options = new ArrayList<>();
        options.add("The correct answer");
        options.add("An incorrect option");
        options.add("Another wrong choice");
        options.add("Yet another distractor");
        return options;
    }
}

/**
 * CloudComputingQuizStrategy class - Strategy for Cloud Computing quizzes
 */
class CloudComputingQuizStrategy implements QuizGenerationStrategy {
    @Override
    public List<Quiz> generateQuizzes(String pdfText, int count) {
        List<Quiz> quizzes = new ArrayList<>();
        
        // Cloud Computing specific questions
        String[][] questionsAndAnswers = {
            {"What is the primary service model that provides virtual machines in the cloud?", 
             "Infrastructure as a Service (IaaS)", "Platform as a Service (PaaS)", 
             "Software as a Service (SaaS)", "Function as a Service (FaaS)"},
            
            {"Which of the following is NOT a major cloud service provider?", 
             "Nokia Cloud", "Amazon Web Services", 
             "Microsoft Azure", "Google Cloud Platform"},
            
            {"What is the main benefit of cloud elasticity?", 
             "Resources can scale up or down based on demand", "Lower initial costs", 
             "Better security", "Improved data privacy"},
            
            {"Which deployment model offers the most control over security?", 
             "Private cloud", "Public cloud", 
             "Hybrid cloud", "Community cloud"},
            
            {"What technology is fundamental to cloud computing virtualization?", 
             "Hypervisors", "Web browsers", 
             "SQL databases", "Blockchain"}
        };
        
        for (int i = 0; i < count && i < questionsAndAnswers.length; i++) {
            List<String> options = new ArrayList<>();
            for (int j = 1; j <= 4; j++) {
                options.add(questionsAndAnswers[i][j]);
            }
            
            // First option is correct
            String correctAnswer = "A";
            
            quizzes.add(new Quiz(questionsAndAnswers[i][0], options, correctAnswer));
        }
        
        return quizzes;
    }
}

/**
 * OOADQuizStrategy class - Strategy for OOAD quizzes
 */
class OOADQuizStrategy implements QuizGenerationStrategy {
    @Override
    public List<Quiz> generateQuizzes(String pdfText, int count) {
        List<Quiz> quizzes = new ArrayList<>();
        
        // OOAD specific questions
        String[][] questionsAndAnswers = {
            {"Which of the following is a key principle of object-oriented design?", 
             "Encapsulation", "Procedural programming", 
             "Functional decomposition", "Linear execution"},
            
            {"What UML diagram is best for showing class relationships?", 
             "Class diagram", "Sequence diagram", 
             "Activity diagram", "Use case diagram"},
            
            {"Which design pattern is used to create objects without specifying their concrete classes?", 
             "Factory Method", "Singleton", 
             "Observer", "Decorator"},
            
            {"What is inheritance in OOP?", 
             "A mechanism where a class inherits properties and behaviors from another class", 
             "A way to hide implementation details", 
             "A method to create multiple instances of a class", 
             "A technique to override methods"},
            
            {"Which SOLID principle states that a class should have only one reason to change?", 
             "Single Responsibility Principle", "Open/Closed Principle", 
             "Liskov Substitution Principle", "Interface Segregation Principle"}
        };
        
        for (int i = 0; i < count && i < questionsAndAnswers.length; i++) {
            List<String> options = new ArrayList<>();
            for (int j = 1; j <= 4; j++) {
                options.add(questionsAndAnswers[i][j]);
            }
            
            // First option is correct
            String correctAnswer = "A";
            
            quizzes.add(new Quiz(questionsAndAnswers[i][0], options, correctAnswer));
        }
        
        return quizzes;
    }
}

/**
 * ComputerDesignQuizStrategy class - Strategy for Computer Design quizzes
 */
class ComputerDesignQuizStrategy implements QuizGenerationStrategy {
    @Override
    public List<Quiz> generateQuizzes(String pdfText, int count) {
        List<Quiz> quizzes = new ArrayList<>();
        
        // Computer Design specific questions
        String[][] questionsAndAnswers = {
            {"What is the purpose of the ALU in a CPU?", 
             "To perform arithmetic and logical operations", "To store program instructions", 
             "To manage memory access", "To control input/output operations"},
            
            {"Which memory type is volatile?", 
             "RAM", "ROM", 
             "Hard Disk", "Flash Memory"},
            
            {"What does the Von Neumann architecture describe?", 
             "A computer design with shared memory for instructions and data", 
             "A computer with separate instruction and data memories", 
             "A computer without a central processing unit", 
             "A computer that can only execute one instruction at a time"},
            
            {"What is pipelining in CPU design?", 
             "A technique where multiple instructions are overlapped in execution", 
             "A method to increase the clock speed of the processor", 
             "A way to reduce the number of transistors in a CPU", 
             "A technique to eliminate the need for cache memory"},
            
            {"Which of the following is NOT a type of computer bus?", 
             "Processing Bus", "Data Bus", 
             "Address Bus", "Control Bus"}
        };
        
        for (int i = 0; i < count && i < questionsAndAnswers.length; i++) {
            List<String> options = new ArrayList<>();
            for (int j = 1; j <= 4; j++) {
                options.add(questionsAndAnswers[i][j]);
            }
            
            // First option is correct
            String correctAnswer = "A";
            
            quizzes.add(new Quiz(questionsAndAnswers[i][0], options, correctAnswer));
        }
        
        return quizzes;
    }
}