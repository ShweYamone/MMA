package com.freelance.solutionhub.mma.model;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "ServiceInfoModel")
public class ServiceInfoModel implements Serializable{

    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String id;
    @ColumnInfo
    public String serviceOrderType;
    @ColumnInfo
    public String serviceOrderStatus;
    @ColumnInfo
    public String priorityLevel;
    @ColumnInfo
    public String panelId;
    @ColumnInfo
    public String busStopId;
    @ColumnInfo
    public String busStopLocation;
    @ColumnInfo
    public String weatherCondition;
    @ColumnInfo
    public String thirdPartyFault;
    @ColumnInfo
    public String thirdPartyFaultDescription;
    @ColumnInfo
    public String reportedProblem;
    @ColumnInfo
    public String reportedProblemDescription;
    @ColumnInfo
    public String actualProblem;
    @ColumnInfo
    public String actualProblemDescription;
    @ColumnInfo
    public String cause;
    @ColumnInfo
    public String causeDescription;
    @ColumnInfo
    public String remedy;
    @ColumnInfo
    public String remedyDescription;
    @ColumnInfo
    public String targetResponseDate;
    @ColumnInfo
    public String targetEndDate;
    @ColumnInfo
    public String totalResponseTime;
    @ColumnInfo
    public String totalDownTime;
    @ColumnInfo
    public String responseTime;
    @ColumnInfo
    public String resolutionTime;
    @ColumnInfo
    public String acknowledgementDate;
    @ColumnInfo
    public String acknowledgedBy;
    @ColumnInfo
    public String firstResponseDate;
    @ColumnInfo
    public String creationDate;
    @ColumnInfo
    public String jobCompletionDate;
    @ColumnInfo
    public String faultDetectedDate;
    @ColumnInfo
    public String notificationDate;
    @ColumnInfo
    public boolean isPartReplacement;
    @Ignore
    public Object partReplacement;

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

    public String getTotalResponseTime() {
        return totalResponseTime;
    }

    public void setTotalResponseTime(String totalResponseTime) {
        this.totalResponseTime = totalResponseTime;
    }

    public String getTotalDownTime() {
        return totalDownTime;
    }

    public void setTotalDownTime(String totalDownTime) {
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

    public Object getPartReplacement() {
        return partReplacement;
    }

    public void setPartReplacement(Object partReplacement) {
        this.partReplacement = partReplacement;
    }
}
