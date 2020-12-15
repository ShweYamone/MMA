package com.freelance.solutionhub.mma.model;

import java.util.Date;

public class Item{
    public String id;
    public Date inserted_at;
    public boolean is_read;
    public Payload payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getInserted_at() {
        return inserted_at;
    }

    public void setInserted_at(Date inserted_at) {
        this.inserted_at = inserted_at;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public String getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(String recipient_id) {
        this.recipient_id = recipient_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String recipient_id;
    public String type;
    public Date updated_at;
}