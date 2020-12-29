package com.freelance.solutionhub.mma.model;

public class Payload{
    public String event_publisher;
    public String mso_id;
    public String mso_type;
    public String status;
    public String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEvent_publisher() {
        return event_publisher;
    }

    public void setEvent_publisher(String event_publisher) {
        this.event_publisher = event_publisher;
    }

    public String getMso_id() {
        return mso_id;
    }

    public void setMso_id(String mso_id) {
        this.mso_id = mso_id;
    }

    public String getMso_type() {
        return mso_type;
    }

    public void setMso_type(String mso_type) {
        this.mso_type = mso_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}