import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;


public class TerminalTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final String TEST_FILE = "testFile.txt";
    private static final File WORKING_DIRECTORY = new File(System.getProperty("user.home"));
    private File file;
    private Terminal terminal;

//    @TempDir
//    Path tempDir;
    private final String testDir = "testDir";
    private final String testFile = "testFile.txt";

//    Path filePath = Paths.get(String.valueOf(WORKING_DIRECTORY), TEST_FILE);



    @BeforeEach
    public void setUp() {
        // Redirect system output to capture it for testing
        System.setOut(new PrintStream(outputStream));
        terminal = new Terminal();
        // Set up the file path for testing
        file = new File(WORKING_DIRECTORY, TEST_FILE);
    }

    @AfterEach
    public void tearDown() {

        // Restore original system output after each test
        System.setOut(originalOut);
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

    @Test
    public void testWriteToFile() throws IOException {
        String[] content = {"Hello", "World"};

        // Call the method under test
        Terminal.writeToFile(content, TEST_FILE);

        // Check that the file was created
//        System.out.println(File.getAbsolutePath());
            assertTrue(file.exists(), "The file should be created.");

        // Verify the content of the file
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(2, lines.size(), "The file should contain 2 lines.");
        assertEquals("Hello", lines.get(0), "First line should match input.");
        assertEquals("World", lines.get(1), "Second line should match input.");
    }

    @Test
    public void testAppendToFile() throws IOException {
        String[] initialContent = {"Hello", "World"};
        String[] appendedContent = {"Foo", "Bar"};

        Terminal.writeToFile(initialContent, TEST_FILE);
        Terminal.appendToFile(appendedContent, TEST_FILE);
        assertTrue(file.exists(), "The file should be created.");

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(4, lines.size(), "The file should contain 4 lines.");
        assertEquals("Hello", lines.get(0), "First line should match initial input.");
        assertEquals("World", lines.get(1), "Second line should match initial input.");
        assertEquals("Foo", lines.get(2), "Third line should match appended input.");
        assertEquals("Bar", lines.get(3), "Fourth line should match appended input.");
    }


    @Test
    public void testPipePwdToWc() {
        // Test command: "pwd | wc"
        Terminal.pipeCommand("pwd | wc");

        String output = outputStream.toString().trim();
        assertTrue(output.startsWith("Word Count: "), "Output should start with 'Word Count:'");
    }


    @Test
    public void testPipeCatToWc() {
        // Test command: "cat <file> | wc"
        String filePath = "testFile.txt";
        Terminal.pipeCommand("cat " + filePath + " | wc");

        String output = outputStream.toString().trim();
        assertTrue(output.startsWith("Word Count: "), "Output should start with 'Word Count:'");
    }



    @Test
    public void testGrepUsageError() {
        // Test "grep" command without a pattern
        Terminal.pipeCommand("pwd | grep");

        String output = outputStream.toString().trim();
        assertEquals("Usage: grep <pattern>", output, "Output should indicate usage error for grep.");
    }

    @Test
    public void testWcUsage() {
        // Test "wc" command without arguments
        String input = "Hello\nWorld\nTest";
        Terminal.wc(input);

        String output = outputStream.toString().trim();
        assertTrue(output.startsWith("Word Count: "), "Output should start with 'Word Count:'");
    }

}
