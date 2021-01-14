package com.digisoft.mma.model;

public class PhotoModel {
    String image;
    String photoPath;
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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public PhotoModel(String image, int uid) {
        this.image = image;
        this.uid = uid;
    }

    public PhotoModel(String image, int uid, String photoPath){
        this.image = image;
        this.uid = uid;
        this.photoPath = photoPath;
    }
}
