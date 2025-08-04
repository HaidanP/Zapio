package com.zapio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A JButton with rounded corners.
 */
public class RoundedButton extends JButton {
    private Color normalBackgroundColor;
    private Color normalTextColor;
    private Color hoverBackgroundColor;
    private Color hoverTextColor;
    private Color pressedBackgroundColor;
    private Color borderColor;
    private boolean isHovered = false;
    private int cornerRadius = 15; // Adjust for desired roundness
    private int borderThickness = 2; // Border thickness in pixels

    public RoundedButton(String text) {
        super(text);
        init();
    }

    public RoundedButton(String text, int cornerRadius) {
        super(text);
        this.cornerRadius = cornerRadius;
        init();
    }
    
    private void init() {
        // Make the button transparent to allow custom painting
        setContentAreaFilled(false);
        setFocusPainted(false); 
        setBorderPainted(false);
        setOpaque(false);
        
        // Default colors
        normalBackgroundColor = Color.BLACK;
        normalTextColor = Color.WHITE;
        hoverBackgroundColor = Color.WHITE;
        hoverTextColor = Color.BLACK;
        pressedBackgroundColor = new Color(220, 220, 220); // Light gray for pressed state
        borderColor = Color.BLACK;
        
        // Set initial colors
        setBackground(normalBackgroundColor);
        setForeground(normalTextColor);
        
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add mouse listeners for hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setForeground(hoverTextColor);
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setForeground(normalTextColor);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine background color based on state
        if (getModel().isPressed()) {
            g2.setColor(pressedBackgroundColor);
        } else if (isHovered) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(getBackground());
        }

        // Paint the rounded rectangle background
        g2.fill(new RoundRectangle2D.Float(borderThickness/2, borderThickness/2, 
                                          getWidth()-borderThickness, getHeight()-borderThickness, 
                                          cornerRadius, cornerRadius));
        
        // Draw border if hovered
        if (isHovered) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.draw(new RoundRectangle2D.Float(borderThickness/2, borderThickness/2, 
                                             getWidth()-borderThickness, getHeight()-borderThickness, 
                                             cornerRadius, cornerRadius));
        }
        
        g2.dispose();
        
        // Let the superclass paint the text
        super.paintComponent(g);
    }

    // Optional: Override paintBorder if you want a border
    // @Override
    // protected void paintBorder(Graphics g) {
    //     Graphics2D g2 = (Graphics2D) g.create();
    //     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //     g2.setColor(getForeground()); // Example: border color matches text
    //     g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
    //     g2.dispose();
    // }

    // Optional: Ensure the button's shape is respected for hit detection
    // @Override
    // public boolean contains(int x, int y) {
    //     if (shape == null || !shape.getBounds().equals(getBounds())) {
    //         shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
    //     }
    //     return shape.contains(x, y);
    // }
    // private Shape shape; // Keep track of the shape
}
