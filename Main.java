import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal myTerminal = new Terminal();
        Scanner scan=new Scanner(System.in);
        boolean CLI_open = true ;
        while (CLI_open){

            System.out.print(myTerminal.workingDirectory.getAbsolutePath() + " :~$ ");
            String input = scan.nextLine();
//            System.out.println();
            CLI_open = myTerminal.start(input);
        }
    }

}