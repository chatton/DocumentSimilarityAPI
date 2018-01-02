package ie.gmit.sw.users;

import java.util.Scanner;

public class ConsoleUser implements User {

    private final Scanner scanner;

    public ConsoleUser() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void write(String str) {
        System.out.print(str);
    }

    @Override
    public void writeLine(String str) {
        System.out.println(str);
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }
}
