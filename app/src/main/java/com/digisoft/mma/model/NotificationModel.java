package com.digisoft.mma.model;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    String id;
    String messageHead;
    String messageBody;
    String messageDateTime;
    boolean isRead;

    public NotificationModel(String id,String messageHead, String messageBody, String messageDateTime,boolean isRead) {
        this.id = id;
        this.messageHead = messageHead;
        this.messageBody = messageBody;
        this.messageDateTime = messageDateTime;
        this.isRead = isRead;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageHead() {
        return messageHead;
    }

    public void setMessageHead(String messageHead) {
        this.messageHead = messageHead;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageDateTime() {
        return messageDateTime;
    }

    public void setMessageDateTime(String messageDateTime) {
        this.messageDateTime = messageDateTime;
    }
}
