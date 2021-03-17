package com.example.bunnyhopperble.adapter;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
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
import java.io.FileNotFoundException;
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
                "ROAD STRING," +
                "SENSOR STRING," +
                "VALUE DOUBLE," +
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
                        list.add(token.nextToken());//road type
                        list.add(token.nextToken());//sensor
                        double val = Double.parseDouble(token.nextToken());
                        list.add(val);//value
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
            SQLiteDatabase database = this.getWritableDatabase();

            try {
                save_state.setValue(1);
                Log.i("DBAdapter", "Saving to DB...");
                database.beginTransaction();
                String sql = "INSERT INTO " + table + "(FILENAME,TIME,ROAD,SENSOR,VALUE,TECHNIQUES) VALUES(?,?,?,?,?,?)";
                SQLiteStatement statement = database.compileStatement(sql);

                for (int i = 0; i < values.size(); i++) {
                    if(i%6 == 0) {
                        statement.clearBindings();
                        statement.bindString(1, values.get(i).toString());
                        statement.bindString(2, values.get(i + 1).toString());
                        statement.bindString(3, values.get(i + 2).toString());
                        statement.bindString(4, values.get(i + 3).toString());
                        statement.bindDouble(5, (double) values.get(i + 4));
                        statement.bindString(6, values.get(i + 5).toString());
                        statement.executeInsert();
                        j++;
                    }
                }
                database.setTransactionSuccessful();
                save_state.setValue(2);
                Log.i("DBAdapter", "Saved " + j + " registries to DB");
            } catch (Exception e) {
                save_state.setValue(4);
                Log.w("DBAdapter", "Error encountered: " + e);
            } finally {
                database.endTransaction();
            }
        }else{
            save_state.setValue(3);
            Log.i("DBAdapter", "No entries to save...");
        }
    }
    /** 0 = not saved, 1 = saving in progress, 2 = saved successful, 3 = save failed, records not found, 4 = saved failed, exception */
    /**public void saveData(){

        int i =0;
        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransaction();
        try{
            ContentValues vals = new ContentValues();
            save_state.setValue(1);
            String fn = filename.getName().toString();
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
                        vals.put("FILENAME",fn);
                        vals.put("TIME", token.nextToken());
                        vals.put("SENSOR", token.nextToken());
                        float val = Float.parseFloat(token.nextToken());
                        vals.put("VALUE", val);
                        database.insert(table, null, vals);
                        i += 1;
                    }
                    //
                }else{
                    Log.i("DBAdapter", "Finished reading");
                }
            }
            database.endTransaction();
            reader.close();
        }catch (FileNotFoundException e) {
            save_state.setValue(3);
            Log.e("DBAdapter","Couldn't read file "+filename.getName()+" for DB storage");
            e.printStackTrace();
        } catch (IOException e) {
            save_state.setValue(3);
            Log.e("DBAdapter","Couldn't read file "+filename.getName()+" for DB storage");
            e.printStackTrace();
        }finally {
            if(database != null && database.inTransaction())
                database.endTransaction();
        }

        if(i != 0) {
            save_state.setValue(2);
            Log.i("DBAdapter","Saved "+i+" registries to DB");
        }
    }*/

    public final LiveData<Integer> getSavedState() { return save_state;	}
    public void setSavedState(int state){
        save_state.setValue(state);
    }

    //future change -> do it from dbToFile to dbToCommit, possibly give the option for either?
    public void dbToFile(){

    }
}
