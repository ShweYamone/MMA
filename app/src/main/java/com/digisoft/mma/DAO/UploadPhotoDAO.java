package com.digisoft.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.digisoft.mma.model.UploadPhotoModel;

import java.util.List;

@Dao
public interface UploadPhotoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UploadPhotoModel uploadPhotoModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UploadPhotoModel> uploadPhotoModels);

    @Query("Delete from UploadPhotoModel where updateEventBodyKey=:updateEventBodyKey")
    void deleteById(String updateEventBodyKey);

    @Query("Delete from UploadPhotoModel")
    void deleteAll();

    @Query("Select * from UploadPhotoModel where bucketName=:bucketName")
    List<UploadPhotoModel> getPhotosToUploadByBucketName(String bucketName);

    @Query("Select count(*) from UploadPhotoModel")
    int getNumberOfPhotosToUpload();

    @Query("Select count(*) from UploadPhotoModel where updateEventBodyKey=:step")
    int getNumberOfPhotosByStep(String step);

}
