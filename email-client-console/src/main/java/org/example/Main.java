package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Sender.getInstance().sendMail(new Email("joona@localhost", "joona@localhost", "Test email", "Hello! I am very cool"));
    }
}