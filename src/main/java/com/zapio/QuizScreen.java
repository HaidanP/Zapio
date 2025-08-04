package com.zapio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Screen to display quiz questions and collect user answers with a modern, elegant UI
 */
public class QuizScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private final List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private final int[] userAnswers;
    private final JPanel contentPanel;
    private final JLabel questionLabel;
    private final JPanel[] optionPanels;
    private final JLabel[] optionLabels;
    private final ButtonGroup optionGroup;
    private final JButton nextButton;
    private final JButton backButton;
    private final JFrame parentFrame;
    private final JLabel progressLabel;
    private final JProgressBar progressBar;
    
    // Colors for the modern UI - Black and White palette
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color CARD_BACKGROUND = new Color(250, 250, 250);
    private static final Color PRIMARY_TEXT = Color.BLACK;
    private static final Color SECONDARY_TEXT = new Color(70, 70, 70);
    private static final Color ACCENT_COLOR = Color.BLACK;
    private static final Color OPTION_HOVER = new Color(240, 240, 240);
    private static final Color OPTION_SELECTED = new Color(220, 220, 220);
    private static final Color OPTION_BORDER = new Color(200, 200, 200);
    
    public QuizScreen(JFrame parentFrame, List<QuizQuestion> questions) {
        this.parentFrame = parentFrame;
        this.questions = questions;
        this.userAnswers = new int[questions.size()];
        
        // Initialize with -1 (no answer selected)
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1;
        }
        
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);
        
        // Create header with elegant spacing
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(30, 40, 20, 40));
        
        JLabel titleLabel = new JLabel("Practice Time!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("10 Must-Know Questions.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress indicator
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setBackground(BACKGROUND_COLOR);
        progressPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(1); // Start with question 1
        
        // Force black color with custom UI that overrides any Look & Feel settings
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() { return Color.BLACK; }
            @Override
            protected Color getSelectionForeground() { return Color.BLACK; }
            @Override
            public void paint(Graphics g, JComponent c) {
                // Paint the track
                g.setColor(new Color(240, 240, 240)); // Light gray background
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                
                // Paint the progress
                Rectangle progressRect = getProgressRectangle(c);
                g.setColor(Color.BLACK);
                g.fillRect(progressRect.x, progressRect.y, progressRect.width, progressRect.height);
            }
            
            private Rectangle getProgressRectangle(JComponent c) {
                JProgressBar bar = (JProgressBar) c;
                int width = (int) (bar.getWidth() * ((double) bar.getValue() / bar.getMaximum()));
                return new Rectangle(0, 0, width, bar.getHeight());
            }
        });
        
        progressBar.setForeground(Color.BLACK);
        progressBar.setBackground(OPTION_BORDER);
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 6));
        progressBar.setBorderPainted(false);
        
        progressLabel = new JLabel("Question 1/" + questions.size());
        progressLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        progressLabel.setForeground(SECONDARY_TEXT);
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.EAST);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(progressPanel);
        
        // Create main content panel with card-like appearance
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OPTION_BORDER, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add subtle shadow to content panel
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(5, 0.2f),
            contentPanel.getBorder()
        ));
        
        // Question label with better typography and text wrapping
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        questionLabel.setForeground(PRIMARY_TEXT);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Enable HTML for text wrapping
        questionLabel.putClientProperty("html.disable", Boolean.FALSE);
        contentPanel.add(questionLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // Option panels with modern card-like design
        optionPanels = new JPanel[4];
        optionLabels = new JLabel[4];
        optionGroup = new ButtonGroup();
        ButtonModel[] models = new ButtonModel[4];
        
        for (int i = 0; i < 4; i++) {
            final int optionIndex = i;
            
            // Create hidden radio button for selection state
            JRadioButton radioButton = new JRadioButton();
            radioButton.setVisible(false);
            models[i] = radioButton.getModel();
            optionGroup.add(radioButton);
            
            // Create panel for each option with hover effects
            optionPanels[i] = new JPanel(new BorderLayout());
            optionPanels[i].setBackground(BACKGROUND_COLOR);
            optionPanels[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OPTION_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            optionPanels[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            optionPanels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Option label
            optionLabels[i] = new JLabel();
            optionLabels[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionLabels[i].setForeground(PRIMARY_TEXT);
            optionLabels[i].setHorizontalAlignment(SwingConstants.LEFT);
            
            optionPanels[i].add(optionLabels[i], BorderLayout.WEST);
            
            // Add hover and selection effects with improved reliability
            optionPanels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!models[optionIndex].isSelected()) {
                        optionPanels[optionIndex].setBackground(OPTION_HOVER);
                        optionPanels[optionIndex].repaint();
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!models[optionIndex].isSelected()) {
                        optionPanels[optionIndex].setBackground(BACKGROUND_COLOR);
                        optionPanels[optionIndex].repaint();
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    models[optionIndex].setSelected(true);
                    userAnswers[currentQuestionIndex] = optionIndex;
                    updateOptionStyles();
                }
            });
            
            // Ensure consistent state on initialization
            optionPanels[i].setOpaque(true);
            
            contentPanel.add(optionPanels[i]);
            contentPanel.add(Box.createVerticalStrut(10));
        }
        
        // Navigation buttons panel with refined design
        JPanel buttonPanel = new JPanel(new BorderLayout(20, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(25, 40, 30, 40));
        
        backButton = new JButton("Back");
        styleButton(backButton, false);
        
        nextButton = new JButton("Next");
        styleButton(nextButton, true);
        
        // Add button actions
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    displayQuestion();
                }
            }
        });
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion();
                } else {
                    // Show results
                    showResults();
                }
            }
        });
        
        // Create container panels for buttons with proper spacing
        JPanel backButtonContainer = new JPanel();
        backButtonContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backButtonContainer.setBackground(BACKGROUND_COLOR);
        backButtonContainer.add(backButton);
        
        JPanel nextButtonContainer = new JPanel();
        nextButtonContainer.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        nextButtonContainer.setBackground(BACKGROUND_COLOR);
        nextButtonContainer.add(nextButton);
        
        buttonPanel.add(backButtonContainer, BorderLayout.WEST);
        buttonPanel.add(nextButtonContainer, BorderLayout.EAST);
        
        // Add panels to main layout with proper spacing
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Display first question
        displayQuestion();
    }
    
    /**
     * Apply consistent button styling
     */
    private void styleButton(JButton button, boolean isPrimary) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Only set the cursor - all other styling will be handled by RoundedButtonUI
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Initial colors will be set by the UI
        if (isPrimary) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(70, 70, 70));
        }
        
        // Add rounded corners and hover effect with proper radius and primary/secondary flag
        button.setUI(new RoundedButtonUI(30, isPrimary));
        
        // Set preferred size for consistent button dimensions
        button.setPreferredSize(new Dimension(120, 44));
    }
    
    /**
     * Update the visual styles of option panels based on selection state
     */
    private void updateOptionStyles() {
        for (int i = 0; i < optionPanels.length; i++) {
            if (userAnswers[currentQuestionIndex] == i) {
                optionPanels[i].setBackground(OPTION_SELECTED);
                optionPanels[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                    BorderFactory.createEmptyBorder(11, 14, 11, 14)
                ));
            } else {
                optionPanels[i].setBackground(BACKGROUND_COLOR);
                optionPanels[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(OPTION_BORDER, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            // Force repaint to ensure visual updates are applied
            optionPanels[i].repaint();
            optionPanels[i].revalidate();
        }
    }
    
    private void displayQuestion() {
        QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
        
        // Update question text with HTML for proper wrapping and center alignment
        questionLabel.setText("<html><div style='width:100%; text-align:center; margin:0; padding:0'>"
                + (currentQuestionIndex + 1) + ". " + currentQuestion.getQuestion()
                + "</div></html>");
        
        // Update options
        List<String> options = currentQuestion.getOptions();
        for (int i = 0; i < 4; i++) {
            optionLabels[i].setText(options.get(i));
        }
        
        // Update progress indicators
        progressBar.setValue(currentQuestionIndex + 1);
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
        
        // Update button text and visibility
        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }
        
        // Hide back button on first question
        backButton.setVisible(currentQuestionIndex > 0);
        
        // Restore selection state
        updateOptionStyles();
    }
    
    private void showResults() {
        // Calculate score
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] == questions.get(i).getCorrectOption()) {
                correctAnswers++;
            }
        }
        
        // Show results screen
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new ResultScreen(parentFrame, correctAnswers, questions.size()));
        parentFrame.validate();
        parentFrame.repaint();
    }
}
