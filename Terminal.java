import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Terminal {
    public static String initialDirectory = System.getProperty("user.dir");
    static public File workingDirectory;
    static Parser myParser;
    private static boolean CLI_open = true;
    public Terminal(){
        workingDirectory = new File(System.getProperty("user.home"));
        myParser= new Parser();
    }
    public boolean start(String input) throws IOException, InterruptedException {
        myParser.initializeCommand(input);

        if (input.contains(">") || input.contains(">>") || input.contains("|")) {
            handleRedirectionAndPiping(myParser.command); // not done
        } else if (myParser.isVaildcommand()){
            execute();
        }else{
            System.out.println("undefined command");
        }
        return CLI_open;
    }
    public void execute() {
        switch (myParser.getCommand()){
            case "pwd":
                pwd(); // done
                break;
            case "cd": // don't work with path
                if(myParser.isValidPath())
                    cd(myParser.getPath());
                else
                    System.out.println("invalid path");
                break;
            case "ls":  // done
                if(myParser.haveArgs())
                    ls(myParser.getarg());
                else
                    ls();
                break;
            case "mkdir":
                mkdir(myParser.getPath());  // done
                break;
            case "rmdir":
                rmdir(myParser.getPath());  //done
                break;
            case "touch":
                touch(myParser.getPath());  // done
                break;
            case "rm":
                rm(myParser.getPath()); // done
                break;
            case "cat": // done
                if(myParser.haveArgs())
                    cat(myParser.getPath());
                else
                    cat();
                break;
            case "help":
                help(); // done
                break;
            case "exit":
                exit(); // done
                break;
            case "mv":
                mv();  // done
                break;
            default:
                pwd();
        }
    }

    public void pwd(){
        System.out.println(workingDirectory.getAbsolutePath());
    }

    
    public void cd(String filePath){
        if(filePath.equals("..")){
            File newFile = new File(workingDirectory.getParent());
            workingDirectory=newFile.getAbsoluteFile();
        }
        else{
            File newFile= new File(workingDirectory , filePath);
            if(!newFile.exists() || !newFile.isDirectory()){
                System.out.println("Path does not exist");
            }
            else{
                workingDirectory=newFile;
            }
        }
    }

    public void ls(){
        String[] files = workingDirectory.list();
        for (int i = 0 ; i < files.length ; i++) {
            if(files[i].charAt(0)=='.') continue;
            System.out.println(files[i]);
        }
    }
    public void ls(String arg){
        if(arg.equals("-r")){
            String[] files = workingDirectory.list();
            for (int i = files.length-1; i >= 0 ; i--) {
                if(files[i].charAt(0)=='.') continue;
                System.out.println(files[i]);
            }
        }
        else if(arg.equals("-a")){
            String[] files = workingDirectory.list();
            for (int i = 0; i < files.length ; i++) {
                if(files[i].charAt(0)=='.') continue;
                System.out.println(files[i]);
            }
        }
        else{
            System.out.println("invalid option");
        }
    }
    public void mkdir(String name) {
        File newDir = new File(workingDirectory, name);

        if (newDir.exists()) {
            System.out.println("Directory already exists: " + newDir.getAbsolutePath());
        } else {
            boolean created = newDir.mkdir();
            if (!created) {
                System.out.println("Failed to create directory: " + newDir.getAbsolutePath());
            }
        }
    }

    public void rmdir(String name){
        File dir = new File(workingDirectory, name);
        File[] files = dir.listFiles();
        if(!dir.exists())
            System.out.println("Directory not exists: " + dir.getAbsolutePath());
        else if(dir.isDirectory() && dir.listFiles().length > 0)
            System.out.println("Directory not empty: " + dir.getAbsolutePath());
        else
            dir.delete();
    }

    public void touch(String name){
        if (name.isEmpty()) {
            System.out.println("An error occurred.");
            return;
        }
        File file = new File(workingDirectory,name);

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
            } else {
                System.out.println("File already exists.");
            }
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public void rm(String name) {
        File file = new File(workingDirectory, name);

        // Check if the file exists
        if (!file.exists()) {
            System.out.println("Error: File does not exist: " + file.getAbsolutePath());
            return; // Early exit if file does not exist
        }

        // Attempt to delete the file
        if (file.delete()) {
            System.out.println("Deleted the file: " + file.getAbsolutePath());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }


    public void cat(){
        Scanner scan=new Scanner(System.in);
        String input = scan.nextLine();
        System.out.println(input);
    }
    public void cat(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Error: No file name provided.");
            return;
        }

        File file = new File(workingDirectory, name);
        if (file.isDirectory()){
            System.out.println("Error: This is does Directory: " + file.getAbsolutePath());
            return;
        }
        if (!file.exists()) {
            System.out.println("Error: File does not exist: " + file.getAbsolutePath());
            return;
        }

        // Using try-with-resources for automatic resource management
        try (Scanner in = new Scanner(file)) {
            System.out.println("Reading file: " + name);
            while (in.hasNextLine()) {
                String data = in.nextLine();
                System.out.println(data);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Function to print available commands
    private static void help() {
        System.out.println("Available Commands:");
        System.out.println("  pwd      : Print the current directory.");
        System.out.println("  cd [dir] or .. : Change the directory.");
        System.out.println("  ls       : List directory contents.");
        System.out.println("  ls -a    : List all files, including hidden ones.");
        System.out.println("  ls -r    : List directory contents in reverse order.");
        System.out.println("  mkdir [dir] : Create a new directory.");
        System.out.println("  rmdir [dir] : Remove an empty directory.");
        System.out.println("  touch [file] : Create an empty file.");
        System.out.println("  mv [src] [dest] : Move or rename a file.");
        System.out.println("  rm [file] : Delete a file.");
        System.out.println("  cat [file] : Display the contents of a file.");
        System.out.println("  > [file] : Redirect output to a file.");
        System.out.println("  >> [file] : Append output to a file.");
        System.out.println("  | : Pipe output of one command to another.");
        System.out.println("  exit : Exit the CLI.");
        System.out.println("  help : Display this help message.");
    }
    private static void exit() {
        CLI_open = false;
    }
    public static void mv() {
        if (myParser.getsize() < 3) {
            System.out.println("Usage: mv <source> <destination>");
            return;
        }

        String sourcePath = myParser.getPath(1);
        String destinationPath = myParser.getPath(myParser.getsize() - 1);

        File sourceFile = new File(workingDirectory, sourcePath);
        File destinationFile = new File(workingDirectory, destinationPath);

        if (!sourceFile.exists()) {
            System.out.println("Error: Source file does not exist: " + sourcePath);
            return;
        }

        // Check if destination is a directory
        if (destinationFile.isDirectory()) {
            // Move the file to the directory with the same name
            try {
                Files.move(sourceFile.toPath(), new File(destinationFile, sourceFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved: " + sourceFile.getName() + " -> " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error: Unable to move the file. " + e.getMessage());
            }
        } else {
            // Rename the file
            try {
                Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Renamed: " + sourceFile.getName() + " -> " + destinationFile.getName());
            } catch (IOException e) {
                System.out.println("Error: Unable to rename the file. " + e.getMessage());
            }
        }
    }

    // New method to handle redirection and piping
    private void handleRedirectionAndPiping(String[] command) throws IOException, InterruptedException {
        // Check for redirection operators
        int redirectIndex = findOperatorIndex(command, ">", ">>");
        int pipeIndex = findOperatorIndex(command, "|");

        if (redirectIndex != -1) {
            String operator = command[redirectIndex].equals(">") ? ">" : ">>";
            String[] cmd = Arrays.copyOfRange(command, 0, redirectIndex);
            String filename = command[redirectIndex + 1];

            // Use the built-in method for listing files instead of external command
            if (cmd.length == 1 && cmd[0].equals("ls")) {
                // List files in the working directory and redirect to file
                try (PrintWriter writer = new PrintWriter(new FileWriter( filename, operator.equals(">>")))) {
                    String[] files = workingDirectory.list();
                    if (files != null) {
                        for (String file : files) {
                            if (!file.startsWith(".")) { // Exclude hidden files
                                writer.println(file);
//                                System.out.println(file);
                            }
                        }
                    }
                    System.out.println("Output redirected to: " + filename);
                } catch (IOException e) {
                    System.out.println("Error writing to file: " + e.getMessage());
                }

            } else {
                // Handle external command if cmd is not just "ls"
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectOutput(operator.equals(">") ?
                        ProcessBuilder.Redirect.to(new File(filename)) :
                        ProcessBuilder.Redirect.appendTo(new File(filename)));
                Process process = pb.start();
                process.waitFor();
                System.out.println("Output redirected to: " + filename);
            }

        } else if (pipeIndex != -1) {
            // Handle piping
            String[] firstCmd = Arrays.copyOfRange(command, 0, pipeIndex);
            String[] secondCmd = Arrays.copyOfRange(command, pipeIndex + 1, command.length);

            ProcessBuilder pb1 = new ProcessBuilder(firstCmd);
            ProcessBuilder pb2 = new ProcessBuilder(secondCmd);
            pb1.redirectErrorStream(true); // Redirect error stream to the output stream of p1

            Process p1 = pb1.start();
            try (InputStream is = p1.getInputStream(); OutputStream os = pb2.start().getOutputStream()) {
                is.transferTo(os);
            }

            p1.waitFor();
            System.out.println("Piped output from first command to second command.");
        }


    }  // not done


    private static int findOperatorIndex(String[] command, String... operators) {
        for (int i = 0; i < command.length; i++) {
            for (String operator : operators) {
                if (command[i].equals(operator)) {
                    return i;
                }
            }
        }
        return -1; // No operator found
    }

}

