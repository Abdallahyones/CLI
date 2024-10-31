import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalTest {
    private Terminal terminal;
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
}
