package com.freelance.solutionhub.mma.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.freelance.solutionhub.mma.DAO.EventDAO;
import com.freelance.solutionhub.mma.DAO.UpdateEventBodyDAO;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.UpdateEventBody;

@Database(entities = {UpdateEventBody.class, Event.class} , version = 1,exportSchema = false)
public abstract class InitializeDatabase extends RoomDatabase {
    private static InitializeDatabase INSTANCE;
    public abstract EventDAO eventDAO();
    public abstract UpdateEventBodyDAO updateEventBodyDAO();

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
