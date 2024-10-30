import java.util.Arrays;
public class Parser {
    private String[] command;
    String[] allCommands = {"pwd", "cd", "ls","mkdir", "rmdir", "touch","rm","cat"};
    private String[] args;


    public void initializeCommand(String input){
        if(command!=null)
            Arrays.fill(command, null);
        command = input.split(" ");
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
        else return true;
    }
    private static boolean isValidPath(String path) {
        String windowsPathPattern = "^[a-zA-Z]:\\\\(.+?\\\\)*.*$";
        return path.matches(windowsPathPattern)||path.equals("..");
    }
    public String getPath(){
        return command[1];
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
