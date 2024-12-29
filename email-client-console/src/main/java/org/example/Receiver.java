package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class Receiver extends Thread {

    final private String ADDRESS = "localhost"; // Again, address is hardcoded since the program is only meant to work with a local mail server
    final private int IMAP_PORT = 3143;

    final private String name;
    final private String password;

    final private ArrayList<String> action_queue = new ArrayList<>();
    final private ArrayList<String> mailboxes = new ArrayList<>();
    final private HashMap<String, String> replies = new HashMap<>();

    public Receiver(String name, String password) {
        super();
        this.name = name;
        this.password = password;
    }

    public void addAction(String action) {
        action_queue.add(action);
    }

    public String getReply(String code) {
        return replies.remove(code);
    }

    @Override
    public void run() throws IMAPProtocolException {
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
            capabilities.removeFirst(); // First two items in response are not listed capabilities, but rather the command name and untagged response code
            capabilities.removeFirst();
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
                message = ++message_code + " LOGIN " + name + " " + password + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
            }

            if (response.startsWith(message_code + " OK")) {

                /*message = ++message_code + " NAMESPACE" + crlf; // Namespace command not supported in IMAP4rev1
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                String[] namespace = response.split("\"");
                String prefix = namespace[1];
                String delimiter = namespace[3];
                response = reader.readLine();
                System.out.println("S: " + response);*/

                message = ++message_code + " LIST \"\" \"*\""  + crlf;
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                if (response.startsWith("*")) {
                    String[] split = response.split(" ");
                    mailboxes.add(split[split.length - 1]);
                }
                while (response.startsWith("*")) {
                    response = reader.readLine();
                    if (response.startsWith("*")) {
                        String[] split = response.split(" ");
                        mailboxes.add(split[split.length - 1]);
                    }
                    System.out.println("S: " + response);
                }
                System.out.println(mailboxes);

                message = ++message_code + " SELECT INBOX"  + crlf; // An IMAP server implicitly always has a mailbox called INBOX
                writer.printf(message);
                System.out.print("C: " + message);
                response = reader.readLine();
                System.out.println("S: " + response);
                if (response.startsWith(message_code + " NO") || response.startsWith(message_code + " BAD")) {
                    throw new IMAPProtocolException(response);
                }
                while (response.startsWith("*")) {
                    response = reader.readLine();
                    System.out.println("S: " + response);
                }

                while (true) {
                    while (action_queue.isEmpty()) {
                        Thread.sleep(100);
                    }
                    String command = action_queue.removeFirst();
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
