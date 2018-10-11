package com.agafonova.inhale.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import com.agafonova.inhale.db.ExerciseRepository;
import com.agafonova.inhale.model.TimerData;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Olga Agafonova on 10/11/18.
 */

public class TimerDataViewModel extends AndroidViewModel{

    private ExerciseRepository mRepository;
    private LiveData<List<TimerData>> mExerciseData;

    public TimerDataViewModel(Application application) {
        super(application);
        mRepository = new ExerciseRepository(application);
        mExerciseData = mRepository.getExerciseData();
    }

    public LiveData<List<TimerData>> getExerciseData() {
        return mExerciseData;
    }

    public long insert(TimerData item) throws ExecutionException, InterruptedException {
        long id = mRepository.insert(item);
        return id;
    }
}
