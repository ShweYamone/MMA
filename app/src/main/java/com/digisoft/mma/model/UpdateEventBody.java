package com.digisoft.mma.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "UpdateEventBody")
public class UpdateEventBody implements Serializable {

    @PrimaryKey()
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "actor")
    private String actor;

    @ColumnInfo(name = "actorId")
    private String actorId;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "serviceOrderId")
    private String serviceOrderId;

    @ColumnInfo(name = "serviceOrderStatus", defaultValue = "DEFAULT")
    private String serviceOrderStatus;

    @ColumnInfo(name = "remark" , defaultValue = "DEFAULT")
    private String remark;

    @ColumnInfo(name = "weatherCondition", defaultValue = "DEFAULT")
    private String weatherCondition;

    @Ignore
    private List<Event> events;

    //For Room DB store
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
    }

    /**** CM JOBDONE *****/
    @Ignore
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus,String remark,String weatherCondition){
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
        this.remark = remark;
        this.weatherCondition = weatherCondition;
    }


    /******TagIn, *****/
    @Ignore
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, List<Event> events) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.events = events;
    }

    /*****PM JOBDONE*******/
    @Ignore
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus, String remark) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
        this.remark = remark;
    }

    /*****CM_ACK_To_APPR****/
    @Ignore
    public UpdateEventBody(String actor, String actorId, String date, String serviceOrderId, String serviceOrderStatus) {
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
    }

    @Ignore
    public UpdateEventBody(String id, String actor, String actorId, String date, String serviceOrderId,String serviceOrderStatus, String remark,String weatherCondition) {
        this.id = id;
        this.actor = actor;
        this.actorId = actorId;
        this.date = date;
        this.serviceOrderId = serviceOrderId;
        this.serviceOrderStatus = serviceOrderStatus;
        this.remark = remark;
        this.weatherCondition = weatherCondition;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(String serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public String getServiceOrderStatus() {
        return serviceOrderStatus;
    }

    public void setServiceOrderStatus(String serviceOrderStatus) {
        this.serviceOrderStatus = serviceOrderStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
