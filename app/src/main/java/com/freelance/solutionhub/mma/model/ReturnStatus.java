package com.freelance.solutionhub.mma.model;

public class ReturnStatus {
    public Object data;
    public String status;

    public ReturnStatus(Object data, String status) {
        this.data = data;
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
