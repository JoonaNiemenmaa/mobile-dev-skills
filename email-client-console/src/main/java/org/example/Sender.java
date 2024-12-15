package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Sender {
    final private int SMTP_PORT = 25;
    final private String ADDRESS = "localhost"; // The client is only expected to work with a single mail server, hence the hardcoded address

    static private Sender sender;

    private Sender() {}

    public static Sender getInstance() {
        if (sender == null) {
            sender = new Sender();
        }
        return sender;
    }

    private int writeMessage(String message, DataOutputStream socket_out, DataInputStream socket_in) throws IOException {
        System.out.println("C: " + message);
        message = message + "\r\n"; // Each command sent to the server has to end with <CR><LF> for the server to take action
        socket_out.write(message.getBytes(StandardCharsets.US_ASCII)); // All messages written to the server have to be in US 7-bit ASCII
        return readLine(socket_in);
    }

    private int readLine(DataInputStream socket_in) throws IOException {
        String response = "";
        char c = (char)socket_in.read();
        while (socket_in.available() != 0) {
            response = response + c;
            c = (char)socket_in.read();
        }
        response = response + c;
        System.out.print("S: " + response);
        int reply = 0;
        if (response.length() > 3) {
            reply = Integer.parseInt(response.substring(0, 3));
        }
        return reply; // Only returns the response code
    }

    public int sendMail(Email email) {
        Socket socket = null;
        int reply = 0;
        try {
            socket = new Socket(ADDRESS, SMTP_PORT);

            String HOST_ADDRESS = socket.getLocalAddress().getHostAddress();

            System.out.println("--- NEW CONNECTION ---");

            DataOutputStream socket_out = new DataOutputStream(socket.getOutputStream());
            DataInputStream socket_in = new DataInputStream(socket.getInputStream());

            reply = readLine(socket_in);
            if (reply == 220) { // Initial reply 220 means that the server has accepted the connection and is ready
                reply = writeMessage("EHLO " + HOST_ADDRESS, socket_out, socket_in);
                if (reply != 250) { // Reply 250 generally indicates success
                    reply = writeMessage("HELO " + HOST_ADDRESS, socket_out, socket_in);
                }
                if (reply == 250) {
                    reply = writeMessage("MAIL FROM:<" + email.getSender() + ">", socket_out, socket_in);
                }
                if (reply == 250) {
                    for (String recipient : email.getRecipients()) {
                        reply = writeMessage("RCPT TO:<" + recipient + ">", socket_out, socket_in);
                        if (reply != 250) break;
                    }
                }
                if (reply == 250) {
                    reply = writeMessage("DATA", socket_out, socket_in);
                    if (reply == 354) {
                        System.out.print(email.getContent());
                        socket_out.write(email.getContent().getBytes(StandardCharsets.US_ASCII));
                        reply = writeMessage(".", socket_out, socket_in);
                    }
                }
            }
            writeMessage("QUIT", socket_out, socket_in);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return reply; // Returns the last return code (Excluding the QUIT command) for error reporting
    }

    public int verifyUser(String user) {
        int reply = 0;
        Socket socket = null;
        try {
            socket = new Socket(ADDRESS, SMTP_PORT);

            String HOST_ADDRESS = socket.getLocalAddress().getHostAddress();

            System.out.println("--- NEW CONNECTION ---");

            DataOutputStream socket_out = new DataOutputStream(socket.getOutputStream());
            DataInputStream socket_in = new DataInputStream(socket.getInputStream());

            reply = readLine(socket_in);

            if (reply == 220) {
                reply = writeMessage("EHLO " + HOST_ADDRESS, socket_out, socket_in);
                if (reply != 250) {
                    reply = writeMessage("HELO " + HOST_ADDRESS, socket_out, socket_in);
                }
                if (reply == 250) {
                    reply = writeMessage("VRFY " + user, socket_out, socket_in);
                }
            }

            writeMessage("QUIT", socket_out, socket_in);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return reply;
    }

    public int[] verifyUser(ArrayList<String> users) {
        int reply = 0;
        int[] result = new int[users.size()];
        Socket socket = null;
        try {
            socket = new Socket(ADDRESS, SMTP_PORT);

            String HOST_ADDRESS = socket.getLocalAddress().getHostAddress();

            System.out.println("--- NEW CONNECTION ---");

            DataOutputStream socket_out = new DataOutputStream(socket.getOutputStream());
            DataInputStream socket_in = new DataInputStream(socket.getInputStream());

            reply = readLine(socket_in);

            if (reply == 220) {
                reply = writeMessage("EHLO " + HOST_ADDRESS, socket_out, socket_in);
                if (reply != 250) {
                    reply = writeMessage("HELO " + HOST_ADDRESS, socket_out, socket_in);
                }
                if (reply == 250) {
                    for (int i = 0; i < users.size(); i++) {
                        reply = writeMessage("VRFY " + users.get(i), socket_out, socket_in);
                        result[i] = reply;
                    }
                }
            }

            writeMessage("QUIT", socket_out, socket_in);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
