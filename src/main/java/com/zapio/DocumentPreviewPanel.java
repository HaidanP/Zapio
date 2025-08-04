package com.zapio;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * A panel that displays a preview of a document (PDF, DOCX, TXT).
 */
public class DocumentPreviewPanel extends JPanel {

    private JPanel pagesPanel; // Panel to hold individual page images for PDF
    private JScrollPane scrollPane;
    private Component currentView; // To keep track of what's currently in the scroll pane

    public DocumentPreviewPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY); // Background for the whole preview area

        // Panel specifically for PDF pages (uses BoxLayout)
        pagesPanel = new JPanel();
        pagesPanel.setLayout(new BoxLayout(pagesPanel, BoxLayout.Y_AXIS));
        pagesPanel.setBackground(Color.WHITE);

        // Initialize scrollPane without a specific view yet
        scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Default, overridden later
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
        currentView = null; // Initially empty
    }

    /**
     * Clears the current view from the scroll pane.
     */
    private void clearPreviewArea() {
        if (currentView != null) {
            // For text components, just remove them directly
            if (currentView instanceof JTextComponent) {
                 scrollPane.setViewportView(null);
            } 
            // For the pagesPanel (PDF), remove its contents
            else if (currentView == pagesPanel) {
                 pagesPanel.removeAll();
                 pagesPanel.revalidate();
                 pagesPanel.repaint();
                 // Ensure pagesPanel is not the view if we clear it this way
                 if (scrollPane.getViewport().getView() == pagesPanel) {
                    scrollPane.setViewportView(null); 
                 }
            }
             // Fallback for other potential component types
             else {
                 scrollPane.setViewportView(null);
             }
        }
        currentView = null;
        // Reset scrollbar policies
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Loads and displays a document file.
     * Supports PDF, DOCX, and TXT files.
     * Clears previous content before loading.
     * @param file The document file to load.
     */
    public void loadDocument(File file) {
        clearPreviewArea(); // Clear previous content and reset state

        if (file == null || !file.exists()) {
            displayError("File not found or is invalid.");
            return;
        }

        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            loadPDFPreview(file);
        } else if (fileName.endsWith(".docx")) {
            loadDOCXPreview(file);
        } else if (fileName.endsWith(".txt")) {
            loadTXTPreview(file);
        } else {
            displayError("Unsupported file type: " + fileName);
        }
    }

    /**
     * Creates rendering hints for PDF rendering
     * @return RenderingHints object with balanced settings for text clarity
     */
    private RenderingHints createRenderingHints() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return hints;
    }
    
    private void loadPDFPreview(File file) {
        // Ensure pagesPanel is the view for PDF
        if (currentView != pagesPanel) {
            scrollPane.setViewportView(pagesPanel);
            currentView = pagesPanel;
        }
        // Ensure horizontal scrollbar is OFF for PDF image previews
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Reset layout for pagesPanel if it was changed by text views
        pagesPanel.setLayout(new BoxLayout(pagesPanel, BoxLayout.Y_AXIS));
        pagesPanel.setBackground(Color.WHITE);
        
        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numPages = document.getNumberOfPages();
            
            // Very low DPI for better overall readability
            float dpi = 72f; // Lower DPI for clearer text with less processing

            // Estimate panel width for initial scaling
            int panelWidth = scrollPane.getViewport().getWidth() > 0 ? scrollPane.getViewport().getWidth() : 600;
            // Reduced adjustment to give slightly more width for the image
            panelWidth -= 15; // Adjust for scrollbar and padding

            // Clear pagesPanel specifically (already done in clearPreviewArea, but good practice)
            pagesPanel.removeAll(); 

            // Apply rendering hints to PDFBox renderer
            pdfRenderer.setRenderingHints(createRenderingHints());

            for (int i = 0; i < numPages; i++) {
                // Render the PDF page at higher DPI
                BufferedImage originalImage = pdfRenderer.renderImageWithDPI(i, dpi);

                // Calculate scaling to fit width while maintaining aspect ratio
                double scale = (double) panelWidth / originalImage.getWidth();
                int scaledWidth = panelWidth;
                int scaledHeight = (int) (originalImage.getHeight() * scale);

                // Create scaled image with better color model for text
                BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = scaledImage.createGraphics();
                
                // Set balanced rendering hints for the scaling operation
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Fill with a clean white background
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, scaledWidth, scaledHeight);
                
                // Draw the image with high-quality settings
                g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
                g.dispose();
                
                // Release memory of the large original image
                originalImage.flush();

                JLabel pageLabel = new JLabel(new ImageIcon(scaledImage));
                pageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                pagesPanel.add(pageLabel);
                pagesPanel.add(Box.createVerticalStrut(10)); // Spacing between pages
                
                // Important: Repaint incrementally to show progress for large PDFs
                if (i % 5 == 0) { // Update UI every 5 pages
                     pagesPanel.revalidate();
                     pagesPanel.repaint();
                }
            }
            // Final update
            pagesPanel.revalidate();
            pagesPanel.repaint();
            // Scroll to top after loading
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));

        } catch (IOException e) {
            System.err.println("Error loading PDF: " + e.getMessage());
            e.printStackTrace();
            displayError("Error loading PDF: " + e.getMessage());
        } catch (Exception e) {
             System.err.println("An unexpected error occurred during PDF rendering: " + e.getMessage());
             e.printStackTrace();
             displayError("Error rendering PDF page.");
        }
    }

    /**
     * Loads and displays a DOCX file
     * @param file The DOCX file to display
     */
    private void loadDOCXPreview(File file) {
        // Enable horizontal scrollbar for DOCX
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            // Create a styled text pane for rich text display
            JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            textPane.setBackground(Color.WHITE);
            // Add padding within the text component itself
            textPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
            textPane.setCaretPosition(0); // Ensure view starts at the top

            // Get the document content
            StyledDocument styledDoc = textPane.getStyledDocument();
            
            // Define styles
            Style defaultStyle = textPane.getStyle(StyleContext.DEFAULT_STYLE);
            StyleConstants.setFontFamily(defaultStyle, "Serif");
            StyleConstants.setFontSize(defaultStyle, 12);
            
            Style headingStyle = styledDoc.addStyle("Heading", defaultStyle);
            StyleConstants.setFontSize(headingStyle, 18);
            StyleConstants.setBold(headingStyle, true);
            
            Style boldStyle = styledDoc.addStyle("Bold", defaultStyle);
            StyleConstants.setBold(boldStyle, true);
            
            Style italicStyle = styledDoc.addStyle("Italic", defaultStyle);
            StyleConstants.setItalic(italicStyle, true);
            
            // Process paragraphs
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                // Determine if this might be a heading based on formatting
                boolean isHeading = false;
                // Check if paragraph is potentially a heading based on its runs
                for (XWPFRun run : paragraph.getRuns()) {
                    // Look for bold text or colored text as indicators of a heading
                    if (run.isBold() || run.getColor() != null) {
                        isHeading = true;
                        break;
                    }
                }
                
                // Apply appropriate style and add the paragraph text
                String text = paragraph.getText();
                if (!text.isEmpty()) {
                    if (isHeading) {
                        styledDoc.insertString(styledDoc.getLength(), text, headingStyle);
                    } else {
                        styledDoc.insertString(styledDoc.getLength(), text, defaultStyle);
                    }
                    styledDoc.insertString(styledDoc.getLength(), "\n\n", defaultStyle);
                }
            }
            
            // Set the text pane as the direct view for the scroll pane
            scrollPane.setViewportView(textPane);
            currentView = textPane; // Track the current view
            
            // Scroll to top
            SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));
            
        } catch (Exception e) {
            System.err.println("Error loading DOCX: " + e.getMessage());
            e.printStackTrace();
            displayError("Error loading DOCX file: " + e.getMessage());
        }
    }
    
    /**
     * Loads and displays a TXT file
     * @param file The TXT file to display
     */
    private void loadTXTPreview(File file) {
         // Enable horizontal scrollbar for TXT if needed (though line wrap is on)
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        try {
            // Read the text file content
            String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
            
            // Create a text area for plain text display
            JTextArea textArea = new JTextArea(content);
            textArea.setEditable(false);
            textArea.setLineWrap(true);       // Keep line wrapping
            textArea.setWrapStyleWord(true); // Keep word wrapping
            textArea.setBackground(Color.WHITE);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            // Add padding within the text component itself
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
            textArea.setCaretPosition(0); // Ensure view starts at the top

            // Set the text area as the direct view for the scroll pane
            scrollPane.setViewportView(textArea);
            currentView = textArea; // Track the current view

            // Scroll to top
            SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));
            
        } catch (IOException e) {
            System.err.println("Error loading TXT: " + e.getMessage());
            e.printStackTrace();
            displayError("Error loading TXT file: " + e.getMessage());
        }
    }
    
    private void displayError(String message) {
        clearPreviewArea(); // Use the centralized clear method
        // Display error message using a simple JLabel centered in the scroll pane
        JLabel errorLabel = new JLabel("<html><div style='text-align: center; padding: 20px;'>" + message + "</div></html>", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        errorLabel.setOpaque(true); // Make background visible
        errorLabel.setBackground(Color.WHITE); // Ensure consistent background

        // Center the label within the scroll pane viewport
        JPanel errorPanel = new JPanel(new GridBagLayout());
        errorPanel.setBackground(Color.WHITE);
        errorPanel.add(errorLabel);
        scrollPane.setViewportView(errorPanel);
        currentView = errorPanel;
    }
}
