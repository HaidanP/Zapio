package com.zapio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * Selection screen showing document preview and study options with a modern design.
 */
public class SelectionScreen extends JPanel {
    private DocumentPreviewPanel previewPanel;
    private JPanel optionsCardPanel; // Panel to hold the option cards
    private RoundedButton proceedButton; // Use the custom rounded button
    private File documentFile;
    private JPanel selectedOptionCard = null; // Track the selected card
    private final Color cardDefaultBg = new Color(245, 245, 245); // Light gray for cards
    private final Color cardHoverBg = new Color(230, 230, 230); // Slightly darker gray
    private final Color cardSelectedBg = Color.BLACK; // Black for selected
    private final Color cardSelectedFg = Color.WHITE; // White text for selected
    private final Color cardDefaultFg = Color.BLACK; // Black text for default

    public SelectionScreen(ZapioApp app) {
        setupUI(app);
    }

    /**
     * Sets up the UI components with the redesigned options panel.
     */
    private void setupUI(ZapioApp app) {
        setLayout(new BorderLayout(30, 0)); // Increased horizontal gap
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(40, 40, 40, 40)); // Increased padding

        // --- Left Panel: Document Preview ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15)); // Gap between title and preview
        leftPanel.setOpaque(false); // Use main panel background
        leftPanel.setPreferredSize(new Dimension(400, 600)); // Adjusted size

        JLabel previewTitle = new JLabel("<html><u>DOCUMENT PREVIEW</u></html>");
        previewTitle.setFont(new Font("SansSerif", Font.BOLD, 18)); // Slightly larger bold font
        previewTitle.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(previewTitle, BorderLayout.NORTH);

        previewPanel = new DocumentPreviewPanel();
        leftPanel.add(previewPanel, BorderLayout.CENTER);

        // --- Right Panel: Options ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 30)); // Vertical gap
        rightPanel.setOpaque(false);

        JLabel optionsTitle = new JLabel("What do you need?");
        optionsTitle.setFont(new Font("SansSerif", Font.BOLD, 36)); // Larger, bolder title
        optionsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(optionsTitle, BorderLayout.NORTH);

        // Panel to hold the option cards using GridBagLayout for centering
        optionsCardPanel = new JPanel(new GridBagLayout());
        optionsCardPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE; // Stack vertically
        gbc.weightx = 1.0; // Allow horizontal expansion/centering
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make cards fill width
        gbc.insets = new Insets(0, 0, 15, 0); // Vertical spacing between cards

        String[] options = {
            "Flash Cards",
            "Practice Quiz",
            // "Handwritten Index Card", // Temporarily disabled
            "Full Cheatsheet"
        };

        for (String option : options) {
            JPanel card = createOptionCard(option);
            optionsCardPanel.add(card, gbc);
        }
        
        // Add a filler component to push cards to the top if space allows
        gbc.weighty = 1.0; // Takes up remaining vertical space
        optionsCardPanel.add(Box.createVerticalGlue(), gbc);

        rightPanel.add(optionsCardPanel, BorderLayout.CENTER);

        // --- Proceed Button ---
        this.proceedButton = new RoundedButton("Proceed", 25); // Use RoundedButton with radius
        proceedButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        proceedButton.setBackground(Color.BLACK); // Explicitly set background
        proceedButton.setForeground(Color.WHITE); // Explicitly set text color
        proceedButton.setPreferredSize(new Dimension(200, 50)); // Make button larger
        
        proceedButton.addActionListener(e -> {  // Using e without referencing it
            String selected = getSelectedOptionText();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an option first.", "No Option Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (documentFile == null) {
                JOptionPane.showMessageDialog(this, "Please upload a document first.", "No Document", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            System.out.println("Proceed button clicked. File: " + documentFile.getName() + ", Option: " + selected);
            
            // Handle different options
            if ("Practice Quiz".equals(selected)) {
                generateQuiz(app);
            } else if ("Flash Cards".equals(selected)) {
                generateFlashcards(app);
            } else if ("Full Cheatsheet".equals(selected)) {
                generateCheatsheet(app);
            } else {
                // For other options
                JOptionPane.showMessageDialog(this, "This feature is not implemented yet: " + selected, 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Panel to center the button with bottom padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Padding above the button
        buttonPanel.add(proceedButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels to main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a styled, rounded panel (card) for an option.
     */
    private JPanel createOptionCard(String text) {
        // Main card panel with rounded corners (painted)
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Use card background color based on state
                if (this == selectedOptionCard) {
                    g2.setColor(cardSelectedBg);
                } else if (getClientProperty("hover") == Boolean.TRUE) {
                     g2.setColor(cardHoverBg);
                 } else {
                     g2.setColor(cardDefaultBg);
                 }
                // Fill rounded rectangle background
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20)); // Corner radius
                g2.dispose();
                super.paintComponent(g); // Ensure child components (like label) are painted
            }
            @Override
            public Dimension getPreferredSize() {
                // Ensure a minimum height for the cards
                return new Dimension(super.getPreferredSize().width, 60); 
            }
        };
        cardPanel.setOpaque(false); // We are painting the background ourselves
        cardPanel.setBorder(new EmptyBorder(15, 25, 15, 25)); // Padding inside the card
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.putClientProperty("optionText", text); // Store the option text

        // Label for the option text
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        // Set initial text color
        label.setForeground(cardDefaultFg); 
        cardPanel.add(label, BorderLayout.CENTER);

        // Mouse listener for hover and click effects
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cardPanel.putClientProperty("hover", true);
                cardPanel.repaint(); // Repaint to show hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cardPanel.putClientProperty("hover", false);
                cardPanel.repaint(); // Repaint to remove hover effect
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Deselect the previously selected card (if any)
                if (selectedOptionCard != null && selectedOptionCard != cardPanel) {
                    JLabel oldLabel = (JLabel) selectedOptionCard.getComponent(0);
                    oldLabel.setForeground(cardDefaultFg); // Reset text color
                    selectedOptionCard.repaint(); // Repaint old card
                }

                // Select the new card
                selectedOptionCard = cardPanel;
                label.setForeground(cardSelectedFg); // Set selected text color
                cardPanel.repaint(); // Repaint new selected card
            }
        });

        return cardPanel;
    }
    
    /**
     * Gets the text of the currently selected option card.
     * @return The option text, or null if no option is selected.
     */
    private String getSelectedOptionText() {
        if (selectedOptionCard != null) {
            return (String) selectedOptionCard.getClientProperty("optionText");
        }
        return null;
    }

    /**
     * Load and display a document in the preview panel.
     */
    public void loadDocument(File file) {
        this.documentFile = file;
        previewPanel.loadDocument(file);
        // Reset selection when a new document is loaded
        if (selectedOptionCard != null) {
             JLabel oldLabel = (JLabel) selectedOptionCard.getComponent(0);
             oldLabel.setForeground(cardDefaultFg); // Reset text color
             selectedOptionCard.repaint();
             selectedOptionCard = null;
         }
    }
    
    // Static reference to store the previous screen for navigation
    public static JComponent PREVIOUS_SCREEN = null;
    
    /**
     * Generate a quiz based on the current document
     */
    private void generateQuiz(ZapioApp app) {
        if (documentFile == null) {
            return; // Should never happen as we check before calling
        }
        
        // Store current panel as static reference to return to later
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        PREVIOUS_SCREEN = this;
        
        // Create and display loading screen
        LoadingScreen loadingScreen = new LoadingScreen("Generating questions from your document...");
        Container contentPane = frame.getContentPane();
        contentPane.removeAll();
        contentPane.add(loadingScreen);
        frame.validate();
        frame.repaint();
        loadingScreen.start();
        
        // Create quiz generator
        QuizGenerator generator = new QuizGenerator();
        
        // Generate questions asynchronously
        generator.generateQuestionsAsync(documentFile).thenAccept(questions -> {
            // Run on EDT
            SwingUtilities.invokeLater(() -> {
                loadingScreen.stop();
                
                if (questions.isEmpty()) {
                    // Handle error
                    contentPane.removeAll();
                    contentPane.add(this); // Go back to selection screen
                    JOptionPane.showMessageDialog(frame, 
                        "Failed to generate quiz questions. Please try again.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Show quiz screen
                    contentPane.removeAll();
                    contentPane.add(new QuizScreen(frame, questions));
                }
                
                frame.validate();
                frame.repaint();
            });
        });
    }
    
    /**
     * Generate flashcards based on the current document
     */
    private void generateFlashcards(ZapioApp app) {
        if (documentFile == null) {
            return; // Should never happen as we check before calling
        }
        
        // Store current panel as static reference to return to later
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        PREVIOUS_SCREEN = this;
        
        // Create and display loading screen
        LoadingScreen loadingScreen = new LoadingScreen("Generating flashcards from your document...");
        Container contentPane = frame.getContentPane();
        contentPane.removeAll();
        contentPane.add(loadingScreen);
        frame.validate();
        frame.repaint();
        loadingScreen.start();
        
        // Create flashcard generator
        FlashcardGenerator generator = new FlashcardGenerator();
        
        // Generate flashcards asynchronously
        generator.generateFlashcardsAsync(documentFile).thenAccept(flashcards -> {
            // Run on EDT
            SwingUtilities.invokeLater(() -> {
                loadingScreen.stop();
                
                if (flashcards.isEmpty()) {
                    // Handle error
                    contentPane.removeAll();
                    contentPane.add(this); // Go back to selection screen
                    JOptionPane.showMessageDialog(frame, 
                        "Failed to generate flashcards. Please try again.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Show flashcard screen
                    contentPane.removeAll();
                    contentPane.add(new FlashcardScreen(frame, flashcards));
                }
                
                frame.validate();
                frame.repaint();
            });
        });
    }
    
    /**
     * Generate a comprehensive cheatsheet based on the current document
     */
    private void generateCheatsheet(ZapioApp app) {
        if (documentFile == null) {
            return; // Should never happen as we check before calling
        }
        
        // Store current panel as static reference to return to later
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        PREVIOUS_SCREEN = this;
        
        // Create and display loading screen
        LoadingScreen loadingScreen = new LoadingScreen("Generating comprehensive cheatsheet from your document...");
        Container contentPane = frame.getContentPane();
        contentPane.removeAll();
        contentPane.add(loadingScreen);
        frame.validate();
        frame.repaint();
        loadingScreen.start();
        
        // Create cheatsheet generator
        CheatsheetGenerator generator = new CheatsheetGenerator();
        
        // Generate cheatsheet asynchronously
        generator.generateCheatsheetAsync(documentFile).thenAccept(cheatsheetContent -> {
            // Run on EDT
            SwingUtilities.invokeLater(() -> {
                loadingScreen.stop();
                
                if (cheatsheetContent == null || cheatsheetContent.isEmpty()) {
                    // Handle error
                    contentPane.removeAll();
                    contentPane.add(this); // Go back to selection screen
                    JOptionPane.showMessageDialog(frame, 
                        "Failed to generate cheatsheet. Please try again.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Show cheatsheet screen
                    contentPane.removeAll();
                    contentPane.add(new CheatsheetScreen(frame, documentFile, cheatsheetContent));
                }
                
                frame.validate();
                frame.repaint();
            });
        });
    }

    // Removed isSelected method as selection is now tracked by selectedOptionCard field
}
