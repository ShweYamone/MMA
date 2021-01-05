package com.digisoft.mma.model;

public class CheckListModel implements Cloneable{
    public int id;
    public String checkDescription;
    public String maintenanceRemark;
    public boolean isMaintenanceDone;

    public CheckListModel(CheckListModel obj) {
        this.id = obj.getId();
        this.checkDescription = obj.getCheckDescription();
        this.maintenanceRemark = obj.getMaintenanceRemark();
        this.isMaintenanceDone = obj.isMaintenanceDone();
    }


    public CheckListModel(int id, String checkDescription, String maintenanceRemark, boolean isMaintenanceDone) {
        this.id = id;
        this.checkDescription = checkDescription;
        this.maintenanceRemark = maintenanceRemark;
        this.isMaintenanceDone = isMaintenanceDone;
    }

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

    public String getMaintenanceRemark() {
        return maintenanceRemark;
    }

    public void setMaintenanceRemark(String maintenanceRemark) {
        this.maintenanceRemark = maintenanceRemark;
    }

    public boolean isMaintenanceDone() {
        return isMaintenanceDone;
    }

    public void setMaintenanceDone(boolean maintenanceDone) {
        isMaintenanceDone = maintenanceDone;
    }
}
