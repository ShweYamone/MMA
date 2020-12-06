package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PMServiceInfoModel{
    @SerializedName("id")
    String id;

    @SerializedName("priorityLevel")
    String priorityLevel;

    @SerializedName("busStopId")
    String busStopId;

    @SerializedName("busStopLocation")
    String busStopLocation;

    @SerializedName("panelId")
    String panelId;

    @SerializedName("reportedProblem")
    String reportedProblem;

    @SerializedName("cause")
    String cause;

    @SerializedName("actualProblem")
    String actualProblem;

    @SerializedName("remedy")
    String remedy;

    @SerializedName("serviceOrderStatus")
    String serviceOrderStatus;

    @SerializedName("responseTime")
    String responseTime;

    @SerializedName("resolutionTime")
    String resolutionTime;

    @SerializedName("creationDate")
    String creationDate;

    @SerializedName("notificationDate")
    String notificationDate;

    @SerializedName("jobCompletionDate")
    String jobCompletionDate;

    @SerializedName("acknowledgementDate")
    String acknowledgementDate;

    /*

        {
            "id": "PM20202C9F3AE6",
            "priorityLevel": null,
            "busStopId": null,
            "busStopLocation": null,
            "panelId": "pie0108",
            "reportedProblem": null,
            "cause": null,
            "actualProblem": null,
            "remedy": null,
            "serviceOrderStatus": "INPRG",
            "responseTime": "153:06",
            "resolutionTime": null,
            "creationDate": "2020-11-25 01:53:49",
            "notificationDate": "2020-11-25 01:53:49",
            "jobCompletionDate": null,
            "acknowledgementDate": "2020-01-01 09:00:00"
        }
    ]
     */

    public PMServiceInfoModel(String id, String priorityLevel, String busStopId, String busStopLocation, String panelId, String reportedProblem, String cause, String actualProblem, String remedy, String serviceOrderStatus, String responseTime, String resolutionTime, String creationDate, String notificationDate, String jobCompletionDate, String acknowledgementDate) {
        this.id = id;
        this.priorityLevel = priorityLevel;
        this.busStopId = busStopId;
        this.busStopLocation = busStopLocation;
        this.panelId = panelId;
        this.reportedProblem = reportedProblem;
        this.cause = cause;
        this.actualProblem = actualProblem;
        this.remedy = remedy;
        this.serviceOrderStatus = serviceOrderStatus;
        this.responseTime = responseTime;
        this.resolutionTime = resolutionTime;
        this.creationDate = creationDate;
        this.notificationDate = notificationDate;
        this.jobCompletionDate = jobCompletionDate;
        this.acknowledgementDate = acknowledgementDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(String busStopId) {
        this.busStopId = busStopId;
    }

    public String getBusStopLocation() {
        return busStopLocation;
    }

    public void setBusStopLocation(String busStopLocation) {
        this.busStopLocation = busStopLocation;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public String getReportedProblem() {
        return reportedProblem;
    }

    public void setReportedProblem(String reportedProblem) {
        this.reportedProblem = reportedProblem;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getActualProblem() {
        return actualProblem;
    }

    public void setActualProblem(String actualProblem) {
        this.actualProblem = actualProblem;
    }

    public String getRemedy() {
        return remedy;
    }

    public void setRemedy(String remedy) {
        this.remedy = remedy;
    }

    public String getServiceOrderStatus() {
        return serviceOrderStatus;
    }

    public void setServiceOrderStatus(String serviceOrderStatus) {
        this.serviceOrderStatus = serviceOrderStatus;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(String resolutionTime) {
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

    public String getJobCompletionDate() {
        return jobCompletionDate;
    }

    public void setJobCompletionDate(String jobCompletionDate) {
        this.jobCompletionDate = jobCompletionDate;
    }

    public String getAcknowledgementDate() {
        return acknowledgementDate;
    }

    public void setAcknowledgementDate(String acknowledgementDate) {
        this.acknowledgementDate = acknowledgementDate;
    }
}
