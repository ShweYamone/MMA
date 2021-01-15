package com.digisoft.mma.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "UploadPhotoModel")
public class UploadPhotoModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "updateEventBodyKey")
    public String updateEventBodyKey;

    @ColumnInfo(name = "bucketName")
    String bucketName;

    @ColumnInfo(name = "encodedPhotoString")
    String encodedPhotoString;

    @ColumnInfo(name = "photoFilePath")
    public String photoFilePath;

    public UploadPhotoModel(String updateEventBodyKey, String bucketName, String encodedPhotoString, String photoFilePath) {
        this.updateEventBodyKey = updateEventBodyKey;
        this.bucketName = bucketName;
        this.encodedPhotoString = encodedPhotoString;
        this.photoFilePath = photoFilePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdateEventBodyKey() {
        return updateEventBodyKey;
    }

    public void setUpdateEventBodyKey(String updateEventBodyKey) {
        this.updateEventBodyKey = updateEventBodyKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEncodedPhotoString() {
        return encodedPhotoString;
    }

    public void setEncodedPhotoString(String encodedPhotoString) {
        this.encodedPhotoString = encodedPhotoString;
    }

    public String getPhotoFilePath() {
        return photoFilePath;
    }

    public void setPhotoFilePath(String photoFilePath) {
        this.photoFilePath = photoFilePath;
    }
}
