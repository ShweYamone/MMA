package com.freelance.solutionhub.mma.model;

import com.freelance.solutionhub.mma.common.Pageable;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PMServiceInfoModel implements Serializable{
    public String id;
    public String priorityLevel;
    public String busStopId;
    public String busStopLocation;
    public String panelId;
    public String reportedProblem;
    public String cause;
    public String actualProblem;
    public String remedy;
    public String serviceOrderStatus;
    public String responseTime;
    public String resolutionTime;
    public String creationDate;
    public String notificationDate;
    public String jobCompletionDate;
    public String acknowledgementDate;

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
