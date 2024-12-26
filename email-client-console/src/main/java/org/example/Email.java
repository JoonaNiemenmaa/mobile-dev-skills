package org.example;

import java.util.ArrayList;

public class Email {

    final private String sender;
    final private ArrayList<String> recipients;
    final private String content;

    public Email(String sender, ArrayList<String> recipients, String content) {

        this.sender = sender;
        this.recipients = recipients;
        this.content = content;

    }

    public String getSender() {
        return sender;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public String getContent() {
        return content;
    }

}
