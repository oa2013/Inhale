package com.agafonova.inhale.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.agafonova.inhale.model.TimerData;
import java.util.List;

/*
 * Created by Olga Agafonova on 10/11/18.
 */

@Dao
public interface ExerciseDao {

    /* Get the id of the newly inserted item */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TimerData item);

    @Query("SELECT * FROM MASTER_TABLE")
    LiveData<List<TimerData>> getExerciseData();

}
