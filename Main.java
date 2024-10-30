import java.util.Scanner;
import java.util.Vector;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        Terminal myTerminal = new Terminal();
        Scanner scan=new Scanner(System.in);
        while (true){
            System.out.print(":~$ ");
            String input = scan.nextLine();
//            System.out.println();
            myTerminal.start(input);
        }
    }

}