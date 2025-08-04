package com.zapio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Upload Screen for Zapio application
 * Allows users to upload PDF, DOCX, or TXT files
 */
public class UploadScreen extends JPanel {
    private ZapioApp app;
    private JLabel logoLabel;
    private JLabel zapioTitleLabel;
    private JLabel subtitleLabel;
    private JPanel uploadPanel;
    private JLabel uploadIconLabel;
    private JLabel uploadTextLabel;
    private JLabel uploadSubtextLabel;
    
    /**
     * Constructor
     * @param app Reference to the main application
     */
    public UploadScreen(ZapioApp app) {
        this.app = app;
        setupUI();
    }
    
    /**
     * Sets up the UI components for the upload screen
     */
    private void setupUI() {
        // Set panel properties
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create top panel for logo and titles
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        
        // Add logo
        ImageIcon logoIcon = new ImageIcon("/Users/hardikparajuli/Desktop/Valis/assets/images/zapio_logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(logoLabel);
        
        // Add Zapio title
        zapioTitleLabel = new JLabel("Zapio");
        try {
            Font dancingScript = Font.createFont(Font.TRUETYPE_FONT, 
                new File("/Users/hardikparajuli/Desktop/Valis/assets/fonts/Dancing Script OT.otf"));
            zapioTitleLabel.setFont(dancingScript.deriveFont(Font.PLAIN, 64));
        } catch (Exception e) {
            e.printStackTrace();
        }
        zapioTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        zapioTitleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        topPanel.add(zapioTitleLabel);
        
        // Add subtitle
        subtitleLabel = new JLabel("Flashcards, Quizzes & Index Cards—Powered by AI.");
        subtitleLabel.setFont(new Font("Inter Medium", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(71, 71, 71));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 50, 0));
        topPanel.add(subtitleLabel);
        
        // Add top panel to main panel
        add(topPanel, BorderLayout.NORTH);
        
        // Create center panel for upload area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        // Create upload panel
        uploadPanel = new JPanel();
        uploadPanel.setLayout(new BoxLayout(uploadPanel, BoxLayout.Y_AXIS));
        uploadPanel.setBackground(Color.WHITE);
        uploadPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add upload icon
        ImageIcon uploadIcon = new ImageIcon("/Users/hardikparajuli/Desktop/Valis/assets/images/uploadicon.png");
        Image scaledUploadIcon = uploadIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        uploadIconLabel = new JLabel(new ImageIcon(scaledUploadIcon));
        uploadIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadIconLabel.setBorder(new EmptyBorder(20, 0, 15, 0));
        uploadPanel.add(uploadIconLabel);
        
        // Add upload text
        uploadTextLabel = new JLabel("upload a file");
        uploadTextLabel.setFont(new Font("Inter Bold", Font.PLAIN, 24));
        uploadTextLabel.setForeground(new Color(51, 51, 51));
        uploadTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadPanel.add(uploadTextLabel);
        
        // Add upload subtext
        uploadSubtextLabel = new JLabel("(pdf, docx or txt)");
        uploadSubtextLabel.setFont(new Font("Inter Light", Font.PLAIN, 16));
        uploadSubtextLabel.setForeground(new Color(102, 102, 102));
        uploadSubtextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadSubtextLabel.setBorder(new EmptyBorder(5, 0, 20, 0));
        uploadPanel.add(uploadSubtextLabel);
        
        // Set preferred size for upload panel
        uploadPanel.setPreferredSize(new Dimension(380, 200));
        
        // Add upload panel to center panel
        centerPanel.add(uploadPanel);
        
        // Add center panel to main panel
        add(centerPanel, BorderLayout.CENTER);
        
        // Add click listener to upload panel
        uploadPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openFileChooser();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                // No hover effect
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // No hover effect
            }
        });
    }
    
    /**
     * Opens a file chooser dialog for selecting PDF, DOCX, or TXT files
     */
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to upload");
        
        // Set file filter to only allow PDF, DOCX, and TXT files
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Documents (*.pdf, *.docx, *.txt)", "pdf", "docx", "txt");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processUploadedFile(selectedFile);
        }
    }
    
    /**
     * Processes the uploaded file and navigates to the next screen
     * @param file The uploaded file
     */
    private void processUploadedFile(File file) {
        // Navigate to the selection screen
        app.showSelectionScreen(file);
    }
}
