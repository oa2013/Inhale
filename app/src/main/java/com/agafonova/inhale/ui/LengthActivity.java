package com.agafonova.inhale.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.agafonova.inhale.R;
import com.agafonova.inhale.adapters.TimerDataAdapter;
import com.agafonova.inhale.model.TimerData;
import com.agafonova.inhale.viewmodel.TimerDataViewModel;
import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class LengthActivity extends AppCompatActivity implements TimerDataAdapter.ExerciseClickListener {

    private static final String TAG = LengthActivity.class.getName();
    private static final String LAST_ID = "LAST_ID";
    private static final String APP_NAME = "Inhale";

    private TimerDataAdapter mAdapter;
    private ArrayList<TimerData> mExerciseData;
    private TimerDataViewModel mTimerDataViewModel;

    @BindView(R.id.rv_length)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length);

        //Bind ButterKnife and start Crashlytics
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());

        //Initialize Firebase Crashlytics debugger
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        GridLayoutManager layoutManager = new GridLayoutManager(LengthActivity.this, 1, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        loadExerciseData();
    }

    public void loadExerciseData() {

        try{
            mAdapter = new TimerDataAdapter(this,this);
            mRecyclerView.setAdapter(mAdapter);

            TimerData itemOne = new TimerData();
            itemOne.setExhale(getString(R.string.exhale_4));
            itemOne.setInhale(getString(R.string.inhale_2));

            TimerData itemTwo = new TimerData();
            itemTwo.setExhale(getString(R.string.exhale_6));
            itemTwo.setInhale(getString(R.string.inhale_3));

            TimerData itemThree = new TimerData();
            itemThree.setExhale(getString(R.string.exhale_8));
            itemThree.setInhale(getString(R.string.inhale_4));

            TimerData itemFour = new TimerData();
            itemFour.setExhale(getString(R.string.exhale_10));
            itemFour.setInhale(getString(R.string.inhale_5));

            TimerData itemFive = new TimerData();
            itemFive.setExhale(getString(R.string.exhale_12));
            itemFive.setInhale(getString(R.string.inhale_6));

            mExerciseData = new ArrayList<TimerData>();

            mExerciseData.add(itemOne);
            mExerciseData.add(itemTwo);
            mExerciseData.add(itemThree);
            mExerciseData.add(itemFour);
            mExerciseData.add(itemFive);

            mAdapter.setData(mExerciseData);
            mAdapter.notifyDataSetChanged();
        }
        catch(Exception e) {
            Crashlytics.log(Log.VERBOSE, TAG, e.toString());
        }
    }

    /*
    * Save selected exercise data to Room DB
    * */
    @Override
    public void onExerciseClick(TimerData selectedTimerData) throws ExecutionException, InterruptedException {
        mTimerDataViewModel = ViewModelProviders.of(this).get(TimerDataViewModel.class);

        long id = mTimerDataViewModel.insert(selectedTimerData);

        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_ID, Long.toString(id));
        editor.commit();

        Log.d("HELLO", "onExerciseClick: " + id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("exerciseData", mExerciseData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mExerciseData = savedInstanceState.getParcelableArrayList("exerciseData");
    }
}
