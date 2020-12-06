package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("username")
    String username;

    @SerializedName("password")
    String password;

    public UserModel(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
