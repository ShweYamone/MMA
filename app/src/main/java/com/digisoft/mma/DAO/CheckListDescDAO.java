package com.digisoft.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.digisoft.mma.model.CheckListDescModel;

@Dao
public interface CheckListDescDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CheckListDescModel obj);

    @Query("Select description from CheckListDescModel where keyId=:keyId")
    String getDesc(String keyId);

    @Query("Delete from CheckListDescModel")
    void deleteAll();
}
