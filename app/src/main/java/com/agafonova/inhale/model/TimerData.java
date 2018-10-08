package com.agafonova.inhale.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Olga Agafonova on 9/29/18.
 */

public class TimerData implements Parcelable {

    private String mTime;
    private String mInhale;
    private String mExhale;
    private String mPause;
    private boolean isSelected;

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmInhale() {
        return mInhale;
    }

    public void setmInhale(String mInhale) {
        this.mInhale = mInhale;
    }

    public String getmExhale() {
        return mExhale;
    }

    public void setmExhale(String mExhale) {
        this.mExhale = mExhale;
    }

    public String getmPause() {
        return mPause;
    }

    public void setmPause(String mPause) {
        this.mPause = mPause;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public TimerData() {

    }

    private TimerData(Parcel in) {
        mTime = in.readString();
        mInhale = in.readString();
        mExhale = in.readString();
        mPause = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mTime);
        parcel.writeString(mInhale);
        parcel.writeString(mExhale);
        parcel.writeString(mPause);
    }

    static final Parcelable.Creator<TimerData> CREATOR = new Parcelable.Creator<TimerData>() {
        @Override
        public TimerData createFromParcel(Parcel in) {
            return new TimerData(in);
        }

        @Override
        public TimerData[] newArray(int size) {
            return new TimerData[size];
        }
    };
}
