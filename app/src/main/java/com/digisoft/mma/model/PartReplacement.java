package com.digisoft.mma.model;

import java.io.Serializable;

public class PartReplacement implements Serializable {
    public int id;
    public String replacedDate;
    public String faultPartCode;
    public String replacementPartCode;
    public String replacedBy;

    public PartReplacement(int id, String replacedDate, String faultPartCode, String replacementPartCode, String replacedBy) {
        this.id = id;
        this.replacedDate = replacedDate;
        this.faultPartCode = faultPartCode;
        this.replacementPartCode = replacementPartCode;
        this.replacedBy = replacedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReplacedDate() {
        return replacedDate;
    }

    public void setReplacedDate(String replacedDate) {
        this.replacedDate = replacedDate;
    }

    public String getFaultPartCode() {
        return faultPartCode;
    }

    public void setFaultPartCode(String faultPartCode) {
        this.faultPartCode = faultPartCode;
    }

    public String getReplacementPartCode() {
        return replacementPartCode;
    }

    public void setReplacementPartCode(String replacementPartCode) {
        this.replacementPartCode = replacementPartCode;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }
}
