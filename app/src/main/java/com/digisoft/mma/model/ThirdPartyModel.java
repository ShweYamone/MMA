package com.digisoft.mma.model;

import java.io.Serializable;

public class ThirdPartyModel implements Serializable {
    public int id;
    public String thirdPartyNumber;
    public String docketNumber;
    public String thirdPartyType;
    public String companyName;
    public String contactNumber;
    public String ctPersonnel;
    public String officer;
    public String faultStatus;
    public String remarkOnFault;
    public String actionDate;
    public String actionTaken;
    public String faultDetectedDate;
    public String expectedCompletionDate;
    public String referDate;
    public String clearanceDate;

    public ThirdPartyModel() {
        this.thirdPartyType = "NO_TYPE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThirdPartyNumber() {
        return thirdPartyNumber;
    }

    public void setThirdPartyNumber(String thirdPartyNumber) {
        this.thirdPartyNumber = thirdPartyNumber;
    }

    public String getDocketNumber() {
        return docketNumber;
    }

    public void setDocketNumber(String docketNumber) {
        this.docketNumber = docketNumber;
    }

    public String getThirdPartyType() {
        return thirdPartyType;
    }

    public void setThirdPartyType(String thirdPartyType) {
        this.thirdPartyType = thirdPartyType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCtPersonnel() {
        return ctPersonnel;
    }

    public void setCtPersonnel(String ctPersonnel) {
        this.ctPersonnel = ctPersonnel;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getFaultStatus() {
        return faultStatus;
    }

    public void setFaultStatus(String faultStatus) {
        this.faultStatus = faultStatus;
    }

    public String getRemarkOnFault() {
        return remarkOnFault;
    }

    public void setRemarkOnFault(String remarkOnFault) {
        this.remarkOnFault = remarkOnFault;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public String getFaultDetectedDate() {
        return faultDetectedDate;
    }

    public void setFaultDetectedDate(String faultDetectedDate) {
        this.faultDetectedDate = faultDetectedDate;
    }

    public String getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(String expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public String getReferDate() {
        return referDate;
    }

    public void setReferDate(String referDate) {
        this.referDate = referDate;
    }

    public String getClearanceDate() {
        return clearanceDate;
    }

    public void setClearanceDate(String clearanceDate) {
        this.clearanceDate = clearanceDate;
    }
}
