package org.example;

public class Email {

    final private String sender;
    final private String recipient;
    final private String subject;
    final private String content;

    public Email(String sender, String recipient, String subject, String content) {

        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;

    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() { return subject; }

    public String getContent() {
        return content;
    }

}
