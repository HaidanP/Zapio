package com.zapio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// PDFBox imports for PDF export
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * The CheatsheetScreen displays a comprehensive cheatsheet generated from the document
 * with a modern, minimal UI design.
 */
public class CheatsheetScreen extends JPanel {
    private JTextPane cheatsheetTextPane;
    private JScrollPane scrollPane;
    
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33); // Dark gray for better readability
    private static final Color ACCENT_COLOR = new Color(0, 0, 0); // Black for accents
    private static final int PADDING = 40;
    
    /**
     * Constructor
     * @param frame The parent frame
     * @param documentFile The document file the cheatsheet was generated from
     * @param cheatsheetContent The pre-generated cheatsheet content
     */
    public CheatsheetScreen(JFrame frame, File documentFile, String cheatsheetContent) {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        setupUI(frame);
        displayCheatsheet(cheatsheetContent);
    }
    
    /**
     * Sets up the UI components with a clean, minimal design
     */
    private void setupUI(JFrame frame) {
        // Create header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Create centered title with new text
        JLabel titleLabel = new JLabel("One Sheet to Rule Them All");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the title text
        
        // Create a panel to center the title label
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create content area with text pane
        cheatsheetTextPane = new JTextPane();
        cheatsheetTextPane.setEditable(false);
        cheatsheetTextPane.setBackground(BACKGROUND_COLOR);
        cheatsheetTextPane.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Reduced font size for better fit
        cheatsheetTextPane.setForeground(TEXT_COLOR);
        cheatsheetTextPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Center align text (left-aligned paragraphs look better for content)
        StyledDocument doc = cheatsheetTextPane.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), left, false);
        
        // Create scroll pane for content
        scrollPane = new JScrollPane(cheatsheetTextPane);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Add shadow border to the content area for subtle depth
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true)));
        contentWrapper.add(scrollPane, BorderLayout.CENTER);
        
        add(contentWrapper, BorderLayout.CENTER);
        
        // Create a bottom panel for the home button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Export PDF button
        RoundedButton exportButton = new RoundedButton("Export as PDF", 25);
        exportButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        exportButton.setPreferredSize(new Dimension(200, 50));
        exportButton.setBackground(new Color(76, 175, 80)); // Green color
        exportButton.setForeground(Color.WHITE);
        
        // Modern, rounded home button
        RoundedButton homeButton = new RoundedButton("Return to Home", 25);
        homeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        homeButton.setPreferredSize(new Dimension(200, 50));
        homeButton.setBackground(ACCENT_COLOR);
        homeButton.setForeground(Color.WHITE);
        
        // Add action listener for export button
        exportButton.addActionListener(e -> exportToPDF(frame, cheatsheetTextPane.getText()));
        
        // Add action listener for home button to restart the application
        homeButton.addActionListener(e -> AppRestarter.restartApplication(CheatsheetScreen.this));
        
        bottomPanel.add(exportButton);
        bottomPanel.add(Box.createHorizontalStrut(20)); // Add spacing between buttons
        bottomPanel.add(homeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Displays the pre-generated cheatsheet content
     * @param content The cheatsheet content to display
     */
    private void displayCheatsheet(String content) {
        // Format and display the content
        cheatsheetTextPane.setText(formatCheatsheet(content));
        cheatsheetTextPane.setCaretPosition(0); // Scroll to top
    }
    
    /**
     * Format the cheatsheet text with some basic styling
     * @param rawText The raw text from Gemini API
     * @return Formatted text
     */
    private String formatCheatsheet(String rawText) {
        // Remove markdown formatting characters
        String formatted = rawText;
        
        // Remove markdown code block markers
        formatted = formatted.replace("```", "");
        formatted = formatted.replace("`", "");
        
        // Process any other markdown formatting if needed
        // but keep the actual content and structure
        
        return formatted;
    }
    
    /**
     * Export the cheatsheet content to a PDF file
     * 
     * @param frame The parent frame
     * @param content The cheatsheet content
     */
    private void exportToPDF(JFrame frame, String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));
        
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            // Add .pdf extension if not present
            if (!outputFile.getName().toLowerCase().endsWith(".pdf")) {
                outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
            }
            
            try {
                // Create PDF document
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                
                // Create content stream
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                
                // Set font and font size
                contentStream.setFont(PDType1Font.HELVETICA, 10); // Smaller font for PDF
                
                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("One Sheet to Rule Them All");
                contentStream.endText();
                
                // Format content for PDF (wrap text within page width)
                float margin = 50;
                float startY = 720;
                // Width is used to determine how much text can fit on a line
                // We're keeping this simple by using one line per input line
                // but in a more advanced implementation, we'd use width for text wrapping
                float fontSize = 10;
                float leading = 1.5f * fontSize;
                
                // Split the content into lines
                String[] lines = content.split("\n");
                
                contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                float currentY = startY;
                
                for (String line : lines) {
                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        currentY -= leading;
                        continue;
                    }
                    
                    // Check if line starts with a header marker (##, ###)
                    if (line.trim().startsWith("#")) {
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                        contentStream.newLineAtOffset(margin, currentY);
                        contentStream.showText(line.trim());
                        contentStream.endText();
                    } else {
                        // Regular text
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                        contentStream.newLineAtOffset(margin, currentY);
                        contentStream.showText(line.trim());
                        contentStream.endText();
                    }
                    
                    currentY -= leading;
                    
                    // Check if we need a new page
                    if (currentY <= margin) {
                        contentStream.close();
                        
                        // Add a new page
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        currentY = startY;
                    }
                }
                
                contentStream.close();
                document.save(outputFile);
                document.close();
                
                JOptionPane.showMessageDialog(frame, 
                    "Cheatsheet exported successfully to: " + outputFile.getAbsolutePath(), 
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, 
                    "Error exporting PDF: " + e.getMessage(), 
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
