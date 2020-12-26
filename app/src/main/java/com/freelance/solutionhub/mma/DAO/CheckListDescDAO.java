package com.freelance.solutionhub.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.freelance.solutionhub.mma.model.CheckListDescModel;

@Dao
public interface CheckListDescDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CheckListDescModel obj);

    @Query("Select description from CheckListDescModel where keyId=:keyId")
    String getDesc(String keyId);

    @Query("Delete from CheckListDescModel")
    void deleteAll();
}
