package com.example.musicalchat.data.model;

public class Message {
    private String messageId;
    private String senderUid;
    private String text;
    private long sentAt;
    private String type; // "text" | "system"

    public Message() {
    }

    public Message(String messageId, String senderUid, String text, String type) {
        this.messageId = messageId;
        this.senderUid = senderUid;
        this.text = text;
        this.type = type;
        this.sentAt = System.currentTimeMillis();
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderUid() { return senderUid; }
    public void setSenderUid(String senderUid) { this.senderUid = senderUid; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getSentAt() { return sentAt; }
    public void setSentAt(long sentAt) { this.sentAt = sentAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
