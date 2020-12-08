package com.freelance.solutionhub.mma.model;

public class Event {
    public String eventType;
    public String key;
    public String value;

    public Event(String eventType, String key, String value) {
        this.eventType = eventType;
        this.key = key;
        this.value = value;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
