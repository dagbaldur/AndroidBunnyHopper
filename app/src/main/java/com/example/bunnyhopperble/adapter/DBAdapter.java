package com.example.bunnyhopperble.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DBAdapter extends SQLiteOpenHelper {

    /** 0 = not saved, 1 = saving in progress, 2 = saved successful, 3 = save failed, records not found, 4 = saved failed, exception */
    private final MutableLiveData<Integer> save_state = new MutableLiveData<>( 0);

    private final static String db = "BUNNY_HOPPER";
    private final static String table = "HOPS";
    private File filename;

    public DBAdapter(Context context){
        super(context,db,null,2);
    }

    public void setFile( File fn){
        filename = fn;
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("CREATE TABLE "+ table +"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FILENAME STRING," +
                "TIME STRING," +
                "LOCATION STRING," +
                "SENSOR STRING," +
                "VALUE DOUBLE," +
                "ROAD STRING," +
                "TECHNIQUES STRING" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ table);
        onCreate(sqLiteDatabase);
    }

    private ArrayList<Object> prepareEntries(){
        ArrayList<Object> list = new ArrayList<>();
        String fn = filename.getName().toString();
        try{
            Log.i("DBAdapter","Reading file "+fn+" for DB storage");
            FileInputStream fr =  new FileInputStream(String.valueOf(filename));
            InputStreamReader reader = new InputStreamReader(fr);
            BufferedReader buffer = new BufferedReader(reader);
            StringBuffer data = new StringBuffer("");
            String readString = buffer.readLine();

            while( readString != null) {
                data.append(readString);
                readString = buffer.readLine();
                if(readString != null) {
                    StringTokenizer token = new StringTokenizer(readString, "|");
                    while (token.hasMoreTokens()) {
                        list.add(fn);//filename
                        list.add(token.nextToken());//time
                        list.add(token.nextToken());//location
                        list.add(token.nextToken());//sensor
                        double val = Double.parseDouble(token.nextToken());
                        list.add(val);//value
                        list.add(token.nextToken());//road type
                        list.add(token.nextToken());//technique
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("DBAdapter","Couldn't read file "+filename.getName()+" for DB storage");
            e.printStackTrace();
        }
        return list;
    }

    public void saveData(){
        int j = 0;
        ArrayList<Object> values = prepareEntries();
        if(values.size() > 0) {
            Log.i("DBAdapter",+values.size()+" entries prepared to save");
            try {
                SQLiteDatabase database = this.getWritableDatabase();
                try {
                    save_state.postValue(1);
                    Log.i("DBAdapter", "Saving to DB...");
                    database.beginTransaction();

                    String sql = "INSERT INTO " + table + "(FILENAME,TIME,LOCATION,SENSOR,VALUE,ROAD,TECHNIQUES) VALUES(?,?,?,?,?,?,?)";
                    SQLiteStatement statement = database.compileStatement(sql);

                    for (int i = 0; i < values.size(); i++) {
                        if (i % 7 == 0) {
                            statement.clearBindings();
                            statement.bindString(1, values.get(i).toString());//filename
                            statement.bindString(2, values.get(i + 1).toString());//time
                            statement.bindString(3, values.get(i + 2).toString());//location
                            statement.bindString(4, values.get(i + 3).toString());//sensor
                            statement.bindDouble(5, (double) values.get(i + 4));//value
                            statement.bindString(6, values.get(i + 5).toString());//road
                            statement.bindString(7, values.get(i + 6).toString());//technique
                            statement.executeInsert();
                            j++;
                        }
                    }
                    database.setTransactionSuccessful();
                    save_state.postValue(2);
                    Log.i("DBAdapter", "Saved " + j + " registries to DB");
                } catch (Exception e) {
                    save_state.postValue(4);
                    Log.e("DBAdapter", "Error encountered: " + e);
                } finally {

                    database.endTransaction();
                }
            }catch(Exception e){
                save_state.postValue(4);
                Log.e("DBAdapter", "Database error: " + e);
            }
        }else{
            save_state.postValue(3);
            Log.i("DBAdapter", "No entries to save...");
        }
    }



    public final LiveData<Integer> getSavedState() { return save_state;	}
    public void setSavedState(int state){
        save_state.setValue(state);
    }

    //future change -> do it from dbToFile to dbToCommit, possibly give the option for either?
    public void dbToFile(){

    }
}
