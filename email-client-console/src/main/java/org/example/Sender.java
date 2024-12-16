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

    // Socket timeouts in java are measured in milliseconds
    // These socket timeouts are based on values provided in the RFC

    final private int MINUTE = 1000 * 60; // Minute in milliseconds

    private int INITIAL_TIMEOUT = MINUTE * 5; // Timeout for the initial 220 greeting message from the server
    private int EHLO_HELO_TIMEOUT = MINUTE * 5;
    private int MAIL_FROM_TIMEOUT = MINUTE * 5;
    private int RCPT_TO_TIMEOUT = MINUTE * 5;
    private int DATA_INITIATE_TIMEOUT = MINUTE * 2;
    private int DATA_BLOCK_TIMEOUT = MINUTE * 3;    // Java's socket API does not support changing timeouts for write operations thus this field is unfortunately unused for now
    private int DATA_TERMINATE_TIMEOUT = MINUTE * 10;

    private int VRFY_TIMEOUT = MINUTE * 5;  // Timeouts for the VRFY command were not provided in the RFC
                                            // So I just placed it at five minutes to match the other timeouts

    private int QUIT_TIMEOUT = 5000; // Again, no provided value in RFC. Since were closing the connection anyway might as well not have a lengthy timeout

    private Sender() {}

    public static Sender getInstance() {
        if (sender == null) {
            sender = new Sender();
        }
        return sender;
    }

    private int writeMessage(String message, int timeout, Socket socket) throws IOException {
        DataOutputStream socket_out = new DataOutputStream(socket.getOutputStream());
        DataInputStream socket_in = new DataInputStream(socket.getInputStream());
        System.out.println("C: " + message);
        message = message + "\r\n"; // Each command sent to the server has to end with <CR><LF> for the server to take action
        socket_out.write(message.getBytes(StandardCharsets.US_ASCII)); // All messages written to the server have to be in US 7-bit ASCII
        return readLine(timeout, socket);
    }

    private int readLine(int timeout, Socket socket) throws IOException {
        DataInputStream socket_in = new DataInputStream(socket.getInputStream());
        String response = "";
        socket.setSoTimeout(timeout);
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

            reply = readLine(INITIAL_TIMEOUT, socket);
            if (reply == 220) { // Initial reply 220 means that the server has accepted the connection and is ready
                reply = writeMessage("EHLO " + HOST_ADDRESS, EHLO_HELO_TIMEOUT, socket);
                if (reply != 250) { // Reply 250 generally indicates success
                    reply = writeMessage("HELO " + HOST_ADDRESS, EHLO_HELO_TIMEOUT, socket);
                }
                if (reply == 250) {
                    reply = writeMessage("MAIL FROM:<" + email.getSender() + ">", MAIL_FROM_TIMEOUT, socket);
                }
                if (reply == 250) {
                    for (String recipient : email.getRecipients()) {
                        reply = writeMessage("RCPT TO:<" + recipient + ">", RCPT_TO_TIMEOUT, socket);
                        if (reply != 250) break;
                    }
                }
                if (reply == 250) {
                    reply = writeMessage("DATA", DATA_INITIATE_TIMEOUT, socket);
                    if (reply == 354) {
                        System.out.print(email.getContent());
                        socket_out.write(email.getContent().getBytes(StandardCharsets.US_ASCII));
                        reply = writeMessage(".", DATA_TERMINATE_TIMEOUT, socket);
                    }
                }
            }
            writeMessage("QUIT", QUIT_TIMEOUT, socket);
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

            reply = readLine(INITIAL_TIMEOUT, socket);

            if (reply == 220) {
                reply = writeMessage("EHLO " + HOST_ADDRESS, EHLO_HELO_TIMEOUT, socket);
                if (reply != 250) {
                    reply = writeMessage("HELO " + HOST_ADDRESS, EHLO_HELO_TIMEOUT, socket);
                }
                if (reply == 250) {
                    reply = writeMessage("VRFY " + user, VRFY_TIMEOUT, socket);
                }
            }

            writeMessage("QUIT", QUIT_TIMEOUT, socket);
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

            reply = readLine(INITIAL_TIMEOUT, socket);

            if (reply == 220) {
                reply = writeMessage("EHLO " + HOST_ADDRESS, EHLO_HELO_TIMEOUT, socket);
                if (reply != 250) {
                    reply = writeMessage("HELO " + HOST_ADDRESS, EHLO_HELO_TIMEOUT, socket);
                }
                if (reply == 250) {
                    for (int i = 0; i < users.size(); i++) {
                        reply = writeMessage("VRFY " + users.get(i), VRFY_TIMEOUT, socket);
                        result[i] = reply;
                    }
                }
            }

            writeMessage("QUIT", QUIT_TIMEOUT, socket);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // The RFC states that the timeouts for commands should be easily configurable, hence the setters

    public void setInitialTimeout(int INITIAL_TIMEOUT) {
        this.INITIAL_TIMEOUT = INITIAL_TIMEOUT;
    }

    public void setGreetingTimeout(int EHLO_HELO_TIMEOUT) {
        this.EHLO_HELO_TIMEOUT = EHLO_HELO_TIMEOUT;
    }

    public void setMailFromTimeout(int MAIL_FROM_TIMEOUT) {
        this.MAIL_FROM_TIMEOUT = MAIL_FROM_TIMEOUT;
    }

    public void setRcptToTimeout(int RCPT_TO_TIMEOUT) {
        this.RCPT_TO_TIMEOUT = RCPT_TO_TIMEOUT;
    }

    public void setDataInitiateTimeout(int DATA_INITIATE_TIMEOUT) {
        this.DATA_INITIATE_TIMEOUT = DATA_INITIATE_TIMEOUT;
    }

    public void setDataBlockTimeout(int DATA_BLOCK_TIMEOUT) {
        this.DATA_BLOCK_TIMEOUT = DATA_BLOCK_TIMEOUT;
    }

    public void setDataTerminateTimeout(int DATA_TERMINATE_TIMEOUT) {
        this.DATA_TERMINATE_TIMEOUT = DATA_TERMINATE_TIMEOUT;
    }

    public void setVrfyTimeout(int VRFY_TIMEOUT) {
        this.VRFY_TIMEOUT = VRFY_TIMEOUT;
    }

    public void setQUIT_TIMEOUT(int QUIT_TIMEOUT) {
        this.QUIT_TIMEOUT = QUIT_TIMEOUT;
    }
}
