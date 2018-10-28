package com.agafonova.inhale;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.agafonova.inhale.ui.AboutActivity;
import com.agafonova.inhale.ui.LengthActivity;
import com.agafonova.inhale.viewmodel.TimerDataViewModel;
import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

public class MainActivity extends AppCompatActivity implements LinearTimer.TimerListener  {

    private static final String TAG = MainActivity.class.getName();
    private static final String LAST_ID = "LAST_ID";
    private static final String APP_NAME = "Inhale";
    private static final String EXERCISE_STRING = "EXERCISE_STRING";

    private static final int ENTER_FADE_DURATION = 100;
    private static final int EXIT_FADE_DURATION = 3000;
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String TIMER_DATA_KEY = "TIMER_DATA";

    private static final String PLAYER_POSITION = "PLAYER_POSITION";
    private static final String PLAYER_STATUS = "PLAYER_STATUS";

    private AnimationDrawable animationDrawable;
    private long mTotalSeconds = 30;
    private int mTimerCounter;
    private LinearTimer mTimer;
    private TimerData mTimerData;
    private TimerDataViewModel mTimerDataViewModel;
    private List<TimerData> mSavedExerciseData;
    private String mExerciseID;
    private ExoPlayer mPlayer;

    private boolean mPlayerStatus = true;
    private long mPlayerPosition = 0;

    private int mNumberExhales = 0;
    private int mNumberInhales = 0;
    private String mExerciseString = "No exercise mode set";

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

    @BindView(R.id.audio_view)
    PlayerView mPlayerView;

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
        mButtonStopStart.setText(R.string.button_start);

        //Get exercise ID
        mTimerDataViewModel = ViewModelProviders.of(this).get(TimerDataViewModel.class);
        setExerciseIDAndPassDataToAppWidget();

        //Get timer data
        mTimerDataViewModel.getExerciseData().observe(this, new Observer<List<TimerData>>() {
            @Override
            public void onChanged(@Nullable final List<TimerData> items) {

                for (TimerData item : items) {

                    //If the IDs match, then set the exercise mode
                    if (item.getId() == Integer.parseInt(mExerciseID)) {

                        mNumberExhales = extractInteger(item.getExhale());
                        mNumberInhales = extractInteger(item.getInhale());

                        //Save this data to pass it to the app widget
                        mTimerData.setExhale(item.getExhale());
                        mTimerData.setInhale(item.getInhale());

                        mTotalSeconds = (mNumberExhales + mNumberInhales)*1000;

                        mExerciseString = String.valueOf(mNumberExhales) + " : " + String.valueOf(mNumberInhales);
                        mExerciseMode.setText(mExerciseString);

                        //Setup timer
                        LinearTimerView linearTimerView = findViewById(R.id.linearTimer);
                        mTimerCounter = 0;

                        mTimer = new LinearTimer.Builder()
                                .linearTimerView(linearTimerView)
                                .duration(mTotalSeconds)
                                .timerListener(MainActivity.this)
                                .getCountUpdate(LinearTimer.COUNT_UP_TIMER, 1000)
                                .build();
                    }
                }

                mSavedExerciseData = new ArrayList<TimerData>();
                mSavedExerciseData.addAll(items);
            }
        });

        //Set default exercise mode and timer
        if(mExerciseID.isEmpty()) {
            mNumberInhales = extractInteger(getString(R.string.exhale_4));
            mNumberExhales = extractInteger(getString(R.string.inhale_2));

            //Save this data to pass it to the app widget
            mTimerData.setExhale(getString(R.string.exhale_4));
            mTimerData.setInhale(getString(R.string.inhale_2));

            mExerciseString = getString(R.string.exhale_4) + " : " + getString(R.string.inhale_2);
            mExerciseMode.setText(mExerciseString);

            mTotalSeconds = (mNumberExhales + mNumberInhales)*1000;

            LinearTimerView linearTimerView = findViewById(R.id.linearTimer);
            mTimerCounter = 0;

            mTimer = new LinearTimer.Builder()
                    .linearTimerView(linearTimerView)
                    .duration(mTotalSeconds)
                    .timerListener(MainActivity.this)
                    .getCountUpdate(LinearTimer.COUNT_UP_TIMER, 1000)
                    .build();
        }

        if(savedInstanceState == null)
        {
            mTimerData = new TimerData();
            mTimerData.setTime("00:00:00");
        }
        else {
            mTimerData = savedInstanceState.getParcelable(TIMER_DATA_KEY);
            mTime.setText(mTimerData.getTime());
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

        initializePlayer();
        updatePlayer();
    }

    private void initializePlayer() {
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        mPlayerView.setPlayer(mPlayer);
        mPlayerView.setUseArtwork(false);
        mPlayerView.setControllerShowTimeoutMs(0);
    }

    private void updatePlayer() {
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.snowburn));

        try {
            rawResourceDataSource.open(dataSpec);

            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return rawResourceDataSource;
                }
            };
            MediaSource audioSource = new ExtractorMediaSource.Factory(factory).createMediaSource(rawResourceDataSource.getUri());
            mPlayer.prepare(audioSource);
            mPlayer.setPlayWhenReady(mPlayerStatus);
            mPlayer.seekTo(mPlayerPosition);

        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }
    }

    private int extractInteger(String input) {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(input);
        int result = 0;

        while (m.find()) {
            result = Integer.parseInt(m.group());
        }

        return result;
    }

    private void setExerciseIDAndPassDataToAppWidget() {
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);

        mExerciseID = sharedPreferences.getString(LAST_ID,"");

        if(mExerciseString != null && mTimerData != null) {
            mExerciseString = mTimerData.getExhale() + " : " + mTimerData.getInhale();
            saveExerciseStringForAppWidget(mExerciseString);
        }
    }

    private void startTimer() {

        try
        {
            //start the timer with the first click
            if(mTimerCounter == 0) {
                mTimer.startTimer();
                mTimerCounter++;
                mButtonStopStart.setText(R.string.button_stop);
            }
            //restart on even number of clicks
            else if ((mTimerCounter % 2) == 0)
            {
                mTimer.restartTimer();
                mTime.setText(R.string.placeholder);
                mTimerCounter++;
                mButtonStopStart.setText(R.string.button_stop);
            }
            //pause the timer on odd number of clicks (when the timer stops)
            else {
                mTimer.pauseTimer();
                mTimerCounter++;
                mButtonStopStart.setText(R.string.button_start);
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
        if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mPlayer == null) {
            initializePlayer();
            updatePlayer();
        }
        else {
            updatePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();

        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
            updatePlayer();
        }
        else {
            updatePlayer();
        }

        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
        setExerciseIDAndPassDataToAppWidget();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
        else {
            updatePlayer();
        }

        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
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

        if(tickUpdateInMillis <= (mNumberExhales * 1000)) {
            mBreathingInstructions.setText(R.string.exhale);
        }
        else {
            mBreathingInstructions.setText(R.string.inhale);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(tickUpdateInMillis);

        mTime.setText(dateFormat.format(date));
        mTimerData.setTime(dateFormat.format(date));
    }

    @Override
    public void onTimerReset() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TIMER_DATA_KEY, mTimerData);
        outState.putLong(PLAYER_POSITION, mPlayer.getCurrentPosition());
        outState.putBoolean(PLAYER_STATUS, mPlayer.getPlayWhenReady());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION);
            mPlayerStatus = savedInstanceState.getBoolean(PLAYER_STATUS);
            mTimerData = savedInstanceState.getParcelable(TIMER_DATA_KEY);
            mExerciseString = savedInstanceState.getString(EXERCISE_STRING);
            setExerciseIDAndPassDataToAppWidget();
        }
    }

    public void saveExerciseStringForAppWidget(String exerciseString) {
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();
    }
}
