package com.example.criminalintent;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT ="suspect";
    private static final String JSON_NUMBER = "number";
    private UUID mId;
    private String mTitle = "";
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private String mSuspect;
    private Uri mNumber;
    public Crime() {
        // Generate unique identifier
        mId = UUID.randomUUID();
        mDate = new Date();
    }
    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mSolved = json.getBoolean(JSON_SOLVED);
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO))
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        if (json.has(JSON_SUSPECT)){
            mSuspect = json.getString(JSON_SUSPECT);
        }
        if(json.has(JSON_NUMBER)){
            mNumber = Uri.parse(json.getString(JSON_NUMBER));
        }
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_DATE, mDate.getTime());
        json.put(JSON_SOLVED, mSolved);
        if (mPhoto != null){
            json.put(JSON_PHOTO, mPhoto.toJSON());
            json.put(JSON_SUSPECT, mSuspect);
        }
        if(mNumber != null){
            json.put(JSON_NUMBER, mNumber.toString());
        }
        return json;
    }

    public String getmSuspect() {
        return mSuspect;
    }

    public void setmSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    public UUID getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }
    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }
    public Photo getmPhoto() {
        return mPhoto;
    }
    public void setmPhoto(Photo mPhoto) {
        this.mPhoto = mPhoto;
    }

    public Uri getmNumber() {
        return mNumber;
    }

    public void setmNumber(Uri mNumber) {
        this.mNumber = mNumber;
    }

    @Override
    public String toString(){
        return mTitle;
    }
}
