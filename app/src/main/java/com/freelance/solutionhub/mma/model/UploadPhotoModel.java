package com.freelance.solutionhub.mma.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "UploadPhotoModel")
public class UploadPhotoModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "bucketName")
    String bucketName;

    @ColumnInfo(name = "encodedPhotoString")
    String encodedPhotoString;

    @ColumnInfo(name = "dateForPhotoName")
    String dateForPhoto;

    public UploadPhotoModel(String bucketName, String encodedPhotoString,String dateForPhoto) {
        this.bucketName = bucketName;
        this.encodedPhotoString = encodedPhotoString;
        this.dateForPhoto = dateForPhoto;
    }

    public String getDateForPhoto() {
        return dateForPhoto;
    }

    public void setDateForPhoto(String dateForPhoto) {
        this.dateForPhoto = dateForPhoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
