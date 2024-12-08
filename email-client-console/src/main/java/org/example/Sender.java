package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Sender {
    final private int SMTP_PORT = 25;
    final private String ADDRESS = "localhost";

    static private Sender sender;

    private Sender() {}

    public static Sender getInstance() {
        if (sender == null) {
            sender = new Sender();
        }
        return sender;
    }


    private int readLine(DataInputStream socket_in) throws IOException {
        String response = "";
        char c = 30;
        while (c != '\n') {
            c = (char)socket_in.read();
            response = response + c;
        }
        return Integer.parseInt(response.substring(0, 3)); // Only returns the response code

    }

    public void sendMail(Email email) {
        Socket socket = null;
        try {
            socket = new Socket(ADDRESS, SMTP_PORT);

            DataOutputStream socket_out = new DataOutputStream(socket.getOutputStream());
            DataInputStream socket_in = new DataInputStream(socket.getInputStream());

            System.out.print("S: " + readLine(socket_in) + "\n\r");

            String message = "EHLO koululappari\n\r";
            System.out.print("C: " + message);
            socket_out.write(message.getBytes(StandardCharsets.US_ASCII));

            System.out.print("S: " + readLine(socket_in) + "\n\r");



            message = "QUIT\n\r";
            socket_out.write(message.getBytes(StandardCharsets.US_ASCII));
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
