package com.zapio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Screen to display the quiz results with a modern, elegant UI
 * Follows the design principles of elegant minimalism and functional design
 */
public class ResultScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Modern monochromatic color palette
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color CARD_BACKGROUND = new Color(252, 252, 252);
    private static final Color PRIMARY_TEXT = Color.BLACK;
    private static final Color SECONDARY_TEXT = new Color(70, 70, 70);
    private static final Color SUCCESS_COLOR = Color.BLACK;
    private static final Color WARNING_COLOR = new Color(60, 60, 60);
    private static final Color ERROR_COLOR = new Color(120, 120, 120);
    
    public ResultScreen(JFrame parentFrame, int score, int totalQuestions) {
        // Use absolute positioning for complete control
        setLayout(null);
        setBackground(BACKGROUND_COLOR);
        
        // We'll use component size directly for positioning in the layout
        
        // Create header with refined spacing for elegant minimalism
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(60, 40, 20, 40)); // More top padding for better balance
        
        // Title with refined typography
        JLabel titleLabel = new JLabel("You Scored");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(PRIMARY_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add subtle animation to title for micro-interaction
        titleLabel.putClientProperty("original-location-y", titleLabel.getLocation().y);
        new Timer(40, new ActionListener() {
            private int frame = 0;
            private final int totalFrames = 15;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frame < totalFrames) {
                    float alpha = (float) frame / totalFrames;
                    titleLabel.setForeground(new Color(0, 0, 0, (int)(alpha * 255)));
                    frame++;
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        }).start();
        
        // Create main content panel with comfortable spacing
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 60, 40, 60)); // Adjusted top margin
        
        // Score display with refined circular progress indicator - ensure proper centering
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        scorePanel.setBackground(BACKGROUND_COLOR);
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create circular progress component
        double percentage = (double) score / totalQuestions;
        CircularProgressPanel progressPanel = new CircularProgressPanel(percentage);
        progressPanel.setPreferredSize(new Dimension(250, 250)); // Larger to ensure visibility
        progressPanel.setMinimumSize(new Dimension(250, 250));
        progressPanel.setMaximumSize(new Dimension(250, 250));
        progressPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Score text with elegant typography - centered properly
        JPanel scoreTextPanel = new JPanel();
        scoreTextPanel.setLayout(new BoxLayout(scoreTextPanel, BoxLayout.Y_AXIS));
        scoreTextPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent
        
        // Center the score text vertically within the circle
        scoreTextPanel.add(Box.createVerticalGlue());
        
        JLabel scoreLabel = new JLabel(score + "/" + totalQuestions);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 42)); // Larger font for emphasis
        scoreLabel.setForeground(getScoreColor(percentage));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel percentLabel = new JLabel(Math.round(percentage * 100) + "%");
        percentLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        percentLabel.setForeground(SECONDARY_TEXT);
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        scoreTextPanel.add(scoreLabel);
        scoreTextPanel.add(Box.createVerticalStrut(4));
        scoreTextPanel.add(percentLabel);
        scoreTextPanel.add(Box.createVerticalGlue()); // Add space at the bottom to center vertically
        
        // Add score components to a layered pane for overlay effect
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(250, 250));
        
        progressPanel.setBounds(0, 0, 250, 250);
        layeredPane.add(progressPanel, JLayeredPane.DEFAULT_LAYER);
        
        scoreTextPanel.setBounds(0, 0, 250, 250);
        scoreTextPanel.setOpaque(false);
        // Use DRAG_LAYER to ensure text is always on top with highest z-index
        layeredPane.add(scoreTextPanel, JLayeredPane.DRAG_LAYER);
        
        // Container with subtle shadow for depth
        JPanel layeredPaneContainer = new JPanel();
        layeredPaneContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        layeredPaneContainer.setBackground(BACKGROUND_COLOR);
        layeredPaneContainer.add(layeredPane);
        layeredPaneContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        layeredPaneContainer.setMaximumSize(new Dimension(300, 280)); // Fixed height to prevent overflow
        
        // Message based on score with refined card-like appearance
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Custom painting for refined rounded corners
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBackground(CARD_BACKGROUND);
        messagePanel.setOpaque(false);  // Allow custom painting
        
        // Add refined shadow with smoother appearance
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(4, 0.08f),  // Lighter, more subtle shadow
            new EmptyBorder(25, 30, 25, 30)  // More generous padding
        ));
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.setMaximumSize(new Dimension(500, 130));
        
        String motivationalMessage = getMotivationalMessage(score, totalQuestions);
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; line-height: 150%;'>"
                + motivationalMessage + "</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        messageLabel.setForeground(PRIMARY_TEXT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Add animation to message panel for micro-interaction
        messagePanel.putClientProperty("original-location-y", messagePanel.getLocation().y);
        messagePanel.setVisible(false); // Start invisible for fade-in effect
        
        // Create a timer for animation
        Timer messageTimer = new Timer(40, new ActionListener() {
            private int frame = 0;
            private final int totalFrames = 15;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frame == 0) {
                    messagePanel.setVisible(true);
                }
                
                if (frame < totalFrames) {
                    float alpha = (float) frame / totalFrames;
                    messagePanel.setBackground(new Color(
                        CARD_BACKGROUND.getRed(),
                        CARD_BACKGROUND.getGreen(),
                        CARD_BACKGROUND.getBlue(),
                        (int)(alpha * 255)
                    ));
                    frame++;
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        messageTimer.setInitialDelay(300); // Delay to show after score
        messageTimer.start();
        
        // Return to home button with elegant styling matching QuizScreen
        JButton returnButton = new JButton("Return to Home");
        returnButton.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Slightly larger for better touch target
        returnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnButton.setUI(new RoundedButtonUI(30, true)); // Match QuizScreen buttons
        returnButton.setPreferredSize(new Dimension(200, 44));
        returnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnButton.setMaximumSize(new Dimension(200, 44));
        
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Restart the application
                AppRestarter.restartApplication(ResultScreen.this);
            }
        });
        
        // Position components using absolute coordinates for precise control
        
        // Get dimensions for positioning
        int screenWidth = parentFrame.getWidth();
        int screenHeight = parentFrame.getHeight();
        
        // Use these dimensions for responsive layout
        
        // Position title at the top and center it
        titleLabel.setBounds(0, 50, screenWidth, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);
        
        // Create layered pane to ensure proper z-ordering
        JLayeredPane mainLayeredPane = new JLayeredPane();
        mainLayeredPane.setBounds(0, 0, screenWidth, screenHeight);
        mainLayeredPane.setLayout(null);
        add(mainLayeredPane);
        
        // Position components in the layered pane with appropriate z-index
        int circlePanelSize = 280;
        int circlePanelX = (screenWidth - circlePanelSize) / 2;
        int circlePanelY = 130;
        
        // Add components to layered pane with specific z-indices
        layeredPaneContainer.setBounds(circlePanelX, circlePanelY, circlePanelSize, circlePanelSize);
        mainLayeredPane.add(layeredPaneContainer, JLayeredPane.PALETTE_LAYER); // Higher layer
        
        // Position message panel well below the circle
        int messagePanelWidth = 500;
        int messagePanelHeight = 120;
        int messagePanelX = (screenWidth - messagePanelWidth) / 2;
        int messagePanelY = circlePanelY + circlePanelSize + 60; // Plenty of space below circle
        messagePanel.setBounds(messagePanelX, messagePanelY, messagePanelWidth, messagePanelHeight);
        mainLayeredPane.add(messagePanel, JLayeredPane.DEFAULT_LAYER);
        
        // Position button below message panel
        final int buttonWidth = 200;
        final int buttonHeight = 44;
        final int buttonX = (screenWidth - buttonWidth) / 2;
        // Calculate the button Y position - moved up by reducing the gap and an extra 15px
        int tempButtonY = messagePanelY + messagePanelHeight + 25; // Reduced from 40 to 25
        
        // Ensure button is visible by checking against screen height
        final int buttonY;
        if (tempButtonY + buttonHeight > screenHeight - 20) {
            buttonY = screenHeight - buttonHeight - 20;
        } else {
            buttonY = tempButtonY;
        }
        
        returnButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        mainLayeredPane.add(returnButton, JLayeredPane.DEFAULT_LAYER);
        
        // Add component listener to handle resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update positions on resize
                titleLabel.setBounds(0, 50, getWidth(), 50);
                mainLayeredPane.setBounds(0, 0, getWidth(), getHeight());
                
                // Recalculate positions based on new width
                int newCirclePanelX = (getWidth() - circlePanelSize) / 2;
                layeredPaneContainer.setBounds(newCirclePanelX, circlePanelY, circlePanelSize, circlePanelSize);
                
                int newMessagePanelX = (getWidth() - messagePanelWidth) / 2;
                messagePanel.setBounds(newMessagePanelX, messagePanelY, messagePanelWidth, messagePanelHeight);
                
                // Button X position changes with width, Y remains the same
                int newButtonX = (getWidth() - buttonWidth) / 2;
                returnButton.setBounds(newButtonX, buttonY, buttonWidth, buttonHeight);
            }
        });
    }
    
    /**
     * Get appropriate color based on score percentage
     */
    private Color getScoreColor(double percentage) {
        if (percentage >= 0.7) {
            return SUCCESS_COLOR;
        } else if (percentage >= 0.4) {
            return WARNING_COLOR;
        } else {
            return ERROR_COLOR;
        }
    }
    
    private String getMotivationalMessage(int score, int total) {
        double percentage = (double) score / total;
        
        if (percentage >= 0.9) {
            return "Excellent! You've mastered this material and are ready to apply your knowledge!";
        } else if (percentage >= 0.7) {
            return "Great job! You have a solid understanding of the key concepts!";
        } else if (percentage >= 0.5) {
            return "Good effort! With a bit more study, you'll improve your understanding!";
        } else if (percentage >= 0.3) {
            return "You're making progress! Review the material again to strengthen your knowledge!";
        } else {
            return "Keep practicing! Everyone starts somewhere, and with dedication you'll improve!";
        }
    }
    
    /**
     * Custom panel to display an elegant circular progress indicator
     * with refined styling and subtle animation
     */
    private class CircularProgressPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final double percentage;
        private double animatedPercentage = 0.0;
        private Timer animationTimer;
        
        public CircularProgressPanel(double percentage) {
            this.percentage = percentage;
            setOpaque(false);
            
            // Create animation for progress arc
            animationTimer = new Timer(16, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (animatedPercentage < percentage) {
                        // Ease-out animation for smooth progress
                        animatedPercentage = Math.min(percentage, animatedPercentage + 0.02);
                        repaint();
                    } else {
                        animationTimer.stop();
                    }
                }
            });
            animationTimer.setInitialDelay(200);
            animationTimer.start();
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(250, 250);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 20; // Leave some margin to prevent cutoff
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            int strokeWidth = 12; // Thicker stroke for more elegant appearance
            
            // Draw background circle - lighter for more subtle contrast
            g2d.setColor(new Color(245, 245, 245));
            g2d.fill(new Ellipse2D.Double(x, y, size, size));
            
            // Draw thin ring for better definition
            g2d.setColor(new Color(235, 235, 235));
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(new Ellipse2D.Double(x + strokeWidth/2, y + strokeWidth/2, 
                    size - strokeWidth, size - strokeWidth));
            
            // Draw progress arc with rounded cap for modern look
            double angle = 360.0 * animatedPercentage;
            g2d.setColor(getScoreColor(percentage));
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Adjust for proper arc positioning (start at top, go clockwise)
            g2d.draw(new Arc2D.Double(
                x + strokeWidth/2, y + strokeWidth/2, 
                size - strokeWidth, size - strokeWidth,
                90, -angle, Arc2D.OPEN
            ));
            
            g2d.dispose();
        }
    }
}
