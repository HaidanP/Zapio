package com.zapio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import java.util.List;

/**
 * Screen to display flashcards with a modern, elegant UI
 * Features a minimalist black and white design with refined interactions
 */
public class FlashcardScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private final List<Flashcard> flashcards;
    private int currentCardIndex = 0;
    private boolean showingAnswer = false;
    
    // Main content components
    private final JPanel cardPanel;
    private final JLabel contentLabel;
    private final JPanel leftNavPanel;
    private final JPanel rightNavPanel;
    private final JLabel progressLabel;
    private final JProgressBar progressBar;
    
    // Colors for the modern UI - Black and White palette
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_TEXT = Color.BLACK;
    private static final Color SECONDARY_TEXT = new Color(70, 70, 70);
    
    // Card shadow properties
    private static final int SHADOW_SIZE = 12;
    private static final float SHADOW_OPACITY = 0.15f;
    private static final int CARD_CORNER_RADIUS = 16;
    
    /**
     * Constructor for the flashcard screen
     */
    public FlashcardScreen(JFrame parentFrame, List<Flashcard> flashcards) {
        this.flashcards = flashcards;
        
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);
        
        // Create header with elegant spacing
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 40, 15, 40));
        
        JLabel titleLabel = new JLabel("Flash Cards");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Tap card to flip");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress indicator
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setBackground(BACKGROUND_COLOR);
        progressPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        
        progressBar = new JProgressBar(0, flashcards.size());
        progressBar.setValue(1); // Start with card 1
        
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
        
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 6)); // Thin, elegant progress bar
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(false);
        
        progressLabel = new JLabel("1 / " + flashcards.size());
        progressLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        progressLabel.setForeground(SECONDARY_TEXT);
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.EAST);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(progressPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel with the flashcard and navigation arrows
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 25, 0, 25));
        
        // Left navigation arrow panel
        leftNavPanel = createNavigationArrowPanel(false);
        leftNavPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPreviousCard();
            }
            

        });
        
        // Right navigation arrow panel
        rightNavPanel = createNavigationArrowPanel(true);
        rightNavPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showNextCard();
            }
            

        });
        
        // Create card panel with shadow effect - smaller size
        cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint shadow first (slightly larger than the card)
                g2.setColor(new Color(0, 0, 0, (int)(255 * SHADOW_OPACITY)));
                for (int i = 0; i < SHADOW_SIZE; i++) {
                    float alpha = SHADOW_OPACITY * (1 - (float)i / SHADOW_SIZE);
                    g2.setColor(new Color(0, 0, 0, (int)(255 * alpha)));
                    g2.fill(new RoundRectangle2D.Float(
                        SHADOW_SIZE - i, 
                        SHADOW_SIZE - i, 
                        getWidth() - 2 * (SHADOW_SIZE - i), 
                        getHeight() - 2 * (SHADOW_SIZE - i), 
                        CARD_CORNER_RADIUS, 
                        CARD_CORNER_RADIUS
                    ));
                }
                
                // Paint card background
                g2.setColor(CARD_BACKGROUND);
                g2.fill(new RoundRectangle2D.Float(
                    SHADOW_SIZE, 
                    SHADOW_SIZE, 
                    getWidth() - 2 * SHADOW_SIZE, 
                    getHeight() - 2 * SHADOW_SIZE, 
                    CARD_CORNER_RADIUS, 
                    CARD_CORNER_RADIUS
                ));
                
                // Paint subtle border
                g2.setColor(new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(
                    SHADOW_SIZE, 
                    SHADOW_SIZE, 
                    getWidth() - 2 * SHADOW_SIZE, 
                    getHeight() - 2 * SHADOW_SIZE, 
                    CARD_CORNER_RADIUS, 
                    CARD_CORNER_RADIUS
                ));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(SHADOW_SIZE + 20, SHADOW_SIZE + 20, SHADOW_SIZE + 20, SHADOW_SIZE + 20));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.setPreferredSize(new Dimension(0, 280)); // Control the height of the card
        
        // Content label for question/answer
        contentLabel = new JLabel();
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        contentLabel.setForeground(PRIMARY_TEXT);
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Make the content wrap properly
        contentLabel.putClientProperty("html.disable", Boolean.FALSE);
        
        cardPanel.add(contentLabel, BorderLayout.CENTER);
        
        // Add click listener to flip the card
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flipCard();
            }
        });
        
        // Add navigation arrows and card to content panel
        contentPanel.add(leftNavPanel, BorderLayout.WEST);
        contentPanel.add(cardPanel, BorderLayout.CENTER);
        contentPanel.add(rightNavPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
        
        // Add some bottom padding
        JPanel bottomPadding = new JPanel();
        bottomPadding.setBackground(BACKGROUND_COLOR);
        bottomPadding.setPreferredSize(new Dimension(0, 20));
        add(bottomPadding, BorderLayout.SOUTH);
        
        // Display first card
        updateCardDisplay();
    }
    
    /**
     * Creates a navigation arrow panel (left or right)
     */
    private JPanel createNavigationArrowPanel(boolean isRight) {
        JPanel arrowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw arrow
                int arrowSize = 12;
                int centerY = height / 2;
                int startX = isRight ? width / 2 - arrowSize : width / 2 + arrowSize;
                
                g2.setColor(isEnabled() ? PRIMARY_TEXT : SECONDARY_TEXT);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                if (isRight) {
                    // Right arrow
                    g2.drawLine(startX, centerY - arrowSize, startX + arrowSize, centerY);
                    g2.drawLine(startX, centerY + arrowSize, startX + arrowSize, centerY);
                } else {
                    // Left arrow
                    g2.drawLine(startX, centerY - arrowSize, startX - arrowSize, centerY);
                    g2.drawLine(startX, centerY + arrowSize, startX - arrowSize, centerY);
                }
                
                g2.dispose();
            }
            
            @Override
            public boolean isEnabled() {
                if (isRight) {
                    return currentCardIndex < flashcards.size() - 1;
                } else {
                    return currentCardIndex > 0;
                }
            }
        };
        
        arrowPanel.setPreferredSize(new Dimension(40, 40));
        arrowPanel.setBackground(BACKGROUND_COLOR);
        arrowPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        arrowPanel.setOpaque(true);
        
        return arrowPanel;
    }
    
    /**
     * Updates the display with the current card content
     */
    private void updateCardDisplay() {
        Flashcard currentCard = flashcards.get(currentCardIndex);
        String content = showingAnswer ? currentCard.getAnswer() : currentCard.getQuestion();
        
        // Update label with HTML formatting for proper wrapping
        contentLabel.setText("<html><div style='text-align: center;'>" + content + "</div></html>");
        
        // Update progress
        progressBar.setValue(currentCardIndex + 1);
        progressLabel.setText((currentCardIndex + 1) + " / " + flashcards.size());
        
        // Force repaint of navigation arrows
        leftNavPanel.repaint();
        rightNavPanel.repaint();
        
        // If we're on the last card and showing the answer, add a return home option
        if (currentCardIndex == flashcards.size() - 1 && showingAnswer) {
            addReturnHomeOption();
        }
    }
    
    /**
     * Flips the current card between question and answer
     */
    private void flipCard() {
        // Simple flip animation effect
        Timer timer = new Timer(5, null);
        final int[] width = {cardPanel.getWidth()};
        final int targetWidth = cardPanel.getWidth();
        
        timer.addActionListener(e -> {
            width[0] -= 40;
            if (width[0] <= 0) {
                // When fully collapsed, change content and expand
                showingAnswer = !showingAnswer;
                updateCardDisplay();
                timer.removeActionListener(timer.getActionListeners()[0]);
                
                timer.addActionListener(e2 -> {
                    width[0] += 40;
                    if (width[0] >= targetWidth) {
                        timer.stop();
                        cardPanel.revalidate();
                    } else {
                        cardPanel.setPreferredSize(new Dimension(width[0], cardPanel.getHeight()));
                        cardPanel.revalidate();
                    }
                });
            } else {
                cardPanel.setPreferredSize(new Dimension(width[0], cardPanel.getHeight()));
                cardPanel.revalidate();
            }
        });
        
        timer.start();
    }
    
    /**
     * Shows the previous card
     */
    private void showPreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            showingAnswer = false;
            updateCardDisplay();
        }
    }
    
    /**
     * Shows the next card
     */
    private void showNextCard() {
        if (currentCardIndex < flashcards.size() - 1) {
            currentCardIndex++;
            showingAnswer = false;
            updateCardDisplay();
        }
    }
    

    /**
     * Adds a subtle return home option when user reaches the end of flashcards
     */
    private void addReturnHomeOption() {
        // Create a larger, more elegant return home button at the bottom of the screen
        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        homePanel.setBackground(BACKGROUND_COLOR);
        homePanel.setBorder(new EmptyBorder(10, 0, 25, 0));
        
        JButton homeButton = new JButton("Return to Home");
        homeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        homeButton.setForeground(Color.WHITE);
        homeButton.setBackground(Color.BLACK);
        homeButton.setFocusPainted(false);
        homeButton.setBorderPainted(false);
        homeButton.setContentAreaFilled(true);
        homeButton.setOpaque(true);
        homeButton.setUI(new RoundedButtonUI(12));
        homeButton.setPreferredSize(new Dimension(220, 50));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Set pointer cursor
        
        // Add hover effect - white background with black text
        homeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                homeButton.setBackground(Color.WHITE);
                homeButton.setForeground(Color.BLACK);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                homeButton.setBackground(Color.BLACK);
                homeButton.setForeground(Color.WHITE);
            }
        });
        
        homeButton.addActionListener(e -> {
            // Restart the application
            AppRestarter.restartApplication(this);
        });
        
        homePanel.add(homeButton);
        
        // Remove any existing components in the SOUTH position
        Component[] components = getComponents();
        for (Component c : components) {
            if (getLayout() instanceof BorderLayout && 
                ((BorderLayout)getLayout()).getConstraints(c) == BorderLayout.SOUTH) {
                remove(c);
            }
        }
        
        // Add the home panel
        add(homePanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}
