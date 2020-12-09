package com.freelance.solutionhub.mma.model;

public class PhotoModel {
    String image;
    int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public PhotoModel(String image, int uid) {
        this.image = image;
        this.uid = uid;
    }
}
