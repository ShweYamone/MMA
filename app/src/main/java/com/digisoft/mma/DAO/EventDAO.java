package com.digisoft.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.digisoft.mma.model.Event;

import java.util.List;

@Dao
public interface EventDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Event> eventList);

    @Query("Update Event Set alreadyUploaded=:uploaded where updateEventBodyKey=:updateEventBodyKey")
    void update(String uploaded, String updateEventBodyKey);

    @Query("Update Event Set alreadyUploaded=:uploaded where updateEventBodyKey=:updateEventBodyKey AND eventType=:eventType")
    void updateByThirdParty(String uploaded, String updateEventBodyKey, String eventType);

    @Query("Update Event Set alreadyUploaded=:uploaded where updateEventBodyKey=:updateEventBodyKey AND eventKey=:eventKey")
    void updateByUpdateEventBodyAndKey(String uploaded, String updateEventBodyKey, String eventKey);

    @Query("SELECT count(*) from Event where eventType=:eventType AND alreadyUploaded='no'")
    int getNumOfEventsToUploadByEventType(String eventType);

    @Query("SELECT count(*) from Event where eventType=:eventType")
    int getNumOfEventsByEventType(String eventType);

    @Query("Select * from Event where eventType=:eventType AND alreadyUploaded='no'")
    List<Event> getEventsToUploadByEventType(String eventType);

    @Query("Select * from Event where eventType=:eventType")
    List<Event> getEventsByEventType(String eventType);

    @Query("Select * from Event where updateEventBodyKey=:updateEvent")
    List<Event> getEventsByUpdateEvent(String updateEvent);

    @Query("Select value from Event where eventType=:eventType AND eventKey=:key")
    String getEventValue(String eventType, String key);

    @Query("Select count(*) from Event where eventType=:eventType AND eventKey=:eventKey")
    int getEventValueCount(String eventType, String eventKey);

    @Query("Select count(*) from Event")
    int getNumberOfEvents();

    @Query("Select count(*) from Event where updateEventBodyKey=:updateEventBodyKey")
    int getNumberOfEventsByUpdateBodyKey(String updateEventBodyKey);

    @Query("Select * from Event where updateEventBodyKey=:key AND alreadyUploaded='no'")
    List<Event> getEventsToUpload(String key);

    @Query("Select count(*) from Event where updateEventBodyKey=:key AND alreadyUploaded='no'")
    int getNumberEventsToUpload(String key);

    @Query("Select distinct eventKey from Event where updateEventBodyKey=:updateEventBody AND eventType!=:except")
    List<String> getUniqueKeyFromEvents(String updateEventBody, String except);

    @Query("Delete from Event where updateEventBodyKey=:key")
    void deleteByUpdateEventBodyKey(int key);

    @Query("Delete from Event")
    void deleteAll();
}
