package com.digisoft.mma.model;

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
    public String reportedProblemDescription;
    public String actualProblem;
    public String actualProblemDescription;
    public String cause;
    public String causeDescription;
    public String remedy;
    public String remedyDescription;
    public String targetResponseDate;
    public String targetEndDate;
    public double totalResponseTime;
    public double totalDownTime;
    public String responseTime;
    public String resolutionTime;
    public String acknowledgementDate;
    public String acknowledgedBy;
    public String firstResponseDate;
    public String creationDate;
    public String jobCompletionDate;
    public String faultDetectedDate;
    public String notificationDate;
    public boolean isPartReplacement;
    public PartReplacement partReplacement;
    public String preventativeMaintenanceServiceTechnician;
    public String parentPreventativeServiceOrderName;
    public String preventativeMaintenanceCheckType;

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

    public String getReportedProblemDescription() {
        return reportedProblemDescription;
    }

    public void setReportedProblemDescription(String reportedProblemDescription) {
        this.reportedProblemDescription = reportedProblemDescription;
    }

    public String getActualProblem() {
        return actualProblem;
    }

    public void setActualProblem(String actualProblem) {
        this.actualProblem = actualProblem;
    }

    public String getActualProblemDescription() {
        return actualProblemDescription;
    }

    public void setActualProblemDescription(String actualProblemDescription) {
        this.actualProblemDescription = actualProblemDescription;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getCauseDescription() {
        return causeDescription;
    }

    public void setCauseDescription(String causeDescription) {
        this.causeDescription = causeDescription;
    }

    public String getRemedy() {
        return remedy;
    }

    public void setRemedy(String remedy) {
        this.remedy = remedy;
    }

    public String getRemedyDescription() {
        return remedyDescription;
    }

    public void setRemedyDescription(String remedyDescription) {
        this.remedyDescription = remedyDescription;
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

    public String getAcknowledgementDate() {
        return acknowledgementDate;
    }

    public void setAcknowledgementDate(String acknowledgementDate) {
        this.acknowledgementDate = acknowledgementDate;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
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

    public String getJobCompletionDate() {
        return jobCompletionDate;
    }

    public void setJobCompletionDate(String jobCompletionDate) {
        this.jobCompletionDate = jobCompletionDate;
    }

    public String getFaultDetectedDate() {
        return faultDetectedDate;
    }

    public void setFaultDetectedDate(String faultDetectedDate) {
        this.faultDetectedDate = faultDetectedDate;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
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

    public String getPreventativeMaintenanceServiceTechnician() {
        return preventativeMaintenanceServiceTechnician;
    }

    public void setPreventativeMaintenanceServiceTechnician(String preventativeMaintenanceServiceTechnician) {
        this.preventativeMaintenanceServiceTechnician = preventativeMaintenanceServiceTechnician;
    }

    public String getParentPreventativeServiceOrderName() {
        return parentPreventativeServiceOrderName;
    }

    public void setParentPreventativeServiceOrderName(String parentPreventativeServiceOrderName) {
        this.parentPreventativeServiceOrderName = parentPreventativeServiceOrderName;
    }

    public String getPreventativeMaintenanceCheckType() {
        return preventativeMaintenanceCheckType;
    }

    public void setPreventativeMaintenanceCheckType(String preventativeMaintenanceCheckType) {
        this.preventativeMaintenanceCheckType = preventativeMaintenanceCheckType;
    }
}

