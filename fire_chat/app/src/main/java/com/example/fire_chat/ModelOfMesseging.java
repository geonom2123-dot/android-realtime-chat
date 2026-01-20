package com.example.fire_chat;

public class ModelOfMesseging {
    public String message;
    public String senderId;
    public long timestamp;

    // Απαραίτητος κενός constructor για το Firebase
    public ModelOfMesseging() {
    }

    public ModelOfMesseging(String message, String senderId, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getters (χρειάζονται για να διαβάζει ο Adapter τις τιμές)
    public String getMessage() { return message; }
    public String getSenderId() { return senderId; }
    public long getTimestamp() { return timestamp; }
}