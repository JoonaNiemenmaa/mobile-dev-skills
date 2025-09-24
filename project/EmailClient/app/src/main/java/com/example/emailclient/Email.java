package com.example.emailclient;

import java.io.Serializable;
import java.util.ArrayList;

public class Email implements Serializable {
    private String sender = "";
    private String recipients = "";
    private String subject = "";
    private String text_content = "";

    public Email() {}
    public Email(String sender, String recipients, String subject, String text_content) {
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.text_content = text_content;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getTextContent() {
        return text_content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTextContent(String text_content) {
        this.text_content = text_content;
    }
}
