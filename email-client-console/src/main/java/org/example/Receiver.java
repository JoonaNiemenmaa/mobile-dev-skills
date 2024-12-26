package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Receiver {

    final private String ADDRESS = "localhost"; // Again, address is hardcoded since the program is only meant to work with a local mail server
    final private int IMAP_PORT = 143;

    private ArrayList<String> action_queue = new ArrayList<>();

    public Receiver() {}

    public void startSession(String name, String password) throws IMAPProtocolException {
        final String crlf = "\r\n";
        try (Socket socket = new Socket(ADDRESS, IMAP_PORT)) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int message_code = 0;

            String response = reader.readLine(); // Read the initial greeting from the server
            System.out.println("S: " + response);

            String message = message_code++ + " CAPABILITY" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            response = reader.readLine(); // first response to CAPABILITY includes a list of all supported features of the server unless an error occurs
            System.out.println("S: " + response);
            if (response.startsWith(message_code + "BAD")) {
                throw new IMAPProtocolException(response);
            }
            ArrayList<String> capabilities = new ArrayList<>(Arrays.asList(response.split(" ")));
            capabilities.removeFirst(); // First two items in response are not listed capabilities, but rather the command name and untagged response code
            capabilities.removeFirst();
            if (!capabilities.contains("IMAP4rev1")) {
                throw new IMAPProtocolException("Server does not support IMAP4rev1");
            }
            response = reader.readLine(); // Second response line should have an OK response code
            System.out.println("S: " + response);

            if (capabilities.contains("AUTH=PLAIN")) {
                message = message_code++ + " AUTHENTICATE PLAIN" + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                message = Base64.getEncoder().encodeToString(("\0" + name + "\0" + password).getBytes()) + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                response = reader.readLine();
                System.out.println("S: " + response);
            } else {
                message = message_code++ + " LOGIN " + name + " " + password + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                response = reader.readLine();
                System.out.println("S: " + response);
            }

            if (response.startsWith((message_code - 1) + " OK")) { // Need to subtract 1 from message code to match it with the authentication command code
                // Start the session here
            }

            message = message_code + " LOGOUT" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            response = reader.readLine();
            System.out.println("S: " + response);
            response = reader.readLine();
            System.out.println("S: " + response);

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

}
