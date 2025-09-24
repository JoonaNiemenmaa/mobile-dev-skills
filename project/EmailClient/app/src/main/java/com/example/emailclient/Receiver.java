package com.example.emailclient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class Receiver {

    final private String ADDRESS = "10.0.2.2"; // Again, address is hardcoded since the program is only meant to work with a local mail server
    final private int IMAP_PORT = 3143;

    final private String user_address;
    final private String password;

    final private ArrayList<String> action_queue = new ArrayList<>();
    final private HashMap<String, String> replies = new HashMap<>();

    static private Receiver receiver;
    static public Receiver getInstance() {
        return receiver;
    }
    static public void startReceiver(String user_address, String password) {
        receiver = new Receiver(user_address, password);
    }

    private Receiver(String user_address, String password) {
        this.user_address = user_address;
        this.password = password;
    }

    public void addAction(String action) {
        action_queue.add(action);
    }

    public String getReply(String code) {
        return replies.remove(code);
    }

    public void startSession(NetworkCallback<String> callback) throws IMAPProtocolException {
        final String crlf = "\r\n";
        try (Socket socket = new Socket(ADDRESS, IMAP_PORT)) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int message_code = 0;

            String response = reader.readLine(); // Read the initial greeting from the server
            System.out.println("S: " + response);

            String message = message_code + " CAPABILITY" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            response = reader.readLine(); // first response to CAPABILITY includes a list of all supported features of the server unless an error occurs
            System.out.println("S: " + response);
            if (response.startsWith(message_code + "BAD")) {
                throw new IMAPProtocolException(response);
            }
            ArrayList<String> capabilities = new ArrayList<>(Arrays.asList(response.split(" ")));
            capabilities.remove(0); // First two items in response are not listed capabilities, but rather the command name and untagged response code
            capabilities.remove(0);
            if (!capabilities.contains("IMAP4rev1")) {
                throw new IMAPProtocolException("Server does not support IMAP4rev1");
            }
            response = reader.readLine(); // Second response line should have an OK response code
            System.out.println("S: " + response);

            if (capabilities.contains("AUTH=PLAIN")) {
                message = ++message_code + " AUTHENTICATE PLAIN" + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                message = Base64.getEncoder().encodeToString(("\0" + user_address + "\0" + password).getBytes()) + crlf;
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
                message = ++message_code + " LOGIN " + user_address + " " + password + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
            }

            if (response.startsWith(message_code + " OK")) {
                callback.onComplete(response);
                while (true) {
                    while (action_queue.isEmpty()) {
                        Thread.sleep(100);
                    }
                    String command = action_queue.remove(0);
                    String code = command.split(" ")[0];

                    if (command.split(" ")[1].equals("LOGOUT")) break;

                    writer.printf(command + crlf);
                    System.out.println("C: " + command);

                    String reply = "";
                    response = reader.readLine();
                    System.out.println("S: " + response);
                    while (!response.startsWith(code)) {
                        reply = reply + response + crlf;
                        response = reader.readLine();
                        System.out.println("S: " + response);
                    }
                    reply = reply + response + crlf;

                    replies.put(code, reply);
                }
            }

            message = ++message_code + " LOGOUT" + crlf;
            writer.printf(message);
            System.out.print("C: " + message);
            response = reader.readLine();
            System.out.println("S: " + response);
            response = reader.readLine();
            System.out.println("S: " + response);

        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

}
