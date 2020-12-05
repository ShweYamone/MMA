package com.freelance.solutionhub.mma.model;

public class NotificationModel {
    String messageHead;
    String messageBody;
    String messageDateTime;

    public NotificationModel(String messageHead, String messageBody, String messageDateTime) {
        this.messageHead = messageHead;
        this.messageBody = messageBody;
        this.messageDateTime = messageDateTime;
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
