package cha.com.autodetectsms;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = " ValueDB";

    // Contacts table name
    private static final String TABLE_Values = " ValueTable";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_Text = "text";
    private static final String KEY_Morse = "morse";

    private final  String[] alpha = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "0", " " };
    private final String[] dottie = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
            "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.",
            "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
            "-.--", "--..", ".----", "..---", "...--", "....-", ".....",
            "-....", "--...", "---..", "----.", "-----", "|" }; //Morse mapping for all the letters and digits


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Table_Value = "CREATE TABLE " + TABLE_Values + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_Text + " TEXT,"
                + KEY_Morse + " TEXT" + ")";

        db.execSQL(CREATE_Table_Value);
      //  initialiseValues();
      //  db.execSQL("INSERT INTO "+TABLE_Values +" VALUES (0 ,"++ "," + gson.toJson(dottieArrayList) +")") ;

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Values);
       // Create tables again
        onCreate(db);
    }

    public  ValueDTO getValuesFromDataBase() {
        String selectQuery = "SELECT  * FROM "+TABLE_Values;
        ValueDTO valueDTO=new ValueDTO();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                valueDTO.setAlpha(cursor.getString(1));
                valueDTO.setDottie(cursor.getString(2));

            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return movieDTO list
        return valueDTO;
    }

    public int initialiseValues() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> alphaArrayList=new ArrayList<String>(Arrays.asList(alpha));
        ArrayList<String> dottieArrayList=new ArrayList<String>(Arrays.asList(dottie));
        Gson gson=new Gson();

        ContentValues values = new ContentValues();
        values.put(KEY_Morse, gson.toJson(dottieArrayList) );
        values.put(KEY_Text, gson.toJson(alphaArrayList) );
        values.put(KEY_ID, 1);
        long id=   db.insert(TABLE_Values, null, values);
        Log.i("Inserting Memory Info", id + "");
        return (int)id;

    }


}
