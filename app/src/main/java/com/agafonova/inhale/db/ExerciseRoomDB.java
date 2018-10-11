package com.agafonova.inhale.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import com.agafonova.inhale.model.TimerData;

/**
 * Created by Olga Agafonova on 10/11/18.
 */

@Database(entities = {TimerData.class}, version = 1)
public abstract class ExerciseRoomDB extends RoomDatabase {

    private static ExerciseRoomDB INSTANCE;

    public abstract ExerciseDao exerciseDao();

    public static ExerciseRoomDB  getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ExerciseRoomDB.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ExerciseRoomDB.class, "exercise_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}