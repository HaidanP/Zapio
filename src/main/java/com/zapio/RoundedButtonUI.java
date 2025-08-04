package com.zapio;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom UI for buttons with rounded corners and smooth hover effects
 */
public class RoundedButtonUI extends BasicButtonUI {
    private final int cornerRadius;
    private boolean isPrimary;
    
    // Colors for hover states
    private static final Color PRIMARY_HOVER_BG = Color.WHITE;
    private static final Color PRIMARY_HOVER_FG = Color.BLACK;
    private static final Color PRIMARY_NORMAL_BG = Color.BLACK;
    private static final Color PRIMARY_NORMAL_FG = Color.WHITE;
    
    private static final Color SECONDARY_HOVER_BG = Color.BLACK;
    private static final Color SECONDARY_HOVER_FG = Color.WHITE;
    private static final Color SECONDARY_NORMAL_BG = Color.WHITE;
    private static final Color SECONDARY_NORMAL_FG = new Color(70, 70, 70);
    
    public RoundedButtonUI(int cornerRadius) {
        this(cornerRadius, true);
    }
    
    public RoundedButtonUI(int cornerRadius, boolean isPrimary) {
        this.cornerRadius = cornerRadius;
        this.isPrimary = isPrimary;
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        
        // Important: Remove any existing borders and set properties
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        
        // Apply initial colors based on button type
        if (isPrimary) {
            button.setBackground(PRIMARY_NORMAL_BG);
            button.setForeground(PRIMARY_NORMAL_FG);
        } else {
            button.setBackground(SECONDARY_NORMAL_BG);
            button.setForeground(SECONDARY_NORMAL_FG);
        }
        
        // Add hover listener for smooth transitions
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(PRIMARY_HOVER_BG);
                    button.setForeground(PRIMARY_HOVER_FG);
                } else {
                    button.setBackground(SECONDARY_HOVER_BG);
                    button.setForeground(SECONDARY_HOVER_FG);
                }
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isPrimary) {
                    button.setBackground(PRIMARY_NORMAL_BG);
                    button.setForeground(PRIMARY_NORMAL_FG);
                } else {
                    button.setBackground(SECONDARY_NORMAL_BG);
                    button.setForeground(SECONDARY_NORMAL_FG);
                }
                button.repaint();
            }
        });
    }
    
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle for background
        g2d.setColor(button.getBackground());
        g2d.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), cornerRadius, cornerRadius));
        
        // Always draw border with appropriate color
        if (isPrimary) {
            // Primary button (Next): Black border only on hover
            if (button.getBackground() == PRIMARY_HOVER_BG) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.draw(new RoundRectangle2D.Double(0, 0, c.getWidth() - 1, c.getHeight() - 1, cornerRadius, cornerRadius));
            }
        } else {
            // Secondary button (Back): Always black border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(new RoundRectangle2D.Double(0, 0, c.getWidth() - 1, c.getHeight() - 1, cornerRadius, cornerRadius));
        }
        
        // Calculate text position for proper centering
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle textRect = new Rectangle(0, 0, c.getWidth(), c.getHeight());
        String text = button.getText();
        g2d.setColor(button.getForeground());
        int x = (textRect.width - fm.stringWidth(text)) / 2;
        int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
    }
    
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        // Don't use default pressed painting
    }
    
    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        // Text is painted in the paint method for better control
    }
    
    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        // Custom focus painting
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(b.getForeground().getRGB() & 0xFFFFFF | 0x44000000, true));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(2, 2, b.getWidth() - 5, b.getHeight() - 5, cornerRadius, cornerRadius));
        g2d.dispose();
    }
}
