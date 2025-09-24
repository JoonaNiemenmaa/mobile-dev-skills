package org.example;

public class IMAPProtocolException extends RuntimeException {
    final private String reply;
    public IMAPProtocolException(String reply) {
        super(reply);
        this.reply = reply;
    }
    public String getReply() {
        return reply;
    }
}
