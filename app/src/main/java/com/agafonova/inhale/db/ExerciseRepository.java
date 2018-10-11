package com.agafonova.inhale.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import com.agafonova.inhale.model.TimerData;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Olga Agafonova on 10/11/18.
 */

public class ExerciseRepository {

    private ExerciseDao mExerciseDao;
    private LiveData<List<TimerData>> mExerciseData;

    public interface AsyncResponse {
        void getExerciseData(List<TimerData> items);
    }

    public ExerciseRepository(Application application) {
        ExerciseRoomDB db = ExerciseRoomDB.getDatabase(application);
        mExerciseDao = db.exerciseDao();
        mExerciseData = mExerciseDao.getExerciseData();
    }

    public LiveData<List<TimerData>> getExerciseData() {
        return mExerciseData;
    }

    public long insert(TimerData item) throws ExecutionException, InterruptedException {
        return new insertAsyncTask(mExerciseDao).execute(item).get();
    }

    private static class insertAsyncTask extends AsyncTask<TimerData, Void, Long> {

        private ExerciseDao mAsyncTaskDao;

        insertAsyncTask(ExerciseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(final TimerData... params) {
            long id = mAsyncTaskDao.insert(params[0]);
            return id;
        }
    }
}
