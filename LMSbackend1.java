import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LMSBackend {
    private Connection conn;
    
    public LMSBackend() {
        try {
            // Initialize database connection
            String host = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : "localhost";
            String database = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : "LMS";
            String user = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : "root";
            String password = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "swathi2004";
            int port = 3306;
            
            conn = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database, user, password
            );
            
            // Initialize database tables if they don't exist
            initializeDatabase();
            
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeDatabase() {
        try {
            Statement stmt = conn.createStatement();
            
            // Create users table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "userType ENUM('Professor', 'Student') NOT NULL" +
                ")"
            );
            
            // Create courses table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS courses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "courseName VARCHAR(100) NOT NULL UNIQUE" +
                ")"
            );
            
            // Create materials table
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
            
            // Create quizzes table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS quizzes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "course_id INT NOT NULL, " +
                "quizName VARCHAR(100) NOT NULL, " +
                "question TEXT NOT NULL, " +
                "answer TEXT, " +
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
                "answer TEXT, " +
                "score INT, " +
                "submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (course_id) REFERENCES courses(id), " +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(id)" +
                ")"
            );
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
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
            
            // Generate quiz questions
            generateQuiz(courseId, courseName);
            
            return rowsAffected > 0;
        } catch (SQLException | IOException e) {
            System.err.println("Error uploading material: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
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
    
    private void generateQuiz(int courseId, String courseName) throws SQLException {
        String[] questions = {
            "Define a key concept in " + courseName,
            "Explain the importance of " + courseName,
            "Describe a major principle in " + courseName,
            "What is the relationship between concepts in " + courseName + "?",
            "How does " + courseName + " apply to real-world scenarios?"
        };
        
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO quizzes (course_id, quizName, question, answer) VALUES (?, ?, ?, ?)"
        );
        
        for (String question : questions) {
            stmt.setInt(1, courseId);
            stmt.setString(2, courseName + " Quiz");
            stmt.setString(3, question);
            stmt.setString(4, "Auto-generated answer");
            stmt.executeUpdate();
        }
        
        stmt.close();
    }
    
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
    
    public List<String[]> getCourseQuizzes(String courseName) {
        List<String[]> quizzes = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT q.id, q.question FROM quizzes q " +
                "JOIN courses c ON q.course_id = c.id " +
                "WHERE c.courseName = ?"
            );
            stmt.setString(1, courseName);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String[] quiz = new String[2];
                quiz[0] = rs.getString("id");
                quiz[1] = rs.getString("question");
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
    
    // New method to submit quiz answers and calculate score
    public int submitQuizAnswers(String username, String courseName, Map<Integer, String> answers) {
        int score = 0;
        
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
            
            // For each answer, calculate score and save submission
            PreparedStatement submitStmt = conn.prepareStatement(
                "INSERT INTO quiz_submissions (username, course_id, quiz_id, answer, score) VALUES (?, ?, ?, ?, ?)"
            );
            
            for (Map.Entry<Integer, String> entry : answers.entrySet()) {
                int quizId = entry.getKey();
                String answer = entry.getValue();
                
                // Simple scoring: non-empty answer gets a point
                int questionScore = answer.trim().isEmpty() ? 0 : 1;
                score += questionScore;
                
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
        
        return score;
    }
    
    // New method to get student scores for professors
    public List<String[]> getStudentScores(String courseName) {
        List<String[]> scores = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT qs.username, q.quizName, SUM(qs.score) as total_score, " +
                "COUNT(qs.id) as question_count, MAX(qs.submission_date) as submission_date " +
                "FROM quiz_submissions qs " +
                "JOIN quizzes q ON qs.quiz_id = q.id " +
                "JOIN courses c ON qs.course_id = c.id " +
                "WHERE c.courseName = ? " +
                "GROUP BY qs.username, q.quizName " +
                "ORDER BY qs.username, q.quizName"
            );
            stmt.setString(1, courseName);
            
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                String[] score = new String[4];
                score[0] = rs.getString("username");
                score[1] = rs.getString("quizName");
                int totalScore = rs.getInt("total_score");
                int questionCount = rs.getInt("question_count");
                score[2] = totalScore + "/" + questionCount;
                score[3] = dateFormat.format(rs.getTimestamp("submission_date"));
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
    
    // New method to get a student's score history
    public List<String[]> getStudentScoreHistory(String username) {
        List<String[]> scoreHistory = new ArrayList<>();
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT c.courseName, q.quizName, SUM(qs.score) as total_score, " +
                "COUNT(qs.id) as question_count, MAX(qs.submission_date) as submission_date " +
                "FROM quiz_submissions qs " +
                "JOIN quizzes q ON qs.quiz_id = q.id " +
                "JOIN courses c ON qs.course_id = c.id " +
                "WHERE qs.username = ? " +
                "GROUP BY c.courseName, q.quizName " +
                "ORDER BY qs.submission_date DESC"
            );
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                String[] score = new String[4];
                score[0] = rs.getString("courseName");
                score[1] = rs.getString("quizName");
                int totalScore = rs.getInt("total_score");
                int questionCount = rs.getInt("question_count");
                score[2] = totalScore + "/" + questionCount;
                score[3] = dateFormat.format(rs.getTimestamp("submission_date"));
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
