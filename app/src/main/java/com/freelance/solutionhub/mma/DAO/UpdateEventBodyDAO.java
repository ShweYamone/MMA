package com.freelance.solutionhub.mma.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.freelance.solutionhub.mma.model.UpdateEventBody;

import java.util.List;

@Dao
public interface UpdateEventBodyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UpdateEventBody eventBody);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UpdateEventBody> eventBodyList);

    @Query("Delete from UpdateEventBody where id=:id")
    void deleteById(String id);

    @Query("Delete from UpdateEventBody")
    void deleteAll();

    @Query("Select * from UpdateEventBody")
    List<UpdateEventBody> getUpdateEventBodyList();

    @Query("Select count(*) from UpdateEventBody")
    int getNumberOfUpdateEventBody();

    @Query("Select count(*) from UpdateEventBody where id=:id")
    int getNumberOfUpdateEventsById(String id);

    @Query("Update UpdateEventBody Set date=:date where id=:id")
    void update(String date, String id);
}
