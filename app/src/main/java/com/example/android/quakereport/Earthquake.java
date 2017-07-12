package com.example.android.quakereport;

public class Earthquake {
    private double mMag;
    private String mLocation;
    private long mTimeInMilliseconds;

    public Earthquake(double mag, String location, long timeInMilliseconds) {
        mMag = mag;
        mTimeInMilliseconds = timeInMilliseconds;
        mLocation = location;
    }

    public double getMag() {
        return mMag;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getLocation() {
        return mLocation;
    }
}
