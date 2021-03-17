package com.example.bunnyhopperble.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import static java.time.LocalDateTime.*;


public class RecordAdapter  {

    private final MutableLiveData<Boolean> record_state = new MutableLiveData<>(false);
    private final File directory;
    private File record_session;
    private final static String DIRECTORY_NAME = "/BunnyHopper/";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public RecordAdapter(@NonNull final Context context) {
        File cache = context.getCacheDir();
        directory = new File(cache,DIRECTORY_NAME);
        if(!directory.exists()){
            if(!directory.mkdir()){
                Log.e("DirectoryError","Cannot create directory");
            }else{
                Log.i("Directory","Writting into "+directory.getPath());
                directory.mkdirs();
            }
        } else{
            Log.i("Directory","Directory exists");
        }
        String file_name = String.format("BunnyLog%s.txt", now());
        record_session = new File(directory,file_name);
        changeRecording();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void newFile(){
        if(!record_state.getValue()){
            Log.i("Directory","Writting into "+directory.getPath());
            String file_name = String.format("BunnyLog%s.txt", now());
            Log.i("RecordAdapter","Writting into "+file_name);
            record_session = new File(directory,file_name);
            changeRecording();
        }
    }

    public File getFilename(){
        return record_session;
    }

    public void addRecord(int roadType, int technique[],String sensor, Timestamp timestamp, float value){
        if(record_state.getValue()) {

            String formatted_values = timestamp.getTime() + "|" + roadType + "|" + sensor + "|" + value + "|" + java.util.Arrays.toString(technique);
            try {
                FileOutputStream file = new FileOutputStream(String.valueOf(record_session),true);
                OutputStreamWriter writer = new OutputStreamWriter(file, StandardCharsets.UTF_8);
                //Log.i("Directory","Writing file "+record_session.getName()+" directory "+directory.getPath());
                writer.append(formatted_values);
                writer.append("\n");
                writer.close();
                file.close();
            } catch (FileNotFoundException e) {
                Log.e("RecordException","File not found, resetting state");
                record_state.setValue(false);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("RecordException","General error");
                record_state.setValue(false);
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean deleteRecording(){
        if(!record_session.delete()){
            Log.e("Directory","Cannot delete record");
            return false;
        }
        String file_name = String.format("BunnyLog%s.txt", now());
        record_session = new File(directory,file_name);
        //changeRecording();
        return true;
    }


    public void changeRecording(){
        record_state.setValue(!record_state.getValue());
    }

    public final LiveData<Boolean> getRecordingState() { return record_state;	}
}
