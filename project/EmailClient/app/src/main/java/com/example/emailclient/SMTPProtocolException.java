package com.example.emailclient;

public class SMTPProtocolException extends Exception {
    final private int code;
    final private String reply;
    public SMTPProtocolException(String reply) {
        super(reply);
        this.reply = reply;
        code = Integer.parseInt(reply.substring(0, 3));
    }
    public int getCode() {
        return code;
    }
    public String getReply() {
        return reply;
    }
}
