package com.example.criminalintent;

import android.media.ExifInterface;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String JSON_FILENAME = "filename";

    private static final String JSON_ORIENTATION ="orientation";

    private String mFilename;
    //private int mOrientation;

    /** create a Photo representing an existing file on disk */
    public Photo(String filename) {
        mFilename = filename;
        //ExifInterface exif = null;
        /*try{
            exif = new ExifInterface(filePath);
        }
        catch (Exception e){
            Log.d("Photo", "Cannot open "+ mFilename+" caused by"+e);
        }*/

        //mOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    }

    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
        /*if(json.has(JSON_ORIENTATION))
            mOrientation = json.getInt(JSON_ORIENTATION);
        else{
            mOrientation = 0;
        }*/
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFilename);
        //json.put(JSON_ORIENTATION, mOrientation);
        return json;
    }

    public String getFilename() {
        return mFilename;
    }

    /*public int getmOrientation() {
        return mOrientation;
    }*/
}
