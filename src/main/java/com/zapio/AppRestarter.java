package com.zapio;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class to restart the application
 */
public class AppRestarter {
    
    /**
     * Restarts the application by navigating back to the upload screen
     * 
     * @param component Any component in the application to find the main frame
     */
    public static void restartApplication(Component component) {
        try {
            // Find the top-level frame
            Window window = SwingUtilities.getWindowAncestor(component);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                
                // Create a new ZapioApp instance
                try {
                    // Dispose the current frame to release resources
                    frame.dispose();
                    
                    // Create and show a new ZapioApp instance
                    SwingUtilities.invokeLater(() -> {
                        ZapioApp app = new ZapioApp();
                        app.show();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // If creating a new ZapioApp fails, fall back to just showing the upload screen
                    frame.getContentPane().removeAll();
                    ZapioApp newApp = new ZapioApp();
                    UploadScreen uploadScreen = new UploadScreen(newApp);
                    frame.getContentPane().add(uploadScreen);
                    frame.validate();
                    frame.repaint();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Could not restart the application. Please close and reopen manually.", 
                "Restart Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
