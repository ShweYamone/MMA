package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PMServiceInfoModel {
    @SerializedName("id")
    public String id;

    @SerializedName("priorityLevel")
    public Object priorityLevel;

    @SerializedName("busStopId")
    public Object busStopId;

    @SerializedName("busStopLocation")
    public Object busStopLocation;

    @SerializedName("panelId")
    public String panelId;

    @SerializedName("reportedProblem")
    public Object reportedProblem;

    @SerializedName("cause")
    public Object cause;

    @SerializedName("actualProblem")
    public Object actualProblem;

    @SerializedName("remedy")
    public Object remedy;

    @SerializedName("serviceOrderStatus")
    public String serviceOrderStatus;

    @SerializedName("responseTime")
    public Object responseTime;

    @SerializedName("resolutionTime")
    public Object resolutionTime;

    @SerializedName("creationDate")
    public String creationDate;

    @SerializedName("notificationDate")
    public String notificationDate;

    @SerializedName("jobCompletionDate")
    public Object jobCompletionDate;

    @SerializedName("acknowledgementDate")
    public Object acknowledgementDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Object priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Object getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(Object busStopId) {
        this.busStopId = busStopId;
    }

    public Object getBusStopLocation() {
        return busStopLocation;
    }

    public void setBusStopLocation(Object busStopLocation) {
        this.busStopLocation = busStopLocation;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public Object getReportedProblem() {
        return reportedProblem;
    }

    public void setReportedProblem(Object reportedProblem) {
        this.reportedProblem = reportedProblem;
    }

    public Object getCause() {
        return cause;
    }

    public void setCause(Object cause) {
        this.cause = cause;
    }

    public Object getActualProblem() {
        return actualProblem;
    }

    public void setActualProblem(Object actualProblem) {
        this.actualProblem = actualProblem;
    }

    public Object getRemedy() {
        return remedy;
    }

    public void setRemedy(Object remedy) {
        this.remedy = remedy;
    }

    public String getServiceOrderStatus() {
        return serviceOrderStatus;
    }

    public void setServiceOrderStatus(String serviceOrderStatus) {
        this.serviceOrderStatus = serviceOrderStatus;
    }

    public Object getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Object responseTime) {
        this.responseTime = responseTime;
    }

    public Object getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(Object resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public Object getJobCompletionDate() {
        return jobCompletionDate;
    }

    public void setJobCompletionDate(Object jobCompletionDate) {
        this.jobCompletionDate = jobCompletionDate;
    }

    public Object getAcknowledgementDate() {
        return acknowledgementDate;
    }

    public void setAcknowledgementDate(Object acknowledgementDate) {
        this.acknowledgementDate = acknowledgementDate;
    }
}
