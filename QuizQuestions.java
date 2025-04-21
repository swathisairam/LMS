import java.util.*;

public class QuizQuestions {
    private static final Map<String, List<QuizQuestion>> COURSE_QUESTIONS = new HashMap<>();
    
    static {
        // Initialize OOAD questions
        List<QuizQuestion> ooadQuestions = Arrays.asList(
            new QuizQuestion(
                "Which of the following design patterns is used to define a grammar for interpreting sentences in a language?",
                Arrays.asList("Factory Pattern", "Interpreter Pattern", "Singleton Pattern", "Adapter Pattern"),
                "B"
            ),
            new QuizQuestion(
                "In the Interpreter pattern, which class defines the interface for interpretation?",
                Arrays.asList("Context", "TerminalExpression", "AbstractExpression", "NonTerminalExpression"),
                "C"
            ),
            new QuizQuestion(
                "The primary role of the 'Context' class in the Interpreter pattern is to:",
                Arrays.asList("Provide concrete implementations", "Store global information during interpretation", 
                            "Create abstract syntax trees", "Invoke terminal expressions"),
                "B"
            ),
            new QuizQuestion(
                "What is the main benefit of using the Interpreter pattern?",
                Arrays.asList("High performance", "Simplifies complex grammar definitions", 
                            "Reduced memory consumption", "Automatic code generation"),
                "B"
            ),
            new QuizQuestion(
                "Which design pattern provides a way to access elements of a collection sequentially without exposing its internal representation?",
                Arrays.asList("Strategy Pattern", "Observer Pattern", "Iterator Pattern", "Decorator Pattern"),
                "C"
            ),
            new QuizQuestion(
                "In the Iterator design pattern, the role of ConcreteIterator is to:",
                Arrays.asList("Define an interface for creating an iterator", "Keep track of the current position in the traversal",
                            "Implement custom sorting logic", "Create collections dynamically"),
                "B"
            ),
            new QuizQuestion(
                "One of the key advantages of using the Iterator pattern is:",
                Arrays.asList("It provides high-speed performance", "It directly modifies the internal structure of the collection",
                            "It provides a consistent way to traverse collections", "It allows recursion in iteration"),
                "C"
            ),
            new QuizQuestion(
                "Which principle is most closely followed by the Iterator pattern?",
                Arrays.asList("Liskov Substitution Principle", "Single Responsibility Principle",
                            "Dependency Inversion Principle", "Interface Segregation Principle"),
                "B"
            ),
            new QuizQuestion(
                "In the context of Interpreter pattern, which class is not mandatory if the grammar has no rules involving other expressions?",
                Arrays.asList("Context", "AbstractExpression", "TerminalExpression", "NonTerminalExpression"),
                "D"
            ),
            new QuizQuestion(
                "A major disadvantage of the Interpreter pattern is:",
                Arrays.asList("Lack of flexibility", "Difficult to implement",
                            "High complexity for large grammars", "Tight coupling of classes"),
                "C"
            )
        );
        
        // Initialize Cloud Computing questions
        List<QuizQuestion> cloudQuestions = Arrays.asList(
            new QuizQuestion(
                "What does the 'C' in CAP Theorem stand for?",
                Arrays.asList("Concurrency", "Consistency", "Complexity", "Communication"),
                "B"
            ),
            new QuizQuestion(
                "Which two properties are guaranteed in a CP system under network partition?",
                Arrays.asList("Consistency and Availability", "Availability and Partition Tolerance",
                            "Consistency and Partition Tolerance", "None of the above"),
                "C"
            ),
            new QuizQuestion(
                "Which of the following statements is true about the CAP theorem?",
                Arrays.asList("A distributed system can achieve all three properties at all times",
                            "Only consistency and availability can be guaranteed",
                            "It's impossible to guarantee all three (C, A, P) simultaneously",
                            "Partition tolerance is not required in modern systems"),
                "C"
            ),
            new QuizQuestion(
                "In the context of CAP, what is Partition Tolerance?",
                Arrays.asList("The system continues to work even when some nodes crash",
                            "The system works correctly despite network communication failures",
                            "The system ensures all data is encrypted",
                            "The system can store large data across servers"),
                "B"
            ),
            new QuizQuestion(
                "What happens in an AP system when there's a network partition?",
                Arrays.asList("It stops all operations", "It ensures all data is consistent",
                            "It continues to serve requests but may return stale data",
                            "It blocks write operations"),
                "C"
            ),
            new QuizQuestion(
                "Which type of database typically emphasizes Consistency and Availability over Partition Tolerance?",
                Arrays.asList("NoSQL", "SQL (Relational databases)", "Time-series databases", "Graph databases"),
                "B"
            ),
            new QuizQuestion(
                "What kind of consistency does Cassandra provide?",
                Arrays.asList("Strong consistency", "Immediate consistency",
                            "Eventual consistency", "No consistency"),
                "C"
            ),
            new QuizQuestion(
                "In CAP theorem, when there is no partition, which properties can be satisfied?",
                Arrays.asList("Only consistency", "Only availability",
                            "Both consistency and availability", "Only partition tolerance"),
                "C"
            ),
            new QuizQuestion(
                "What real-world situation would most benefit from a CP system?",
                Arrays.asList("Social media news feed", "Online banking transaction service",
                            "Video streaming platform", "Sensor data collection"),
                "B"
            ),
            new QuizQuestion(
                "Why is CAP theorem particularly important in cloud and microservice architectures?",
                Arrays.asList("These systems don't require databases", "These systems don't experience partitions",
                            "They are distributed and rely on network communication",
                            "They always use SQL databases"),
                "C"
            )
        );
        
        // Initialize Computer Design questions
        List<QuizQuestion> compilerQuestions = Arrays.asList(
            new QuizQuestion(
                "Which phase of the compiler is responsible for checking variable declarations and type consistency?",
                Arrays.asList("Lexical Analysis", "Syntax Analysis", "Semantic Analysis", "Intermediate Code Generation"),
                "C"
            ),
            new QuizQuestion(
                "Which of the following is not a type of parser?",
                Arrays.asList("LL(1) Parser", "LR(0) Parser", "LALR(1) Parser", "RTL(1) Parser"),
                "D"
            ),
            new QuizQuestion(
                "In which phase is a parse tree constructed?",
                Arrays.asList("Lexical Analysis", "Syntax Analysis", "Code Optimization", "Code Generation"),
                "B"
            ),
            new QuizQuestion(
                "What does the 'FIRST' set of a non-terminal in a grammar represent?",
                Arrays.asList("All terminals that can appear in any production",
                            "All terminals that appear at the beginning of any string derived from the non-terminal",
                            "All non-terminals that follow a production",
                            "All symbols in the grammar"),
                "B"
            ),
            new QuizQuestion(
                "Which of the following grammars can be parsed by a predictive parser?",
                Arrays.asList("Ambiguous grammars", "Left-recursive grammars",
                            "Right-recursive grammars", "All context-free grammars"),
                "C"
            ),
            new QuizQuestion(
                "What does the YACC tool generate?",
                Arrays.asList("A lexical analyzer", "A syntax analyzer", "An assembler", "An interpreter"),
                "B"
            ),
            new QuizQuestion(
                "Which of the following is used to resolve shift-reduce conflicts in YACC?",
                Arrays.asList("FIRST and FOLLOW sets", "Symbol table",
                            "Precedence and associativity declarations", "Parse tree"),
                "C"
            ),
            new QuizQuestion(
                "Which intermediate code form is the most general?",
                Arrays.asList("Postfix notation", "Three-address code", "Quadruples", "Parse tree"),
                "B"
            ),
            new QuizQuestion(
                "Which LR parser is used by YACC?",
                Arrays.asList("LR(0)", "CLR(1)", "SLR(1)", "LALR(1)"),
                "D"
            ),
            new QuizQuestion(
                "In LR parsing, what does the parser produce?",
                Arrays.asList("Leftmost derivation", "Rightmost derivation",
                            "Leftmost derivation in reverse", "Rightmost derivation in reverse"),
                "D"
            )
        );
        
        // Initialize GENAI questions
        List<QuizQuestion> genaiQuestions = Arrays.asList(
            new QuizQuestion(
                "How do word embeddings differ from one-hot encoding?",
                Arrays.asList("One-hot encoding captures semantic relationships, embeddings do not",
                            "Word embeddings represent words as sparse vectors",
                            "Word embeddings capture semantic similarity, unlike one-hot encoding",
                            "One-hot encoding has fewer dimensions than embeddings"),
                "C"
            ),
            new QuizQuestion(
                "Which of the following is NOT an advantage of word embeddings?",
                Arrays.asList("Captures word relationships", "Reduces sparsity",
                            "Preserves exact word frequency", "Can improve NLP model performance"),
                "C"
            ),
            new QuizQuestion(
                "Which word embedding method predicts a word based on its surrounding words?",
                Arrays.asList("One-hot encoding", "Skip-gram", "TF-IDF", "CBOW"),
                "D"
            ),
            new QuizQuestion(
                "If the word vectors for 'king', 'queen', 'man', and 'woman' follow semantic relationships, which equation best represents the analogy?",
                Arrays.asList("king - queen = man - woman", "king - man + woman ≈ queen",
                            "king + queen = man + woman", "king + man - woman ≈ queen"),
                "B"
            ),
            new QuizQuestion(
                "Which of the following statements about Word2Vec is true?",
                Arrays.asList("Word2Vec uses convolutional layers to process text",
                            "It is a deep neural network with multiple hidden layers",
                            "It learns word associations by predicting words in context",
                            "It assigns fixed random vectors to words"),
                "C"
            ),
            new QuizQuestion(
                "Which of these is NOT a word embedding technique?",
                Arrays.asList("FastText", "Word2Vec", "BoW", "GloVe"),
                "C"
            ),
            new QuizQuestion(
                "Which type of neural network is commonly used to learn word embeddings?",
                Arrays.asList("RNN", "CNN", "Transformer", "Shallow neural network"),
                "D"
            ),
            new QuizQuestion(
                "What is the primary advantage of FastText over Word2Vec?",
                Arrays.asList("It considers subword information", "It reduces training time",
                            "It uses attention mechanisms", "It ignores rare words"),
                "A"
            ),
            new QuizQuestion(
                "Which of the following results in high-dimensional and sparse word representations?",
                Arrays.asList("Word2Vec", "One-hot encoding", "FastText", "GloVe"),
                "B"
            ),
            new QuizQuestion(
                "GloVe embeddings are trained using:",
                Arrays.asList("Co-occurrence matrix factorization", "Neural networks",
                            "Attention mechanisms", "Skip-gram method"),
                "A"
            )
        );
        
        // Initialize Robotics questions
        List<QuizQuestion> roboticsQuestions = Arrays.asList(
            new QuizQuestion(
                "What is the key advantage of the SIFT (Scale-Invariant Feature Transform) algorithm?",
                Arrays.asList("It is faster than SURF", "It is invariant to scale and rotation",
                            "It uses Haar wavelets for detection", "It works only with grayscale images"),
                "B"
            ),
            new QuizQuestion(
                "What does disparity refer to in stereo vision?",
                Arrays.asList("The distance between two cameras", "The brightness difference between two pixels",
                            "The horizontal shift between corresponding pixels in two images",
                            "The color contrast in an image"),
                "C"
            ),
            new QuizQuestion(
                "In line extraction from range data, segmentation refers to:",
                Arrays.asList("Removing noise from data", "Dividing data into meaningful groups for line fitting",
                            "Extracting only horizontal lines", "Fitting a circle to the data"),
                "B"
            ),
            new QuizQuestion(
                "What makes MSER (Maximally Stable Extremal Regions) detector unique?",
                Arrays.asList("It detects corners only", "It is sensitive to noise",
                            "It is invariant to affine transformations", "It requires 3D data"),
                "C"
            ),
            new QuizQuestion(
                "What is the purpose of camera calibration in stereo vision?",
                Arrays.asList("To increase image brightness", "To remove lens distortion",
                            "To find intrinsic and extrinsic parameters of the camera",
                            "To convert color images to grayscale"),
                "C"
            ),
            new QuizQuestion(
                "What is the main challenge in feature extraction using ultrasonic sensors?",
                Arrays.asList("They only work in dark environments", "They have extremely high precision",
                            "They are affected by noise and have low resolution", "They are too expensive"),
                "C"
            ),
            new QuizQuestion(
                "What is the advantage of multiple-hypothesis belief over single-hypothesis belief?",
                Arrays.asList("It uses less memory", "It only works with odometry data",
                            "It can recover from incorrect initial positions", "It requires no sensors"),
                "C"
            ),
            new QuizQuestion(
                "Which of the following is NOT a component of map-based localization?",
                Arrays.asList("Odometry", "Image filtering", "Map representation", "Pose estimation"),
                "B"
            ),
            new QuizQuestion(
                "Behavior-based navigation is best suited for:",
                Arrays.asList("General-purpose robots in unknown environments",
                            "Structured environments with predefined cues",
                            "Robots requiring 3D vision", "Robots with SLAM capabilities"),
                "B"
            ),
            new QuizQuestion(
                "In stereo vision, increasing the baseline (distance between cameras) generally results in:",
                Arrays.asList("Less accurate depth estimation", "More noise in image",
                            "More accurate depth estimation for close objects", "Better field of view"),
                "C"
            )
        );
        
        // Add all question sets to the map
        COURSE_QUESTIONS.put("OOAD", ooadQuestions);
        COURSE_QUESTIONS.put("Cloud Computing", cloudQuestions);
        COURSE_QUESTIONS.put("Computer Design", compilerQuestions);
        COURSE_QUESTIONS.put("GENAI", genaiQuestions);
        COURSE_QUESTIONS.put("Robotics", roboticsQuestions);
    }
    
    public static List<QuizQuestion> getQuestionsForCourse(String courseName) {
        return COURSE_QUESTIONS.getOrDefault(courseName, new ArrayList<>());
    }
}

class QuizQuestion {
    private String question;
    private List<String> options;
    private String correctAnswer;
    
    public QuizQuestion(String question, List<String> options, String correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public boolean isCorrectAnswer(String answer) {
        return correctAnswer.equals(answer);
    }
} 