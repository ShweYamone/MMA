package com.digisoft.mma.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.digisoft.mma.DAO.CheckListDescDAO;
import com.digisoft.mma.DAO.EventDAO;
import com.digisoft.mma.DAO.FaultMappingDAO;
import com.digisoft.mma.DAO.PhotoFilePathDAO;
import com.digisoft.mma.DAO.UpdateEventBodyDAO;
import com.digisoft.mma.DAO.UploadPhotoDAO;
import com.digisoft.mma.model.CheckListDescModel;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.FaultMappingJSONString;
import com.digisoft.mma.model.PhotoFilePathModel;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.model.UploadPhotoModel;

@Database(entities = {PhotoFilePathModel.class, CheckListDescModel.class, UpdateEventBody.class, Event.class, UploadPhotoModel.class, FaultMappingJSONString.class} , version = 1,exportSchema = false)
public abstract class InitializeDatabase extends RoomDatabase {
    private static InitializeDatabase INSTANCE;
    public abstract EventDAO eventDAO();
    public abstract UpdateEventBodyDAO updateEventBodyDAO();
    public abstract UploadPhotoDAO uploadPhotoDAO();
    public abstract FaultMappingDAO faultMappingDAO();
    public abstract CheckListDescDAO checkListDescDAO();
    public abstract PhotoFilePathDAO photoFilePathDAO();

    public static InitializeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static InitializeDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context,
                InitializeDatabase.class,
                "mma-db")
                .allowMainThreadQueries()
                .build();
    }
}
