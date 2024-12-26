package org.example;

import java.util.ArrayList;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        receiver.startSession("joona", "lupu1tupu");
    }
}