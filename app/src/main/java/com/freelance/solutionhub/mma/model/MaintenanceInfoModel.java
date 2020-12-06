package com.freelance.solutionhub.mma.model;

public class MaintenanceInfoModel {

    String msoNumber;
    String status;
    String panelHealthStatus;
    String location;
    String datetime;

    public MaintenanceInfoModel(String msoNumber, String status, String panelHealthStatus, String location, String datetime) {
        this.msoNumber = msoNumber;
        this.status = status;
        this.panelHealthStatus = panelHealthStatus;
        this.location = location;
        this.datetime = datetime;
    }

    public String getMsoNumber() {
        return msoNumber;
    }

    public void setMsoNumber(String msoNumber) {
        this.msoNumber = msoNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPanelHealthStatus() {
        return panelHealthStatus;
    }

    public void setPanelHealthStatus(String panelHealthStatus) {
        this.panelHealthStatus = panelHealthStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
