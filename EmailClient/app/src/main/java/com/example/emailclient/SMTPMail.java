package com.example.emailclient;

public class SMTPMail {

    final private String sender;
    final private String[] recipients;
    final private String subject;
    private String content;

    public SMTPMail(String sender, String[] recipients, String subject, String text) {

        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;

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
    }

    public String getSender() {
        return sender;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

}
