package com.example.criminalintent;
//whether onCreate will be first
import static android.os.Environment.getExternalStorageDirectory;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    private File externalStorageDirectory;

    private boolean externalStorageIsAccessible;

    public CriminalIntentJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
        externalStorageIsAccessible = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(externalStorageIsAccessible){
            externalStorageDirectory = c.getExternalFilesDir(null);
        }
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            // open and read the file into a StringBuilder
            InputStream in;
            if(externalStorageIsAccessible){
                File file = new File(externalStorageDirectory, mFilename);
                // open and read the file into a StringBuilder
                in = new FileInputStream(file);
            }else{
                in =mContext.openFileInput(mFilename);
            }
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        // build an array in JSON
        JSONArray array = new JSONArray();
        for (Crime c : crimes)
            array.put(c.toJSON());

        // write the file to disk
        Writer writer = null;
        try {
            OutputStream out;
            if(externalStorageIsAccessible){
                File file = new File(externalStorageDirectory, mFilename);
                // open and read the file into a StringBuilder
                out = new FileOutputStream(file);
            }else{
                out =mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            }
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}

