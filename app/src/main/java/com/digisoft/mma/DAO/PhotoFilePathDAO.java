package com.digisoft.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.digisoft.mma.model.PhotoFilePathModel;

import java.util.List;


@Dao
public interface PhotoFilePathDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PhotoFilePathModel object);

    @Query("Select * from PhotoFilePathModel")
    List<PhotoFilePathModel> getPhotoFilePaths();

    @Query("Delete from PhotoFilePathModel")
    void deleteAll();
}
