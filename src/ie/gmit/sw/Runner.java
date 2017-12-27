package ie.gmit.sw;

import java.util.Scanner;

public class Runner {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        final Scanner sc = new Scanner(System.in);
        final UI ui = new UI(sc);
        ui.start();
    }
}
