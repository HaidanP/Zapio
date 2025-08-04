package com.zapio;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom border that adds a shadow effect to components
 */
public class ShadowBorder extends AbstractBorder {
    private static final long serialVersionUID = 1L;
    private final int shadowSize;
    private final float shadowOpacity;
    private final int cornerRadius = 12;
    
    public ShadowBorder(int shadowSize, float shadowOpacity) {
        this.shadowSize = shadowSize;
        this.shadowOpacity = shadowOpacity;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create shadow with gradient opacity
        
        // Draw shadow
        for (int i = 0; i < shadowSize; i++) {
            float opacity = shadowOpacity * (shadowSize - i) / shadowSize;
            g2d.setColor(new Color(0, 0, 0, (int)(255 * opacity)));
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(new RoundRectangle2D.Float(
                x + i, 
                y + i, 
                width - i * 2, 
                height - i * 2, 
                cornerRadius, 
                cornerRadius
            ));
        }
        
        g2d.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = shadowSize;
        return insets;
    }
}
