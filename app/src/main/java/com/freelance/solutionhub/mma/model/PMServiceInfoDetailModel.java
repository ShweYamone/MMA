package com.freelance.solutionhub.mma.model;

import java.io.Serializable;

public class PMServiceInfoDetailModel implements Serializable {
    public String id;
    public String serviceOrderType;
    public String serviceOrderStatus;
    public String priorityLevel;
    public String panelId;
    public String busStopId;
    public String busStopLocation;
    public String weatherCondition;
    public String thirdPartyFault;
    public String thirdPartyFaultDescription;
    public String reportedProblem;
    public String actualProblem;
    public String cause;
    public String remedy;
    public double totalResponseTime;
    public double totalDownTime;
    public String faultDetectedDate;
    public String acknowledgedBy;
    public String acknowledgementDate;
    public String firstResponseDate;
    public String creationDate;
    public String notificationDate;
    public String jobCompletionDate;
    public String targetResponseDate;
    public String targetEndDate;
    public boolean isPartReplacement;
    public PartReplacement partReplacement;

    public class PartReplacement implements Serializable{
        public int id;
        public String replacedDate;
        public String faultPartCode;
        public String replacementPartCode;
        public String replacedBy;
    }

    public PMServiceInfoDetailModel(String id, String serviceOrderType, String serviceOrderStatus, String priorityLevel, String panelId, String busStopId, String busStopLocation, String weatherCondition, String thirdPartyFault, String thirdPartyFaultDescription, String reportedProblem, String actualProblem, String cause, String remedy, double totalResponseTime, double totalDownTime, String faultDetectedDate, String acknowledgedBy, String acknowledgementDate, String firstResponseDate, String creationDate, String notificationDate, String jobCompletionDate, String targetResponseDate, String targetEndDate, boolean isPartReplacement, PartReplacement partReplacement) {
        this.id = id;
        this.serviceOrderType = serviceOrderType;
        this.serviceOrderStatus = serviceOrderStatus;
        this.priorityLevel = priorityLevel;
        this.panelId = panelId;
        this.busStopId = busStopId;
        this.busStopLocation = busStopLocation;
        this.weatherCondition = weatherCondition;
        this.thirdPartyFault = thirdPartyFault;
        this.thirdPartyFaultDescription = thirdPartyFaultDescription;
        this.reportedProblem = reportedProblem;
        this.actualProblem = actualProblem;
        this.cause = cause;
        this.remedy = remedy;
        this.totalResponseTime = totalResponseTime;
        this.totalDownTime = totalDownTime;
        this.faultDetectedDate = faultDetectedDate;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgementDate = acknowledgementDate;
        this.firstResponseDate = firstResponseDate;
        this.creationDate = creationDate;
        this.notificationDate = notificationDate;
        this.jobCompletionDate = jobCompletionDate;
        this.targetResponseDate = targetResponseDate;
        this.targetEndDate = targetEndDate;
        this.isPartReplacement = isPartReplacement;
        this.partReplacement = partReplacement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceOrderType() {
        return serviceOrderType;
    }

    public void setServiceOrderType(String serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
    }

    public String getServiceOrderStatus() {
        return serviceOrderStatus;
    }

    public void setServiceOrderStatus(String serviceOrderStatus) {
        this.serviceOrderStatus = serviceOrderStatus;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
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

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getThirdPartyFault() {
        return thirdPartyFault;
    }

    public void setThirdPartyFault(String thirdPartyFault) {
        this.thirdPartyFault = thirdPartyFault;
    }

    public String getThirdPartyFaultDescription() {
        return thirdPartyFaultDescription;
    }

    public void setThirdPartyFaultDescription(String thirdPartyFaultDescription) {
        this.thirdPartyFaultDescription = thirdPartyFaultDescription;
    }

    public String getReportedProblem() {
        return reportedProblem;
    }

    public void setReportedProblem(String reportedProblem) {
        this.reportedProblem = reportedProblem;
    }

    public String getActualProblem() {
        return actualProblem;
    }

    public void setActualProblem(String actualProblem) {
        this.actualProblem = actualProblem;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getRemedy() {
        return remedy;
    }

    public void setRemedy(String remedy) {
        this.remedy = remedy;
    }

    public double getTotalResponseTime() {
        return totalResponseTime;
    }

    public void setTotalResponseTime(double totalResponseTime) {
        this.totalResponseTime = totalResponseTime;
    }

    public double getTotalDownTime() {
        return totalDownTime;
    }

    public void setTotalDownTime(double totalDownTime) {
        this.totalDownTime = totalDownTime;
    }

    public String getFaultDetectedDate() {
        return faultDetectedDate;
    }

    public void setFaultDetectedDate(String faultDetectedDate) {
        this.faultDetectedDate = faultDetectedDate;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public String getAcknowledgementDate() {
        return acknowledgementDate;
    }

    public void setAcknowledgementDate(String acknowledgementDate) {
        this.acknowledgementDate = acknowledgementDate;
    }

    public String getFirstResponseDate() {
        return firstResponseDate;
    }

    public void setFirstResponseDate(String firstResponseDate) {
        this.firstResponseDate = firstResponseDate;
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

    public String getTargetResponseDate() {
        return targetResponseDate;
    }

    public void setTargetResponseDate(String targetResponseDate) {
        this.targetResponseDate = targetResponseDate;
    }

    public String getTargetEndDate() {
        return targetEndDate;
    }

    public void setTargetEndDate(String targetEndDate) {
        this.targetEndDate = targetEndDate;
    }

    public boolean isPartReplacement() {
        return isPartReplacement;
    }

    public void setPartReplacement(boolean partReplacement) {
        isPartReplacement = partReplacement;
    }

    public PartReplacement getPartReplacement() {
        return partReplacement;
    }

    public void setPartReplacement(PartReplacement partReplacement) {
        this.partReplacement = partReplacement;
    }
}
