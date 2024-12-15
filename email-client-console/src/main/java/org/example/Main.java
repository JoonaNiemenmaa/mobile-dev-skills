package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add("joona@localhost");
        Sender sender = Sender.getInstance();
        sender.sendMail(new Email("joona@localhost", recipients, "Test email", "Moi!\r\nJoona on tosi siisti kooderi :)\r\nmoikka\r\n"));
        ArrayList<String> verify_list = new ArrayList<>();
        verify_list.add("joona");
        verify_list.add("enoleolemassa");
        verify_list.add("joona@localhost");
        int[] results = sender.verifyUser(verify_list);
        for (int result : results) {
            System.out.println(result);
        }
    }
}