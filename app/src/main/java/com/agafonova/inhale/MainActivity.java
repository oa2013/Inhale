package com.agafonova.inhale;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int ENTER_FADE_DURATION = 100;
    private static final int EXIT_FADE_DURATION = 3000;

    private static final String START = "START";
    private static final String STOP = "STOP";

    private AnimationDrawable animationDrawable;

    //The UI is 2 seconds too slow, so to show 60 seconds, set 62 seconds
    private long mTotalSeconds = 62;
    private CountDownTimer mTimer;
    private boolean mIsTimerRunning;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.circular_progress)
    CircularProgressIndicator mIndicator;

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
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_rl);
        animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(ENTER_FADE_DURATION);
        animationDrawable.setExitFadeDuration(EXIT_FADE_DURATION);

        //Set indicator max time
        mIndicator.setDirection(0);
        mIndicator.setMaxProgress(mTotalSeconds);
        mIndicator.setProgressTextAdapter(TIME_TEXT_ADAPTER);

        //Set start/stop button text
        mButtonStopStart.setText(START);

        //Setup start/stop button listener
        mButtonStopStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsTimerRunning) {
                    resetTimer();
                } else {
                    startTimer();
                }
            }
        });
    }

    private void resetTimer() {
        mTimer.cancel();
        mIsTimerRunning = false;
    }

    /*
    * Run the CountDownTimer in reverse to count up (for each inhale and exhale):
    * https://en.proft.me/2017/11/18/how-create-count-timer-android/
    * */

    private void startTimer() {

        mTimer = new CountDownTimer(mTotalSeconds * 1000, 1000) {

            //Run in reverse to count up...
            public void onTick(long millisUntilFinished) {
                mIndicator.setCurrentProgress((mTotalSeconds*1000-millisUntilFinished)/1000);
            }

            public void onFinish() {
                mIsTimerRunning = false;
                mButtonStopStart.setText(START);
            }
        }.start();

        mButtonStopStart.setText(STOP);
        mIsTimerRunning = true;
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
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
        Crashlytics.log(Log.VERBOSE, TAG, "onResume");
        Crashlytics.logException(new Exception("Non-fatal exception: MainActivity onResume"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
        Crashlytics.logException(new Exception("Non-fatal exception: MainActivity onPause"));
    }

    /*
    * Source: https://github.com/antonKozyriatskyi/CircularProgressIndicator#formatting-progress-text
    * */
    private static final CircularProgressIndicator.ProgressTextAdapter TIME_TEXT_ADAPTER = new CircularProgressIndicator.ProgressTextAdapter() {
        @Override
        public String formatText(double time) {
            int hours = (int) (time / 3600);
            time %= 3600;
            int minutes = (int) (time / 60);
            int seconds = (int) (time % 60);
            StringBuilder sb = new StringBuilder();
            if (hours < 10) {
                sb.append(0);
            }
            sb.append(hours).append(":");
            if (minutes < 10) {
                sb.append(0);
            }
            sb.append(minutes).append(":");
            if (seconds < 10) {
                sb.append(0);
            }
            sb.append(seconds);
            return sb.toString();
        }
    };
}
