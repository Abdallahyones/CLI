import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
public class Terminal {
    public static String initialDirectory = System.getProperty("user.dir");
    private File workingDirectory;
    Parser myParser;
    public Terminal(){
        workingDirectory = new File(System.getProperty("user.home"));
        myParser= new Parser();
    }
    public void start(String input){
        myParser.initializeCommand(input);
        if(myParser.isVaildcommand()){
            execute();
        }
        else{
            System.out.println("undefined command");
        }
    }
    public void execute() {
        switch (myParser.getCommand()){
            case "pwd":
                pwd();
                break;
            case "cd":
                if(myParser.isValidPath())
                    cd(myParser.getPath());
                else
                    System.out.println("invalid path");
                break;
            case "ls":
                if(myParser.haveArgs())
                    ls(myParser.getarg());
                else
                    ls();
                break;
            case "mkdir":
                mkdir(myParser.getPath());  //need validation
                break;
            case "rmdir":
                rmdir(myParser.getPath());  //need validation
                break;
            case "touch":
                touch(myParser.getPath());  //need validation
                break;
            case "rm":
                rm(myParser.getPath());
                break;
            case "cat":
                if(myParser.haveArgs())
                    cat(myParser.getPath());
                else
                    cat();
                break;
            default:
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
            File newFile= new File(filePath);
            if(!newFile.exists()){
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
//            if(files[i].charAt(0)=='.') continue;
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
            File[] files = workingDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
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
    public void rm(String name){
        File file = new File(workingDirectory,name);
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
        File file = new File(workingDirectory,name);
        if(!file.exists())
            System.out.println("File does not exists.");
        else {
            try {
                Scanner in = new Scanner(file);
                while (in.hasNextLine()) {
                    String data = in.nextLine();
                    System.out.println(data);
                }
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
}
