import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MiniShell {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                // Display prompt with current directory
                System.out.print(System.getProperty("user.dir") + " $ ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    break;  // Exit the shell
                }

                if (input.isEmpty()) {
                    continue;  // Ignore empty input
                }

                // Split the command by spaces
                List<String> command = Arrays.asList(input.split("\\s+"));

                // Handle redirection and piping if present
                if (command.contains(">") || command.contains(">>") || command.contains("|")) {
                    handleRedirectionAndPiping(command);
                } else {
                    executeCommand(command);
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // Execute general system commands
    private static void executeCommand(List<String> command) throws IOException, InterruptedException {
        String cmd = command.get(0);

        switch (cmd) {
            case "pwd":
                System.out.println(System.getProperty("user.dir"));
                break;
            case "cd":
                changeDirectory(command);
                break;
            case "mkdir":
                Files.createDirectories(Paths.get(command.get(1)));
                break;
            case "rmdir":
                Files.deleteIfExists(Paths.get(command.get(1)));
                break;
            case "rm":
                Files.deleteIfExists(Paths.get(command.get(1)));
                break;
            case "mv":
                Files.move(Paths.get(command.get(1)), Paths.get(command.get(2)), StandardCopyOption.REPLACE_EXISTING);
                break;
            case "cat":
                printFileContent(command.get(1));
                break;
            default:
                // Use ProcessBuilder for other commands
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.inheritIO();
                Process process = pb.start();
                process.waitFor();
        }
    }

    // Change the current working directory
    private static void changeDirectory(List<String> command) {
        if (command.size() < 2) {
            System.err.println("cd: expected argument");
            return;
        }

        String path = command.get(1);
        File newDir = new File(path);
        if (newDir.exists() && newDir.isDirectory()) {
            System.setProperty("user.dir", newDir.getAbsolutePath());
        } else {
            System.err.println("cd: no such file or directory: " + path);
        }
    }

    // Print the content of a file (for 'cat' command)
    private static void printFileContent(String filePath) throws IOException {
        Files.lines(Paths.get(filePath)).forEach(System.out::println);
    }

    // Handle redirection ('>', '>>') and piping ('|')
    private static void handleRedirectionAndPiping(List<String> command) throws IOException, InterruptedException {
        if (command.contains(">") || command.contains(">>")) {
            // Output Redirection
            String operator = command.contains(">") ? ">" : ">>";
            List<String> cmd = command.subList(0, command.indexOf(operator));
            String filename = command.get(command.indexOf(operator) + 1);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectOutput(operator.equals(">") ?
                    ProcessBuilder.Redirect.to(new File(filename)) :
                    ProcessBuilder.Redirect.appendTo(new File(filename)));
            Process process = pb.start();
            process.waitFor();

        } else if (command.contains("|")) {
            // Piping
            int pipeIndex = command.indexOf("|");
            List<String> firstCmd = command.subList(0, pipeIndex);
            List<String> secondCmd = command.subList(pipeIndex + 1, command.size());

            ProcessBuilder pb1 = new ProcessBuilder(firstCmd);
            ProcessBuilder pb2 = new ProcessBuilder(secondCmd);

            Process p1 = pb1.start();
            Process p2 = pb2.start();

            try (InputStream is = p1.getInputStream(); OutputStream os = p2.getOutputStream()) {
                is.transferTo(os);
            }

            p1.waitFor();
            p2.waitFor();
        }
    }
}
