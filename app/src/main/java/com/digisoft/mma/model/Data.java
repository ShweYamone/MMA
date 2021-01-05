package com.digisoft.mma.model;

import java.util.List;

/*********For user profile **********/
public class Data{
    public String userId;
    public String displayName;
    public String email;
    public String phoneNumber;
    public String handphoneNumber;
    public String createdDate;
    public String updatedDate;
    public String passwordUpdatedDate;
    public String currentLoginDate;
    public String lastLoginDate;
    public String lastFailLoginDate;
    public Object suspendStartDate;
    public Object suspendEndDate;
    public String status;
    public String remark;
    public String company;
    public String department;
    public Object expiryDate;
    public boolean firstTimeLogin;
    public List<Role> roles;

    public Data(String userId, String displayName, String email, String phoneNumber, String handphoneNumber, String createdDate, String updatedDate, String passwordUpdatedDate, String currentLoginDate, String lastLoginDate, String lastFailLoginDate, Object suspendStartDate, Object suspendEndDate, String status, String remark, String company, String department, Object expiryDate, boolean firstTimeLogin, List<Role> roles) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.handphoneNumber = handphoneNumber;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.passwordUpdatedDate = passwordUpdatedDate;
        this.currentLoginDate = currentLoginDate;
        this.lastLoginDate = lastLoginDate;
        this.lastFailLoginDate = lastFailLoginDate;
        this.suspendStartDate = suspendStartDate;
        this.suspendEndDate = suspendEndDate;
        this.status = status;
        this.remark = remark;
        this.company = company;
        this.department = department;
        this.expiryDate = expiryDate;
        this.firstTimeLogin = firstTimeLogin;
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
