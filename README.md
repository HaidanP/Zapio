# Zapio: AI-Powered Study Tool

Zapio is a Java-based desktop application that transforms your documents into interactive study materials using AI. Upload your documents and instantly generate flashcards, quizzes, and comprehensive cheatsheets to enhance your learning experience.

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [Running the Application](#running-the-application)
- [Usage Guide](#usage-guide)
- [Technical Implementation](#technical-implementation)
- [Troubleshooting](#troubleshooting)

## Features

### Document Processing
- Support for multiple document formats:
  - PDF (.pdf)
  - Microsoft Word (.docx)
  - Plain text (.txt)

### AI-Generated Study Materials
- **Flashcards**: Interactive question-answer pairs extracted from your document
- **Practice Quizzes**: Multiple-choice questions with automatic scoring
- **Comprehensive Cheatsheets**: Well-structured summaries with PDF export capability

## Project Structure

```
Zapio/
├── .env                        # Contains OpenRouter API key (pre-configured)
├── assets/
│   └── fonts/                  # Inter font family files
│       ├── Inter-Regular.otf
│       ├── Inter-Bold.otf
│       └── ...
├── pom.xml                     # Maven project configuration
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── zapio/      # Main package
│                   ├── ZapioApp.java              # Main application class
│                   ├── UploadScreen.java         # Document upload UI
│                   ├── SelectionScreen.java      # Study option selection UI
│                   ├── FlashcardScreen.java      # Flashcard display UI
│                   ├── QuizScreen.java           # Quiz interface UI
│                   ├── CheatsheetScreen.java     # Cheatsheet display UI
│                   ├── ResultScreen.java         # Quiz results UI
│                   ├── LoadingScreen.java        # Loading indicator UI
│                   ├── FlashcardGenerator.java   # Flashcard AI generation
│                   ├── QuizGenerator.java        # Quiz AI generation
│                   ├── CheatsheetGenerator.java  # Cheatsheet AI generation
│                   ├── Flashcard.java           # Flashcard data model
│                   ├── QuizQuestion.java        # Quiz question data model
│                   ├── DocumentPreviewPanel.java # Document preview component
│                   ├── RoundedButton.java       # Custom button component
│                   ├── RoundedButtonUI.java     # Button UI delegate
│                   ├── ShadowBorder.java        # Custom border component
│                   └── AppRestarter.java        # Application restart utility
└── target/
    └── zapio-1.0-SNAPSHOT-jar-with-dependencies.jar  # Executable JAR file
```

## Running the Application

### Prerequisites
- Java 11 or higher (JRE or JDK)
- OpenRouter API key (get one from https://openrouter.ai/)

### Environment Setup
1. Create a `.env` file in the root directory
2. Add your OpenRouter API key to the `.env` file:
   ```
   OPENROUTER_API_KEY=your_api_key_here
   ```
3. The application uses the `google/gemini-2.0-flash-exp:free` model through OpenRouter

### Option 1: Run the Pre-built JAR (Recommended)

1. Open a terminal/command prompt
2. Navigate to the Zapio directory
3. Run the application using:
   ```
   java -jar target/zapio-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

### Option 2: Build and Run from Source

If you want to modify the code or rebuild the application:

1. Ensure Maven is installed (version 3.6.0 or higher recommended)
   - Verify with: `mvn -version`

2. Open a terminal/command prompt

3. Navigate to the Zapio directory

4. Clean and compile the project:
   ```
   mvn clean compile
   ```

5. Package the application with dependencies:
   ```
   mvn package
   ```
   This creates an executable JAR in the `target` directory

6. Run the application:
   ```
   java -jar target/zapio-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

### Maven Build Details

The project uses the following Maven plugins:

- **maven-compiler-plugin**: Compiles Java source code (Java 11)
- **maven-assembly-plugin**: Creates a single JAR with all dependencies

Key dependencies include:

- **Apache PDFBox (2.0.30)**: PDF document processing
- **Apache POI (5.2.3)**: Microsoft Word document processing
- **Apache HttpClient (4.5.14)**: API communication
- **org.json (20231013)**: JSON parsing
- **dotenv-java (2.3.2)**: Environment variable management

## Usage Guide

### Document Upload

1. **Launch the application**
   - The Upload Screen appears first
   - A clean, minimalist interface with a central upload area

2. **Upload your document**
   - **Method 1**: Drag and drop a file onto the upload area
   - **Method 2**: Click the upload area to open a file browser
   - **Supported formats**: PDF (.pdf), Word (.docx), Text (.txt)
   - Maximum recommended file size: ~50 pages (larger files will be truncated)

3. **Processing**
   - The application will automatically extract text from your document
   - You'll be taken to the Selection Screen once processing completes

### Study Option Selection

1. **Document Preview**
   - Left side shows a preview of your uploaded document
   - Helps confirm you've uploaded the correct file

2. **Study Options**
   - Three card options are displayed:
     - **Flash Cards**: Question-answer pairs for active recall
     - **Practice Quiz**: Multiple-choice questions to test knowledge
     - **Full Cheatsheet**: Comprehensive summary of key concepts
   - Click on an option to select it (turns black when selected)

3. **Generate Content**
   - Click the "Proceed" button at the bottom
   - A loading screen appears while the AI generates your content
   - This typically takes 10-30 seconds depending on document size

### Using Flash Cards

1. **Card Navigation**
   - Cards are presented one at a time
   - Use the left/right arrows to navigate between cards
   - Card counter shows your current position (e.g., "Card 3 of 10")

2. **Card Interaction**
   - Initially shows the question side
   - **Click on the card** to flip it and reveal the answer
   - Click again to return to the question

3. **Return Home**
   - Click "Return to Home" button to go back to the upload screen

### Taking a Practice Quiz

1. **Question Format**
   - Each question has 4 multiple-choice options (A, B, C, D)
   - One option is the correct answer

2. **Answering Questions**
   - Click on an option to select your answer
   - Click "Submit" to check if your answer is correct
   - Immediate feedback: correct answers turn green, incorrect turn red
   - If incorrect, the correct answer is highlighted

3. **Quiz Progress**
   - Progress bar shows your position in the quiz
   - Click "Next" to proceed to the next question
   - After the final question, you'll see your score

4. **Results Screen**
   - Shows your final score as a percentage
   - Circular progress indicator displays score visually
   - Options to retake the quiz or return home

### Using the Cheatsheet

1. **Content Review**
   - Titled "One Sheet to Rule Them All" at the top
   - Scrollable content area with the AI-generated cheatsheet
   - Content is formatted with clear sections and structure
   - Smaller 12px font size to maximize content fitting on screen

2. **PDF Export**
   - Click the green "Export as PDF" button
   - File save dialog appears to choose save location
   - PDF is generated with proper formatting
   - Success confirmation appears when export is complete

3. **Return Home**
   - Click "Return to Home" button to go back to the upload screen

## Technical Implementation

### Architecture Overview

Zapio follows a modular architecture with three distinct layers:

1. **UI Layer (Presentation)**
   - Built with Java Swing for all interface components
   - Uses CardLayout for screen transitions
   - Custom UI components for modern aesthetics:
     - `RoundedButton`: Custom button with rounded corners
     - `ShadowBorder`: Subtle shadow effects for depth
     - `DocumentPreviewPanel`: Document thumbnail rendering

2. **Business Logic Layer**
   - AI integration through generator classes
   - Asynchronous processing using CompletableFuture
   - Document text extraction logic

3. **Data Model Layer**
   - Simple POJOs for data structures
   - `Flashcard`: Stores question-answer pairs
   - `QuizQuestion`: Contains question text, options, and correct answer

### AI Integration Details

- **API Provider**: OpenRouter (gateway to Google's Gemini models)
- **Model**: google/gemini-2.0-flash-exp:free
- **API Endpoint**: https://openrouter.ai/api/v1/chat/completions
- **Authentication**: Bearer token using API key from .env file

#### API Request Structure
```json
{
  "model": "google/gemini-2.0-flash-exp:free",
  "messages": [
    {
      "role": "user",
      "content": [{
        "type": "text",
        "text": "[Custom prompt with document text]"
      }]
    }
  ]
}
```

#### Custom Prompts by Content Type

1. **Flashcards**: Structured prompt requesting question-answer pairs in JSON format
2. **Quiz Questions**: Prompt for multiple-choice questions with exactly one correct answer
3. **Cheatsheets**: Request for plain text summary without markdown formatting

### Document Processing Implementation

- **PDF Processing**: Uses PDFBox with PDFTextStripper
- **Word Processing**: Uses Apache POI with XWPFWordExtractor
- **Text Processing**: Direct file reading with StandardCharsets.UTF_8
- **Large Document Handling**: Truncates to 15,000 characters if needed

### UI Implementation

- **Screen Transitions**: Managed by ZapioApp using CardLayout
- **Asynchronous Processing**: Loading screens during AI generation
- **Custom Animations**: Card flip effect in FlashcardScreen
- **Interactive Elements**: Hover effects, selection highlighting

### PDF Export Feature

- **Library**: Apache PDFBox
- **Implementation**: CheatsheetScreen.exportToPDF() method
- **Features**:
  - A4 page size with proper margins
  - Font styling for headings and content
  - Multi-page support for long documents
  - Custom title formatting

### Error Handling

- **API Errors**: Graceful degradation with user feedback
- **File Processing Errors**: Validation and error messages
- **Network Issues**: Timeout handling and retry options

## Troubleshooting

### Common Issues and Solutions

#### Java-Related Issues

1. **"Error: A JNI error has occurred"**
   - **Cause**: Incompatible Java version
   - **Solution**: Install Java 11 or higher
   - **Verification**: Run `java -version` to check your current version

2. **"Error: Could not find or load main class com.zapio.ZapioApp"**
   - **Cause**: Incorrect execution directory or corrupted JAR
   - **Solution**: Ensure you're in the correct directory and using the full JAR path
   - **Example**: `java -jar target/zapio-1.0-SNAPSHOT-jar-with-dependencies.jar`

#### Document Processing Issues

3. **"Unsupported file format"**
   - **Cause**: File extension not supported (.pdf, .docx, .txt only)
   - **Solution**: Convert your document to a supported format

4. **"Error extracting text from document"**
   - **Cause**: Corrupted file or password protection
   - **Solution**: Check if the file opens normally in other applications
   - **Alternative**: Try a different document

5. **"Document too large"**
   - **Cause**: Very large documents may be truncated
   - **Solution**: Use a smaller document or extract the most relevant pages

#### AI Generation Issues

6. **"Failed to get a valid response from the API"**
   - **Cause**: Network issues or API limits
   - **Solution**: Check internet connection and try again
   - **Note**: The API key is pre-configured and should work without modification

7. **"Error loading API key from .env file"**
   - **Cause**: Missing or corrupted .env file
   - **Solution**: Ensure the .env file is in the root directory with the correct API key

8. **Slow AI generation**
   - **Cause**: Large document or network latency
   - **Solution**: Be patient; generation typically takes 10-30 seconds
   - **Alternative**: Try a smaller document for faster results

#### UI Issues

9. **Fonts appear incorrect**
   - **Cause**: Missing font files
   - **Solution**: Ensure the assets/fonts directory contains all Inter font files

10. **Window sizing issues**
    - **Cause**: Display scaling or resolution problems
    - **Solution**: The application is designed for 900x700 resolution
    - **Note**: The window is not resizable by design

### Java Environment Tips

- **Recommended Java**: AdoptOpenJDK 11 or Oracle JDK 11+
- **Memory Settings**: Default JVM memory is sufficient
- **Platform Support**: Works on Windows, macOS, and Linux

### If All Else Fails

1. Delete the target directory
2. Rebuild the application with: `mvn clean package`
3. Run with: `java -jar target/zapio-1.0-SNAPSHOT-jar-with-dependencies.jar`

---

## Future Enhancements (v2.0)

- **Handwritten Index Card Generation**: Currently disabled in SelectionScreen.java (line 73), will be implemented in v2.0
