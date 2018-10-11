package com.agafonova.inhale.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Updated by Olga Agafonova on 10/11/18.
 */

@Entity(tableName = "master_table")
public class TimerData implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "time")
    private String mTime;

    @ColumnInfo(name = "inhale")
    private String mInhale;

    @ColumnInfo(name = "exhale")
    private String mExhale;

    @ColumnInfo(name = "pause")
    private String mPause;

    @ColumnInfo(name = "isselected")
    private boolean isSelected;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getInhale() {
        return mInhale;
    }

    public void setInhale(String mInhale) {
        this.mInhale = mInhale;
    }

    public String getExhale() {
        return mExhale;
    }

    public void setExhale(String mExhale) {
        this.mExhale = mExhale;
    }

    public String getPause() {
        return mPause;
    }

    public void setPause(String mPause) {
        this.mPause = mPause;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public TimerData() {

    }

    public TimerData(int iId, String iTime, String iInhale, String iExhale, String iPause) {
        this.mId = iId;
        this.mTime = iTime;
        this.mInhale = iInhale;
        this.mExhale = iExhale;
        this.mPause = iPause;
    }

    @Ignore
    public TimerData(String iTime, String iInhale, String iExhale, String iPause) {
        this.mTime = iTime;
        this.mInhale = iInhale;
        this.mExhale = iExhale;
        this.mPause = iPause;
    }

    private TimerData(Parcel in) {
        mId = in.readInt();
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
