package com.freelance.solutionhub.mma.model;

public class ReturnStatus {
    public DataForPhoto data;
    public String status;

    public ReturnStatus(DataForPhoto data, String status) {
        this.data = data;
        this.status = status;
    }

    public DataForPhoto getData() {
        return data;
    }

    public void setData(DataForPhoto data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
