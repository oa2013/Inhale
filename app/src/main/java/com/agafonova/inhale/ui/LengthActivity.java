package com.agafonova.inhale.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.agafonova.inhale.R;
import com.agafonova.inhale.adapters.TimerDataAdapter;
import com.agafonova.inhale.model.TimerData;
import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class LengthActivity extends AppCompatActivity implements TimerDataAdapter.ExerciseClickListener {

    private static final String TAG = LengthActivity.class.getName();
    private TimerDataAdapter mAdapter;
    private List<TimerData> mExerciseData;

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

        loadExerciseData();
    }

    public void loadExerciseData() {

        try{
            GridLayoutManager layoutManager = new GridLayoutManager(LengthActivity.this, 1, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new TimerDataAdapter(this,this);
            mRecyclerView.setAdapter(mAdapter);

            TimerData itemOne = new TimerData();
            itemOne.setmExhale(getString(R.string.exhale_4));
            itemOne.setmInhale(getString(R.string.inhale_2));

            TimerData itemTwo = new TimerData();
            itemTwo.setmExhale(getString(R.string.exhale_6));
            itemTwo.setmInhale(getString(R.string.inhale_3));

            TimerData itemThree = new TimerData();
            itemThree.setmExhale(getString(R.string.exhale_8));
            itemThree.setmInhale(getString(R.string.inhale_4));

            TimerData itemFour = new TimerData();
            itemFour.setmExhale(getString(R.string.exhale_10));
            itemFour.setmInhale(getString(R.string.inhale_5));

            TimerData itemFive = new TimerData();
            itemFive.setmExhale(getString(R.string.exhale_12));
            itemFive.setmInhale(getString(R.string.inhale_6));

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

    @Override
    public void onExerciseClick(TimerData selectedTimerData) {

        //

    }

    @Override
    protected void onResume() {
        super.onResume();

        try{
            loadExerciseData();
        }
        catch(Exception e) {
            Crashlytics.log(Log.VERBOSE, TAG, e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try{
        }
        catch(Exception e) {
            Crashlytics.log(Log.VERBOSE, TAG, e.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try{
        }
        catch(Exception e) {
            Crashlytics.log(Log.VERBOSE, TAG, e.toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try{
            loadExerciseData();
        }
        catch(Exception e) {
            Crashlytics.log(Log.VERBOSE, TAG, e.toString());
        }
    }


}
