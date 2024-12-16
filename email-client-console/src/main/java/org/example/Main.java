package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("joona@localhost");
        Sender sender = Sender.getInstance();
        try {
            sender.sendMail(new Email("joona@localhost", recipients, "Test email", "Moi!\r\nJoona on tosi siisti kooderi :)\r\nmoikka\r\n"));
            System.out.println(sender.verifyUser("joona"));
            System.out.println(sender.verifyUser("enoleolemassa"));
        } catch (SMTPProtocolException exception) {
            System.err.print("Error: " + exception.getReply());
        }
    }
}