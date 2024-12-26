package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Sender {
    final private int SMTP_PORT = 25;
    final private String ADDRESS = "localhost"; // The client is only expected to work with a single mail server, hence the hardcoded address

    // Socket timeouts in java are measured in milliseconds
    // These socket timeouts are based on values provided in the RFC

    final private int MINUTE = 1000 * 60; // Minute in milliseconds

    public int INITIAL_TIMEOUT = MINUTE * 5; // Timeout for the initial 220 greeting message from the server
    public int EHLO_HELO_TIMEOUT = MINUTE * 5;
    public int MAIL_FROM_TIMEOUT = MINUTE * 5;
    public int RCPT_TO_TIMEOUT = MINUTE * 5;
    public int DATA_INITIATE_TIMEOUT = MINUTE * 2;
    public int DATA_BLOCK_TIMEOUT = MINUTE * 3;    // Java's socket API does not support changing timeouts for write operations thus this field is unfortunately unused
    public int DATA_TERMINATE_TIMEOUT = MINUTE * 10;

    public int VRFY_TIMEOUT = MINUTE * 5;  // Timeouts for the VRFY command were not provided in the RFC
                                            // So I just placed it at five minutes to match the other timeouts

    public int QUIT_TIMEOUT = 5000; // Again, no provided value in RFC. Since were closing the connection anyway might as well not have a lengthy timeout

    static private Sender sender;
    private Sender() {}
    public static Sender getInstance() {
        if (sender == null) sender = new Sender();
        return sender;
    }

    public void sendMail(Email email) throws SMTPProtocolException {
        final String crlf = "\r\n";
        try (Socket socket = new Socket(ADDRESS, SMTP_PORT)) { // Address and port are hardcoded since the app is only meant to be used with a local mail server
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII), true); // SMTP requires that the commands sent using it are in US-ASCII
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socket.setSoTimeout(INITIAL_TIMEOUT);
            String reply = reader.readLine();
            System.out.println("S: " + reply);
            if (!reply.startsWith("220")) {
                throw new SMTPProtocolException(reply);
            }

            String message = "EHLO " + socket.getLocalAddress().getHostAddress() + crlf;
            writer.printf(message);
            System.out.print("C: " + message);

            socket.setSoTimeout(EHLO_HELO_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
            if (reply.startsWith("250")) {
                while (reply.startsWith("250-")) { // In multiline replies every line starts with the code and a dash except the last one which starts with the code and a space character
                    reply = reader.readLine();
                    System.out.println("S: " + reply);
                }
            } else { // Fallback to HELO if EHLO isn't supported for the initial handshake
                message = "HELO " + socket.getLocalAddress().getHostAddress() + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                reply = reader.readLine();
                if (!reply.startsWith("250")) {
                    throw new SMTPProtocolException(reply);
                }
            }

            message = "MAIL FROM:<" + email.getSender() + ">" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            socket.setSoTimeout(MAIL_FROM_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
            if (!reply.startsWith("250")) {
                throw new SMTPProtocolException(reply);
            }

            for (String recipient : email.getRecipients()) {
                message = "RCPT TO:<" + recipient + ">" + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                socket.setSoTimeout(RCPT_TO_TIMEOUT);
                reply = reader.readLine();
                System.out.println("S: " + reply);
                if (!reply.startsWith("250")) {
                    throw new SMTPProtocolException(reply);
                }
            }

            message = "DATA" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            socket.setSoTimeout(DATA_INITIATE_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
            if (!reply.startsWith("354")) {
                throw new SMTPProtocolException(reply);
            }

            writer.printf(email.getContent());
            System.out.print(email.getContent());

            writer.printf("." + crlf);
            System.out.println(".");
            socket.setSoTimeout(DATA_TERMINATE_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
            if (!reply.startsWith("250")) {
                throw new SMTPProtocolException(reply);
            }

            message = "QUIT" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            socket.setSoTimeout(QUIT_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean verifyUser(String user) throws SMTPProtocolException {
        final String crlf = "\r\n";
        boolean result = false;
        try (Socket socket = new Socket(ADDRESS, SMTP_PORT)) { // Address and port are hardcoded since the app is only meant to be used with a local mail server
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII), true); // SMTP requires that the commands sent using it are in US-ASCII
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socket.setSoTimeout(INITIAL_TIMEOUT);
            String reply = reader.readLine();
            System.out.println("S: " + reply);
            if (!reply.startsWith("220")) {
                throw new SMTPProtocolException(reply);
            }

            String message = "EHLO " + socket.getLocalAddress().getHostAddress() + crlf;
            writer.printf(message);
            System.out.print("C: " + message);

            socket.setSoTimeout(EHLO_HELO_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
            if (reply.startsWith("250")) {
                while (reply.startsWith("250-")) { // In multiline replies every line starts with the code and a dash except the last one which starts with the code and a space character
                    reply = reader.readLine();
                    System.out.println("S: " + reply);
                }
            } else { // Fallback to HELO if EHLO isn't supported for the initial handshake
                message = "HELO " + socket.getLocalAddress().getHostAddress() + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                reply = reader.readLine();
                if (!reply.startsWith("250")) {
                    throw new SMTPProtocolException(reply);
                }
            }

            message = "VRFY " + user + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            socket.setSoTimeout(VRFY_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
            result = reply.startsWith("250") || reply.startsWith("251") || reply.startsWith("252");

            message = "QUIT" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            socket.setSoTimeout(QUIT_TIMEOUT);
            reply = reader.readLine();
            System.out.println("S: " + reply);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
