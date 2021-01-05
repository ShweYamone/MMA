package com.digisoft.mma.model;

/*********For user profile **********/
public class UserProfile{
    public Data data;
    public String status;

    public UserProfile(Data data, String status) {
        this.data = data;
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

