package com.freelance.solutionhub.mma.model;

import java.util.List;

public class UpdateEventBody {
    public String actor;
    public String actorId;
    public String date;
    public String serviceOrderId;
    public String serviceOrderStatus;
    public String remark;
    public String weatherCondition;
    public List<Event> events;

    /******TagIn, *****/
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, List<Event> events) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.events = events;
    }

    /*****PM JOBDONE*******/
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus, String remark) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
        this.remark = remark;
    }

    /**** CM JOBDONE *****/
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus,String remark,String weatherCondition){
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
        this.remark = remark;
        this.weatherCondition = weatherCondition;
    }

    /*****CM_ACK_To_APPR****/
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
    }
}
