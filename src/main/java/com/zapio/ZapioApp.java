package com.zapio;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Main application class for Zapio - Flashcards, Quizzes & Index Cards powered by AI
 */
public class ZapioApp {
    // Application dimensions
    public static final int APP_WIDTH = 900;
    public static final int APP_HEIGHT = 700;
    
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Screen identifiers
    public static final String UPLOAD_SCREEN = "UPLOAD_SCREEN";
    public static final String SELECTION_SCREEN = "SELECTION_SCREEN";
    public static final String FLASHCARD_SCREEN = "FLASHCARD_SCREEN";
    public static final String QUIZ_SCREEN = "QUIZ_SCREEN";
    public static final String RESULT_SCREEN = "RESULT_SCREEN";
    public static final String CHEATSHEET_SCREEN = "CHEATSHEET_SCREEN";
    
    /**
     * Constructor - initializes the application
     */
    public ZapioApp() {
        initializeFonts();
        setupUI();
    }
    
    /**
     * Registers custom fonts with the application
     */
    private void initializeFonts() {
        try {
            // Register Inter font family
            String fontPath = "/Users/hardikparajuli/Desktop/Valis/assets/fonts/";
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            // Register Inter Regular font
            Font interRegular = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "Inter-Regular.otf"));
            ge.registerFont(interRegular);
            
            // Register Inter Bold font
            Font interBold = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "Inter-Bold.otf"));
            ge.registerFont(interBold);
            
            // Register Dancing Script font
            Font dancingScript = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath + "Dancing Script OT.otf"));
            ge.registerFont(dancingScript);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading custom fonts: " + e.getMessage());
        }
    }
    
    /**
     * Sets up the main UI components
     */
    private void setupUI() {
        // Create main frame
        mainFrame = new JFrame("Zapio");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(APP_WIDTH, APP_HEIGHT);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null); // Center on screen
        
        // Create card layout for screen switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create and add screens
        UploadScreen uploadScreen = new UploadScreen(this);
        SelectionScreen selectionScreen = new SelectionScreen(this);
        
        mainPanel.add(uploadScreen, UPLOAD_SCREEN);
        mainPanel.add(selectionScreen, SELECTION_SCREEN);
        
        // Add main panel to frame
        mainFrame.add(mainPanel);
    }
    
    /**
     * Shows a specific screen
     * @param screenName The identifier of the screen to show
     */
    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }
    
    /**
     * Show the selection screen with the uploaded document
     * @param file The uploaded document file
     */
    public void showSelectionScreen(File file) {
        Component[] components = mainPanel.getComponents();
        for (Component component : components) {
            if (component instanceof SelectionScreen) {
                ((SelectionScreen) component).loadDocument(file);
                break;
            }
        }
        showScreen(SELECTION_SCREEN);
    }
    
    /**
     * Makes the application visible
     */
    public void show() {
        mainFrame.setVisible(true);
    }
    
    /**
     * Application entry point
     */
    public static void main(String[] args) {
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            ZapioApp app = new ZapioApp();
            app.show();
        });
    }
}
