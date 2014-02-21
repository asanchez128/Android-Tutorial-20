package com.bignerdranch.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CriminalIntentJSONSerializer {
   
   private Context mContext;
   private String mFilename;
   private File mFile;
   
   private static final String TAG = "CrimeLabCrimeLab";
   
   public CriminalIntentJSONSerializer(Context c, String f){
      mContext = c;
      mFilename = f;
   }
   
   public boolean isExternalStorageWritable() {
      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state)) {
         return true;
      }
      return false;
   }
   
   public File makeDir() {
      File root = Environment.getExternalStorageDirectory();
      
      File dir = new File (root.getAbsolutePath() + "/criminalIntent");
      dir.mkdirs();
      File file = new File(dir, "criminal.json");
      
      return file;
   }
   
   public File getFile() {
      mFile = makeDir();
      return mFile;
   }
   
   public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
      ArrayList<Crime> crimes = new ArrayList<Crime>();
      BufferedReader reader = null;
      
      try {
         // Open and read the file into a StringBuilder
         InputStream in = new FileInputStream (getFile().toString());
         reader = new BufferedReader(new InputStreamReader(in));
         StringBuilder jsonString = new StringBuilder();
         String line = null;
         while ((line = reader.readLine()) != null) {
            // Line breaks are omitted and irrelevant
            jsonString.append(line);
         }
         // Parse the JSON using JSONTokener
         JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
         .nextValue();
         // Build the array of crimes from JSONObjects
         for (int i = 0; i < array.length(); i++) {
            crimes.add(new Crime(array.getJSONObject(i)));
         }
      } catch (FileNotFoundException e) {
         // Ignore this one; it happens when starting fresh
      } finally {
         if (reader != null)
            reader.close();
      }
      return crimes;
   }
   
   public void saveCrimes(ArrayList<Crime> crimes)
         throws JSONException, IOException {
      
      // Build an array in JSON
      JSONArray array = new JSONArray();
      for (Crime c : crimes)
         array.put(c.toJSON());
      
      // Write file to disk
      makeDir();
      
      try {
         FileOutputStream f = new FileOutputStream(getFile());
         PrintWriter pw = new PrintWriter(f);
         pw.write(array.toString());
         pw.flush();
         pw.close();
         f.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         Log.i(TAG, "File not found");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
