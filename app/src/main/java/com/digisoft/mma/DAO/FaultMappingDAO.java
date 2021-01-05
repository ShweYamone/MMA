package com.digisoft.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.digisoft.mma.model.FaultMappingJSONString;

import java.util.List;

@Dao
public interface FaultMappingDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FaultMappingJSONString jsonString);

    @Query("SELECT * FROM FaultMappingJSONString")
    List<FaultMappingJSONString> getFaultMappingJSON();

    @Query("DELETE FROM FaultMappingJSONString")
    void delete();
}
