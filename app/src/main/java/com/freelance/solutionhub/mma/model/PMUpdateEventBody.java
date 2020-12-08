package com.freelance.solutionhub.mma.model;

import java.util.List;

public class PMUpdateEventBody {
    public String actor;
    public String actorId;
    public String date;
    public String serviceOrderId;
    public String serviceOrderStatus;
    public String remark;
    public List<Event> events;

    public PMUpdateEventBody(String actor, String actorId, String date, String serviceOrderId, List<Event> events) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.events = events;
    }

    public PMUpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus, String remark) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
        this.remark = remark;
    }

}
