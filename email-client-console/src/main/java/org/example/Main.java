package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("tomi@localhost");
        Sender sender = Sender.getInstance();
        try {
            sender.sendMail(new Email("joona@localhost", recipients, "Test email", "Moi!\r\nJoona on tosi siisti kooderi :)\r\nmoikka\r\n"));
        } catch (SMTPProtocolException exception) {
            System.err.print("Error: " + exception.getReply());
        }
/*        ArrayList<String> verify_list = new ArrayList<>();
        verify_list.add("joona");
        verify_list.add("enoleolemassa");
        verify_list.add("joona@localhost");
        int[] results = sender.verifyUser(verify_list);
        for (int result : results) {
            System.out.println(result);
        }*/
    }
}