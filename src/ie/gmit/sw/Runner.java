package ie.gmit.sw;

import ie.gmit.sw.users.ConsoleUser;
import ie.gmit.sw.users.User;


public class Runner {

    public static void main(String[] args) {
        final User user = new ConsoleUser();
        final UI ui = new UI(user);
        ui.start();
    }
}
