package com.digisoft.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.digisoft.mma.model.UpdateEventBody;

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

    @Query("Select * from UpdateEventBody where id=:id")
    UpdateEventBody getUpdateEventBodyByID(String id);

    @Query("Select count(*) from UpdateEventBody")
    int getNumberOfUpdateEventBody();

    @Query("Select count(*) from UpdateEventBody where id=:id")
    int getNumberOfUpdateEventsById(String id);

    @Query("Update UpdateEventBody Set date=:date where id=:id")
    void updateDateTime(String date, String id);
}
