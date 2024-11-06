import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;


public class TerminalTest {
    private static final String TEST_FILE = "testFile.txt";
    private static final File WORKING_DIRECTORY = new File(System.getProperty("user.dir"));
    private Terminal terminal;

//    @TempDir
//    Path tempDir;
    private final String testDir = "testDir";
    private final String testFile = "testFile.txt";


    @BeforeEach
    public void setUp() {
        terminal = new Terminal();
    }

    @AfterEach
    public void tearDown() {
        // Cleanup created files and directories
        File dir = new File(terminal.workingDirectory, testDir);
        if (dir.exists()) {
            dir.delete();
        }
        File file = new File(terminal.workingDirectory, testFile);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testPwd() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        terminal.pwd();

        assertTrue(out.toString().trim().contains(terminal.workingDirectory.getAbsolutePath()));
    }

    @Test
    public void testCdToParentDirectory() {
        String parentDir = terminal.workingDirectory.getParent();
        terminal.cd("..");
        assertEquals(parentDir, terminal.workingDirectory.getAbsolutePath());
    }

    @Test
    public void testMkdir() {
        terminal.mkdir(testDir);

        File dir = new File(terminal.workingDirectory, testDir);
        assertTrue(dir.exists() && dir.isDirectory(), "Directory should be created");
    }

    @Test
    public void testRmdir() {
        // Create directory first
        terminal.mkdir(testDir);
        terminal.rmdir(testDir);

        File dir = new File(terminal.workingDirectory, testDir);
        assertFalse(dir.exists(), "Directory should be deleted");
    }

    @Test
    public void testTouch() {
        terminal.touch(testFile);

        File file = new File(terminal.workingDirectory, testFile);
        assertTrue(file.exists() && file.isFile(), "File should be created");
    }

    @Test
    public void testRm() {
        // Create file first
        terminal.touch(testFile);
        terminal.rm(testFile,false);

        File file = new File(terminal.workingDirectory, testFile);
        assertFalse(file.exists(), "File should be deleted");
    }

    @Test
    public void testCatWithContent() throws IOException {
        // Create file and add content
        File file = new File(terminal.workingDirectory, testFile);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Hello, World!");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        terminal.cat(testFile);

        assertTrue(out.toString().trim().contains("Hello, World!"));
    }

    @Test
    public void testHelp() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        terminal.help();

        String helpOutput = out.toString().trim();
        assertTrue(helpOutput.contains("Available Commands"), "Help message should be displayed");
    }

    @Test
    public void testLs() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        terminal.ls();

        assertFalse(out.toString().trim().isEmpty(), "Directory listing should be displayed");
    }

    @Test
    public void testExecuteUnknownCommand() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        try {
            terminal.start("unknownCommand");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(out.toString().trim().contains("undefined command"), "Should print 'undefined command' for invalid command");
    }



    // -----------------------------------------
//
//    @Test
//    public void testWriteToFile() throws IOException {
//        String[] content = {"Hello", "World"};
//
//        Terminal.writeToFile(content, TEST_FILE);
//
//        File file = new File(WORKING_DIRECTORY, TEST_FILE);
//        System.out.println("Write Test - File path: " + file.getAbsolutePath()); // Debug line
//        assertTrue(file.exists(), "The file should be created.");
//
//        List<String> lines = Files.readAllLines(file.toPath());
//        assertEquals(2, lines.size(), "The file should contain 2 lines.");
//        assertEquals("Hello", lines.get(0), "First line should match input.");
//        assertEquals("World", lines.get(1), "Second line should match input.");
//
//        file.delete();
//    }
//
//    @Test
//    public void testAppendToFile() throws IOException {
//        String[] initialContent = {"Line 1", "Line 2"};
//        Terminal.writeToFile(initialContent, TEST_FILE);
//
//        String[] additionalContent = {"Line 3", "Line 4"};
//        Terminal.appendToFile(additionalContent, TEST_FILE);
//
//        File file = new File(WORKING_DIRECTORY, TEST_FILE);
//        System.out.println("Append Test - File path: " + file.getAbsolutePath()); // Debug line
//        assertTrue(file.exists(), "The file should exist.");
//        List<String> lines = Files.readAllLines(file.toPath());
//        System.out.println(lines); // Debug line
//        assertEquals(4, lines.size(), "The file should contain 4 lines after appending.");
//        assertEquals("Line 1", lines.get(0), "First line should match initial content.");
//        assertEquals("Line 2", lines.get(1), "Second line should match initial content.");
//        assertEquals("Line 3", lines.get(2), "Third line should match appended content.");
//        assertEquals("Line 4", lines.get(3), "Fourth line should match appended content.");
//
//        file.delete();
//    }

//    @Test
//    public void testPwdPipeGrep() {
//        // Test command: pwd | grep <pattern>
//        String pattern = System.getProperty("user.dir").substring(0, 3); // example pattern that matches part of pwd output
//        String command = "pwd | grep " + pattern;
//
//        Terminal.pipeCommand(command);
//
//        String output = outputStream.toString().trim();
//        assertTrue(output.contains(pattern), "The output should contain the specified pattern from pwd output.");
//    }
//
//    @Test
//    public void testLsPipeWc() {
//        // Test command: ls | wc
//        String command = "ls | wc";
//
//        Terminal.pipeCommand(command);
//
//        String output = outputStream.toString().trim();
//        assertTrue(output.matches("Word Count: \\d+"), "The output should show a word count.");
//    }
//
//    @Test
//    public void testCatPipeGrep() {
//        // Test command: cat <file> | grep <pattern>
//        String testFileName = "testFile.txt";
//        String testContent = "Hello World\nThis is a test file\nAnother line\n";
//        String pattern = "test";
//
//        // Create test file with content
//        Terminal.touch(testFileName);
//        Terminal.writeToFile(testFileName, testContent);  // assuming this helper function exists
//
//        String command = "cat " + testFileName + " | grep " + pattern;
//
//        Terminal.pipeCommand(command);
//
//        String output = outputStream.toString().trim();
//        assertTrue(output.contains("This is a test file"), "The output should contain lines matching the grep pattern.");
//
//        // Clean up
//        Terminal.rm(testFileName, false);
//    }
//
//    @Test
//    public void testCatPipeWc() {
//        // Test command: cat <file> | wc
//        String testFileName = "testFile.txt";
//        String testContent = "Hello World\nThis is a test file\nAnother line\n";
//
//        // Create test file with content
//        Terminal.touch(testFileName);
//        Terminal.writeToFile(testFileName, testContent);  // assuming this helper function exists
//
//        String command = "cat " + testFileName + " | wc";
//
//        Terminal.pipeCommand(command);
//
//        String output = outputStream.toString().trim();
//        assertTrue(output.matches("Word Count: \\d+"), "The output should show a word count.");
//
//        // Clean up
//        Terminal.rm(testFileName, false);
//    }
//
//    @Test
//    public void testUnsupportedPipeCommand() {
//        // Test command: pwd | unsupportedCommand
//        String command = "pwd | unsupportedCommand";
//
//        Terminal.pipeCommand(command);
//
//        String output = outputStream.toString().trim();
//        assertTrue(output.contains("Unsupported command for piped input"), "The output should indicate an unsupported command.");
//    }
}
