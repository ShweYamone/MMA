package com.freelance.solutionhub.mma.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.freelance.solutionhub.mma.model.Event;

import java.util.List;

@Dao
public interface EventDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Event> eventList);

    @Query("Update Event Set alreadyUploaded=:uploaded where updateEventBodyKey=:updateEventBodyKey")
    void update(String uploaded, String updateEventBodyKey);

    @Query("SELECT * from Event where updateEventBodyKey=:key")
    List<Event> getEvents(String key);

    @Query("Select count(*) from Event where event_id=:eventId")
    int getSpecifiedEventCount(String eventId);

    @Query("Select count(*) from Event")
    int getNumberOfEvents();

    @Query("Delete from Event where updateEventBodyKey=:key")
    void deleteByUpdateEventBodyKey(int key);

    @Query("Delete from Event")
    void deleteAll();
}
