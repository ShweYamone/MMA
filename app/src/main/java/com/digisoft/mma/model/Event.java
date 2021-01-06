package com.digisoft.mma.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static com.digisoft.mma.util.AppConstant.NO;

@Entity(tableName = "Event")
public class Event {

    @PrimaryKey
    @ColumnInfo(name = "event_id")
    @NonNull
    public String event_id; // eventType + eventKey

    @ColumnInfo(name = "updateEventBodyKey")
    public String updateEventBodyKey;

    @ColumnInfo(name = "eventType")
    public String eventType;

    @ColumnInfo(name = "eventKey")
    public String key;

    @ColumnInfo(name = "value")
    public String value;

    @ColumnInfo(name = "alreadyUploaded", defaultValue = NO)
    public String alreadyUploaded;

    public Event(String eventType, String key, String value) {
        this.eventType = eventType;
        this.key = key;
        this.value = value;
    }

    @Ignore
    public Event(String event_id, String eventType, String key,String value){
        this.event_id = event_id;
        this.eventType = eventType;
        this.key = key;
        this.value = value;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getUpdateEventBodyKey() {
        return updateEventBodyKey;
    }

    public void setUpdateEventBodyKey(String updateEventBodyKey) {
        this.updateEventBodyKey = updateEventBodyKey;
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

    public String getAlreadyUploaded() {
        return alreadyUploaded;
    }

    public void setAlreadyUploaded(String alreadyUploaded) {
        this.alreadyUploaded = alreadyUploaded;
    }
}
