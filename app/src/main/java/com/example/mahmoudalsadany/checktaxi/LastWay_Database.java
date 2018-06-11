package com.example.mahmoudalsadany.checktaxi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by fci on 11/03/17.
 */

public class LastWay_Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Last_Way";

    private static final String TABLE_NAME = "Path";

    private static final String PID = "id";

    private static final String LATITUDE = "lat";

    private static final String LONGITUDE = "lon";

    private static final int DATABASE_VERSION = 3;
    Context cont;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME +
            "( " + PID + " integer primary key , " + LATITUDE + " varchar(255) not null, " + LONGITUDE + " varchar(2) not null );";

    // Database Deletion
    private static final String DATABASE_DROP = "drop table if exists " + TABLE_NAME + ";";

    public LastWay_Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE);
            Toast.makeText(cont, "database created", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(cont, "database doesn't created " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(DATABASE_DROP);
            onCreate(db);
            Toast.makeText(cont, "database upgraded", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(cont, "database doesn't upgraded " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean InsertData(String lat, String lon) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LATITUDE, lat);
        contentValues.put(LONGITUDE, lon);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        return result == -1 ? false : true;
    }

    public void DropTable(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(DATABASE_DROP);
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    public Cursor ShowData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + " ;", null);
        return cursor;
    }


    public int DeleteData(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

}
