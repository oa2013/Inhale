package com.agafonova.inhale;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.agafonova.inhale.model.TimerData;
import com.agafonova.inhale.ui.LengthActivity;
import com.crashlytics.android.Crashlytics;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

public class MainActivity extends AppCompatActivity implements LinearTimer.TimerListener  {

    private static final String TAG = MainActivity.class.getName();
    private static final int ENTER_FADE_DURATION = 100;
    private static final int EXIT_FADE_DURATION = 3000;
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String TIMER_DATA_KEY = "TIMER_DATA";

    private static final String START = "START";
    private static final String STOP = "STOP";

    private AnimationDrawable animationDrawable;
    private long mTotalSeconds = 30;
    private int mTimerCounter;
    private LinearTimer mTimer;
    private TimerData mTimerData;

    @BindView(R.id.time)
    TextView mTime;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.button_length)
    Button mButtonLength;

    @BindView(R.id.button_stopStart)
    Button mButtonStopStart;

    @BindView(R.id.tv_exercise_mode)
    TextView mExerciseMode;

    @BindView(R.id.tv_breathing_instructions)
    TextView mBreathingInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind ButterKnife and start Crashlytics
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());

        //Initialize Firebase Crashlytics debugger
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        setSupportActionBar(mToolbar);

        //Animate background gradients
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_rl);
        animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(ENTER_FADE_DURATION);
        animationDrawable.setExitFadeDuration(EXIT_FADE_DURATION);

        //Set start/stop button text
        mButtonStopStart.setText(START);

        //Setup the timer
        LinearTimerView linearTimerView = findViewById(R.id.linearTimer);
        mTotalSeconds = mTotalSeconds*1000;
        mTimerCounter = 0;

        mTimer = new LinearTimer.Builder()
                .linearTimerView(linearTimerView)
                .duration(mTotalSeconds)
                .timerListener(this)
                .getCountUpdate(LinearTimer.COUNT_UP_TIMER, 1000)
                .build();

        if(savedInstanceState == null)
        {
            mTimerData = new TimerData();
            mTimerData.setmTime("00:00:00");
        }
        else {
            mTimerData = savedInstanceState.getParcelable(TIMER_DATA_KEY);
            mTime.setText(mTimerData.getmTime());
        }

        //Setup start/stop button listener
        mButtonStopStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        //Setup the length button listener
        mButtonLength.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LengthActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startTimer() {

        try
        {
            //start the timer with the first click
            if(mTimerCounter == 0) {
                mTimer.startTimer();
                mTimerCounter++;
                mButtonStopStart.setText(STOP);
            }
            //restart on even number of clicks
            else if ((mTimerCounter % 2) == 0)
            {
                mTimer.restartTimer();
                mTime.setText(R.string.placeholder);
                mTimerCounter++;
                mButtonStopStart.setText(STOP);
            }
            //pause the timer on odd number of clicks (when the timer stops)
            else {
                mTimer.pauseTimer();
                mTimerCounter++;
                mButtonStopStart.setText(START);
            }
        }
        catch (Exception e) {
            Crashlytics.log(Log.VERBOSE, TAG, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override
    public void animationComplete() {

    }

    @Override
    public void timerTick(long tickUpdateInMillis) {

        if(tickUpdateInMillis >= mTotalSeconds) {
            mTimerCounter = 2;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(tickUpdateInMillis);

        mTime.setText(dateFormat.format(date));
        mTimerData.setmTime(dateFormat.format(date));
    }

    @Override
    public void onTimerReset() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(TIMER_DATA_KEY, mTimerData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTimerData = savedInstanceState.getParcelable(TIMER_DATA_KEY);
    }

}