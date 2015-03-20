package com.tsys.utils;

public class Event {
    private final String type;
    private final java.util.Date occurredOn;

    public Event(String type) {
        this.type = type;
        occurredOn = new java.util.Date();
    }

    public String getType() {
        return type;
    }

    public java.util.Date getOccurredOn() {
        return occurredOn;
    }
}
