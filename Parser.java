import java.io.File;
import java.util.Arrays;
public class Parser {
    public String[] command;
    private String[] allCommands = {"pwd", "cd", "ls","mkdir", "rmdir", "touch","rm","cat" , "help" , "exit" , "mk" , "mv"};
    private String[] args;


    public void initializeCommand(String input){
        if(command!=null)
            Arrays.fill(command, null);
        if (input.contains("|")){
            command = input.split("\\|");
        }else command = input.split(" ");
        if (command.length != 2 && input.contains("|")) {
            System.out.println("Invalid pipe command format. Usage: <command1> | <command2>");
            return;
        }
        command[0] = command[0].toLowerCase();
        if (input.contains("|")) command[1] = command[1].toLowerCase();
    }
    public String getCommand(){
        return command[0];
    }
    public boolean isVaildcommand() {
        for(String s : allCommands){
            if(s.equals(command[0])) return true;
        }
        return false;
    }
//-----------------------------------------------------------------------------
    //check file path for cd
    public boolean isValidPath() {
        if (command.length == 2) return isValidPath(command[1]);
        return false;
    }
    private static boolean isValidPath(String path) {
        // Regex for valid Windows paths
        String windowsPathPattern = "^[a-zA-Z]:\\\\([^<>:\"/\\\\|?*]+(\\\\[^<>:\"/\\\\|?*]+)*)?$";

        // Regex for valid file names (excluding forbidden characters)
        String validFileNamePattern = "^[^<>:\"/\\\\|?*]+$";

        // Allowing for ".." as a valid path
        return path.matches(windowsPathPattern) || path.equals("..") || path.matches(validFileNamePattern);
    }


    public String getPath(int i){
        if (command.length <= i) return "";
        return command[i];
    }
    public String getPath(){
        if (command.length <= 1) return "";
        return command[1];
    }
    public int getsize(){
        return command.length;
    }
//-----------------------------------------------------------------------------
    //check arg for ls
    public boolean haveArgs(){
        return command.length == 2;
    }
    public String getarg(){
        return command[1];
    }
//-----------------------------------------------------------------------------

}
