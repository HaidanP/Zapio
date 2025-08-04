package com.zapio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

/**
 * A loading screen component that displays a spinning animation
 * while background processes are running.
 */
public class LoadingScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private final int DIAMETER = 80; // Larger diameter for more impressive animation
    private final Timer timer;
    private int angle = 0;
    private final Color[] gradientColors = {
        new Color(0, 0, 0),
        new Color(20, 20, 20),
        new Color(40, 40, 40),
        new Color(60, 60, 60),
        new Color(80, 80, 80)
    };
    
    public LoadingScreen(String message) {
        setLayout(new GridBagLayout()); // Use GridBagLayout for better centering
        setBackground(Color.WHITE);
        
        // Create title label with "Generating" text using a sans-serif font similar to Inter
        JLabel titleLabel = new JLabel("Generating");
        // Try to use a font similar to Inter (SansSerif is available on most systems)
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLACK);
        
        // Create a panel for the spinner with a beautiful animation
        JPanel spinnerPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Clear background
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw outer circle (static)
                g2d.setColor(new Color(240, 240, 240));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(centerX - DIAMETER/2, centerY - DIAMETER/2, DIAMETER, DIAMETER);
                
                // Draw spinning arcs with gradient effect
                int arcCount = 5;
                int arcLength = 60; // Degrees
                float strokeWidth = 3.0f;
                
                for (int i = 0; i < arcCount; i++) {
                    // Calculate position based on current angle
                    int arcAngle = (angle + (i * (360 / arcCount))) % 360;
                    
                    // Create a path for the arc
                    Path2D path = new Path2D.Float();
                    path.moveTo(centerX, centerY);
                    
                    // Create arc path
                    double startRad = Math.toRadians(arcAngle);
                    
                    // Outer arc
                    float radius = DIAMETER / 2.0f - (strokeWidth / 2.0f);
                    path.lineTo(
                        centerX + radius * Math.cos(startRad),
                        centerY + radius * Math.sin(startRad)
                    );
                    
                    // Draw the arc path
                    g2d.setColor(gradientColors[i % gradientColors.length]);
                    g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    // Draw arc
                    g2d.drawArc(
                        (int)(centerX - radius), 
                        (int)(centerY - radius), 
                        (int)(radius * 2), 
                        (int)(radius * 2), 
                        arcAngle, 
                        arcLength
                    );
                    
                    // Draw smaller dots at the end of each arc for a polished look
                    float dotRadius = strokeWidth * 0.8f;
                    float dotX = (float)(centerX + radius * Math.cos(Math.toRadians(arcAngle + arcLength)));
                    float dotY = (float)(centerY + radius * Math.sin(Math.toRadians(arcAngle + arcLength)));
                    g2d.fillOval((int)(dotX - dotRadius), (int)(dotY - dotRadius), 
                                (int)(dotRadius * 2), (int)(dotRadius * 2));
                }
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(DIAMETER + 20, DIAMETER + 20);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
            
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        spinnerPanel.setBackground(Color.WHITE);
        spinnerPanel.setOpaque(true);
        
        // Create a panel that stacks the spinner above the text with minimal spacing
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Add spinner first (on top)
        spinnerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(spinnerPanel);
        contentPanel.add(Box.createVerticalStrut(5)); // Minimal space between spinner and text
        
        // Add text label below
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        // Add the content panel to the main panel using GridBagLayout for perfect centering
        add(contentPanel);
        
        // Setup timer for animation
        timer = new Timer(40, new ActionListener() { // Faster refresh rate for smoother animation
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update spinner angle
                angle = (angle + 5) % 360;
                spinnerPanel.repaint();
            }
        });
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }
}
