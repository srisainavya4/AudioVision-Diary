package com.example.visuallyimpaired.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.visuallyimpaired.Models.PersonModel;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "personalDiary";
    private static final String TABLE_PEOPLE = "People";
    private static final String PERSON_ID = "Pid";
    private static final String PERSON_NAME = "PersonName";
    private static final String PERSON_BITMAP = "PersonBitmap";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPersonTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);
        onCreate(db);
    }

    public void createPersonTable(SQLiteDatabase db) {
        String CREATE_PERSON_TABLE = "CREATE TABLE " + TABLE_PEOPLE + "("
                + PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + PERSON_NAME + " TEXT,"
                + PERSON_BITMAP + " BLOB" + ")";
        db.execSQL(CREATE_PERSON_TABLE);
    }

    public void addPerson(Context con,PersonModel personModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PERSON_NAME, personModel.getPersonName());
        values.put(PERSON_BITMAP, personModel.getPersonBitmap());
        long i=db.insert(TABLE_PEOPLE, null, values);
        db.close();
//        Toast.makeText(con, ""+i, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<PersonModel> getAllPerson() {
        ArrayList<PersonModel> personModelArrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PEOPLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PersonModel personModel = new PersonModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getBlob(2)
                );
                personModelArrayList.add(personModel);
            } while (cursor.moveToNext());
        }

        return personModelArrayList;
    }
}
