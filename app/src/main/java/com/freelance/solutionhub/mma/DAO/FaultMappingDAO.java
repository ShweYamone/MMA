package com.freelance.solutionhub.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.freelance.solutionhub.mma.model.FaultMappingJSONString;

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
