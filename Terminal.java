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
    public static boolean CLI_open = true;
    public Terminal(){
        workingDirectory = new File(System.getProperty("user.home"));
        myParser= new Parser();
    }
    public boolean start(String input) throws IOException, InterruptedException {
        myParser.initializeCommand(input);
        boolean append = false ;
        if (input.contains("|")) {
            pipeCommand(input); // not done
            return CLI_open;
        }
        if (myParser.isVaildcommand()){
            String filePath = null;

            if (input.contains(">>")) {
                String[] parts = input.split(">>");
                append = true;
                input = parts[0].trim();
                myParser.initializeCommand(input);
                filePath = parts[1].trim();
            } else if (input.contains(">")) {
                String[] parts = input.split(">");
                input = parts[0].trim();
                myParser.initializeCommand(input);
                filePath = parts[1].trim();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            if (filePath != null) {
                outputStream = new ByteArrayOutputStream();
                System.setOut(new PrintStream(outputStream));
            }
            execute();
            if (filePath != null) {
                System.setOut(originalOut);
                String commandOutput = outputStream.toString();
                if (append) {
                    appendToFile(new String[] { commandOutput }, filePath);
                } else {
                    writeToFile(new String[] { commandOutput }, filePath);
                }
            }
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
            case "cd":
                if (!myParser.haveArgs()){
                    pwd();
                    break;
                }
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
                 // done

                if(myParser.haveArgs() && myParser.getarg().equals("-r"))
                    rm(myParser.getPath(2), true); // done
                else
                    rm(myParser.getPath(),false);
                break;
            case "cat": // done
                if(myParser.haveArgs())
                    for(int i = 1 ; i < myParser.getsize() ; i++){
                        cat(myParser.getPath(i));
                    }
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

    static public void pwd(){
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

     static public void ls(){
        String[] files = workingDirectory.list();
        for (int i = 0 ; i < files.length ; i++) {
            if(files[i].charAt(0)=='.') continue;
            System.out.println(files[i]);
        }
    }
    static public void ls(String arg){
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
                System.out.println(files[i]);
            }
        }
        else{
            System.out.println("invalid option");
        }
    }
    public void mkdir(String name) {

        if (name.isEmpty()) {
            System.out.println("Missing argument");
            return;
        }
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
        if (name.isEmpty()) {
            System.out.println("Missing argument");
            return;
        }
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
            System.out.println("Missing Argument");
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
    public void rm(String name, boolean deleteDirectory) {
        if (name.isEmpty()) {
            System.out.println("Missing argument");
            return;
        }
        File file = new File(workingDirectory, name);


        if (file.isDirectory()){
            if (deleteDirectory){
                if (file.delete()) {
                    System.out.println("Deleted the directory: " + file.getAbsolutePath());
                } else {
                    System.out.println("Failed to delete the directory.");
                }
            }
            else {
                System.out.println("Cannot Delete a directory (if you want to delete a directory use -r)");
            }
                return;
        }

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


    static public void cat(){
        Scanner scan=new Scanner(System.in);
        String input = scan.nextLine();
        System.out.println(input);
    }
    static public void cat(String name) {
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
    public static void help() {
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
        System.out.println("  | : Pipe output of one command to another. [ grep[pattern] , wc ]");
        System.out.println("  exit : Exit the CLI.");
        System.out.println("  help : Display this help message.");
    }
    public static void exit() {
        CLI_open = false;
    }
    public static void mv() {
        boolean morethan1 = myParser.getsize() > 3;
        if (myParser.getsize() < 3) {
            System.out.println("Usage: mv <source> <destination>");
            return;
        }


        for (int i = 1; i < myParser.getsize() - 1; i++){

            String sourcePath = myParser.getPath(i);
            String destinationPath = myParser.getPath(myParser.getsize() - 1);

            File sourceFile = new File(workingDirectory, sourcePath);
            File destinationFile = new File(workingDirectory, destinationPath);

            if (!sourceFile.exists()) {
                System.out.println("Error: Source file does not exist: " + sourcePath);
                continue;
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
                if (morethan1) {
                    System.out.println("Error: No destination folder ");
                    return;
                }
                // Rename the file
                try {
                    Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Renamed: " + sourceFile.getName() + " -> " + destinationFile.getName());
                } catch (IOException e) {
                    System.out.println("Error: Unable to rename the file. " + e.getMessage());
                }
            }

        }

    }

    public static void pipeCommand(String input) {
        myParser.initializeCommand(input);

        String firstCommand = myParser.getCommand();
        String secondCommand = myParser.getPath(1).trim();  // Added trim()

//        System.out.println("First Command: " + firstCommand);  // Debugging line
//        System.out.println("Second Command: " + secondCommand);  // Debugging line

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        String[] firstCommandParts = firstCommand.split("\\s+");
        String firstCmd = firstCommandParts[0];

        String[] firstArgs = firstCommandParts.length > 1 ? Arrays.copyOfRange(firstCommandParts, 1, firstCommandParts.length) : new String[0];
        if (firstCmd.equals("pwd")) pwd();
        else if (firstCmd.equals("ls")){
            myParser.initializeCommand(firstCommand);
            if(myParser.haveArgs())
                ls(myParser.getarg());
            else ls();
        }
        else if (firstCmd.equals("cat")){
            myParser.initializeCommand(firstCommand);
            if(myParser.haveArgs())
                cat(myParser.getPath());
            else
                cat();
        }
        else {
            System.setOut(originalOut);
            System.out.println("Unsupported command for piping: " + firstCmd);
            return;
        }

        System.setOut(originalOut);
        String pipedInput = outputStream.toString().trim();

//        System.out.println("Processing second command: " + secondCommand);

        if (secondCommand.startsWith("grep")) {
            String[] grepParts = secondCommand.split("\\s+");
            if (grepParts.length == 2) {
                grep(pipedInput, grepParts[1]);
            } else {
                System.out.println("Usage: grep <pattern>");
            }
        } else if (secondCommand.startsWith("wc")) {
            String[] wcParts = secondCommand.split("\\s+");
            if (wcParts.length == 1) {
                wc(pipedInput);
            } else {
                System.out.println("Usage: wc");
            }
        } else {
            System.out.println("Unsupported command for piped input: " + secondCommand);
        }
    }

    public static void grep(String input, String pattern) {
        String[] lines = input.split("\n");
        for (String line : lines) {
            if (line.contains(pattern)) {
                System.out.println(line);
            }
        }
    }
    public static void wc(String input) {
        String[] lines = input.split("\n");
        int itemCount = Arrays.stream(lines).mapToInt(line -> line.split("\\s+").length).sum();
        System.out.println("Word Count: " + itemCount);
    }


    public static void writeToFile(String[] args, String filePath) {
        File file = new File(workingDirectory,filePath);
        try (FileWriter writer = new FileWriter(file, false)) {
            for (String arg : args) {
                writer.write(arg + System.lineSeparator());
            }
            System.out.println("Output written to " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file");
        }
    }


    public static void appendToFile(String[] args, String filePath) {
        File file = new File(workingDirectory,filePath);
        try (FileWriter writer = new FileWriter(file,true)) {
            for (String arg : args) {
                writer.append(arg + System.lineSeparator());
            }
//            writer.append("agggg");
            System.out.println("Output appended to " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while appending to the file");
        }
    }


}

