package com.freelance.solutionhub.mma.model;

public class CheckListModel {
    public int id;
    public String checkDescription;
    public Object maintenanceRemark;
    public Object isMaintenanceDone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheckDescription() {
        return checkDescription;
    }

    public void setCheckDescription(String checkDescription) {
        this.checkDescription = checkDescription;
    }

    public Object getMaintenanceRemark() {
        return maintenanceRemark;
    }

    public void setMaintenanceRemark(Object maintenanceRemark) {
        this.maintenanceRemark = maintenanceRemark;
    }

    public Object getIsMaintenanceDone() {
        return isMaintenanceDone;
    }

    public void setIsMaintenanceDone(Object isMaintenanceDone) {
        this.isMaintenanceDone = isMaintenanceDone;
    }
}
