package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("access_token")
    String accessToken;

    @SerializedName("refresh_token")
    String refreshToken;

    @SerializedName("username")
    String username;

    public LoginModel(String accessToken, String refreshToken, String username){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
