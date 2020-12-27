package com.freelance.solutionhub.mma.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.freelance.solutionhub.mma.DAO.CheckListDescDAO;
import com.freelance.solutionhub.mma.DAO.EventDAO;
import com.freelance.solutionhub.mma.DAO.FaultMappingDAO;
import com.freelance.solutionhub.mma.DAO.UpdateEventBodyDAO;
import com.freelance.solutionhub.mma.DAO.UploadPhotoDAO;
import com.freelance.solutionhub.mma.model.CheckListDescModel;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.FaultMappingJSONString;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.UploadPhotoModel;

@Database(entities = {CheckListDescModel.class, UpdateEventBody.class, Event.class, UploadPhotoModel.class, FaultMappingJSONString.class} , version = 1,exportSchema = false)
public abstract class InitializeDatabase extends RoomDatabase {
    private static InitializeDatabase INSTANCE;
    public abstract EventDAO eventDAO();
    public abstract UpdateEventBodyDAO updateEventBodyDAO();
    public abstract UploadPhotoDAO uploadPhotoDAO();
    public abstract FaultMappingDAO faultMappingDAO();
    public abstract CheckListDescDAO checkListDescDAO();

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
                "my-database")
                .allowMainThreadQueries()
                .build();
    }
}
