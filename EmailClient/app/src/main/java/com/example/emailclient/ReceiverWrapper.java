package com.example.emailclient;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

interface Callback<T> {
    void onComplete(T result);
}

public class ReceiverWrapper {
    final private Receiver receiver;
    final private Executor executor;
    public ReceiverWrapper(Receiver receiver) {
        this.receiver = receiver;
        executor = Executors.newSingleThreadExecutor();
    }

    public void fetchMail(String mailbox, Callback<ArrayList<Email>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Email> mail = new ArrayList<>();
                receiver.addAction("SEL SELECT " + mailbox);
                String reply = null;
                while (reply == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    reply = receiver.getReply("SEL");
                }
                System.out.println(reply);

                int exists = 0;
                for (String line : reply.split("\r\n")) {
                    String[] tokens = line.split(" ");
                    if (tokens[2].equals("EXISTS")) exists = Integer.parseInt(tokens[1]);
                }

                receiver.addAction("FETCH_HEADERS FETCH 1:" + exists + " BODY[HEADER]");
                reply = null;
                while (reply == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    reply = receiver.getReply("FETCH_HEADERS");
                }

                Email email = new Email();
                for (String line : reply.split("\r\n")) {
                    if (line.startsWith("* " )) email = new Email();
                    else if (line.startsWith("To: ")) email.setRecipients(line.substring(4));
                    else if (line.startsWith("From: ")) email.setSender(line.substring(6));
                    else if (line.startsWith("Subject: ")) email.setSubject(line.substring(9));
                    else if (line.equals(")")) mail.add(email);
                }

                receiver.addAction("FETCH_TEXT FETCH 1:" + exists + " BODY[TEXT]");
                reply = null;
                while (reply == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    reply = receiver.getReply("FETCH_TEXT");
                }

                int index = -1;
                String text_content = "";
                for (String line : reply.split("\r\n")) {
                    if (line.startsWith("* ")) {
                        if (index != -1) mail.get(index).setTextContent(text_content.substring(0, text_content.stripTrailing().length() - 1));
                        index = Integer.parseInt(line.split(" ")[1]) - 1;
                        text_content = "";
                    } else if (line.startsWith("FETCH_TEXT OK")) {
                        if (index != -1) mail.get(index).setTextContent(text_content.substring(0, text_content.stripTrailing().length() - 1));
                    } else {
                        text_content = text_content + line + "\r\n";
                    }

                }

                callback.onComplete(mail);

            }
        });

    }

}
