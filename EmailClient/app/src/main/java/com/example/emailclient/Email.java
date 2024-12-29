package com.example.emailclient;

public class Email {

    final private String sender;
    final private String[] recipients;
    private String content;

    public Email(String sender, String[] recipients, String subject, String text) {

        this.sender = sender;
        this.recipients = recipients;

        this.content = "From: <" + sender + ">\r\n";
        this.content = this.content + "To: ";
        for (int i = 0; i < recipients.length; i++) {
            if (i + 1 == recipients.length) this.content = this.content + "<" + recipients[i] + ">";
            else this.content = this.content + "<" + recipients[i] + ">, ";
        }
        this.content = this.content + "\r\n";
        this.content = this.content + "Subject: " + subject + "\r\n";
        this.content = this.content + "\r\n";
        this.content = this.content + text;
        System.out.println(this.content);
    }

    public String getSender() {
        return sender;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public String getContent() {
        return content;
    }

}
