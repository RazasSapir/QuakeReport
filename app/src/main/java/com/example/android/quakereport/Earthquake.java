package com.example.android.quakereport;

public class Earthquake {
    private double mMag;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mWebLink;

    public Earthquake(double mag, String location, long timeInMilliseconds, String webLink) {
        mMag = mag;
        mTimeInMilliseconds = timeInMilliseconds;
        mLocation = location;
        mWebLink = webLink;
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

    public String getWebLink() {
        return mWebLink;
    }
}
