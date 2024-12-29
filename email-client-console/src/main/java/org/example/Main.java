package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Sender sender = Sender.getInstance();
        Receiver receiver = new Receiver("test1", "pwd1");
        String[] recipients = new String[]{
                "test1@localhost"
        };
        Email email = new Email("test1@localhost", recipients, "Hello!", "Hello!\r\nI am very cool\r\n");
        try {
            sender.sendMail(email);
        } catch (SMTPProtocolException e) {
            throw new RuntimeException(e);
        }
        receiver.start();
        receiver.addAction("body FETCH 1 BODY[TEXT]");

    }
}