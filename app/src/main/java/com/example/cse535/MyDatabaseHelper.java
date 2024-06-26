package com.example.cse535;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "PersonalHealth.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "health_monitor";
    private static final String COLUMN_ID = "_id";
    private static final String HEART_RATE = "heart_rate";
    private static final String RESPIRATORY_RATE = "respiratory_rate";
    private static final String NAUSEA = "nausea";
    private static final String HEADACHE = "headache";
    private static final String DIARRHEA = "diarrhea";
    private static final String SOAR_THROAT = "soar_throat";
    private static final String FEVER = "fever";
    private static final String MUSCLE_ACHE = "muscle_ache";
    private static final String NO_SMELL_TASTE = "no_smell_taste";
    private static final String COUGH = "cough";
    private static final String BREATHLESSNESS = "breathlessness";
    private static final String TIRED = "tired";
    private static final String COMPUTED_SYMPTOMS = "computed_symptoms";
    private static final String SLEEP = "sleep";
    private static final String RESPONSE = "response";
    private static final String REACTION_TIME = "reaction_time";



    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + HEART_RATE + " INTEGER, "
                + RESPIRATORY_RATE + " INTEGER, "
                + NAUSEA + " INTEGER CHECK (" + NAUSEA + " >= 0 AND " + NAUSEA + " <= 5), "
                + HEADACHE + " INTEGER CHECK (" + HEADACHE + " >= 0 AND " + HEADACHE + " <= 5), "
                + DIARRHEA + " INTEGER CHECK (" + DIARRHEA + " >= 0 AND " + DIARRHEA + " <= 5), "
                + SOAR_THROAT + " INTEGER CHECK (" + SOAR_THROAT + " >= 0 AND " + SOAR_THROAT + " <= 5), "
                + FEVER + " INTEGER CHECK (" + FEVER + " >= 0 AND " + FEVER + " <= 5), "
                + MUSCLE_ACHE + " INTEGER CHECK (" + MUSCLE_ACHE + " >= 0 AND " + MUSCLE_ACHE + " <= 5), "
                + NO_SMELL_TASTE + " INTEGER CHECK (" + NO_SMELL_TASTE + " >= 0 AND " + NO_SMELL_TASTE + " <= 5), "
                + COUGH + " INTEGER CHECK (" + COUGH + " >= 0 AND " + COUGH + " <= 5), "
                + BREATHLESSNESS + " INTEGER CHECK (" + BREATHLESSNESS + " >= 0 AND " + BREATHLESSNESS + " <= 5), "
                + TIRED + " INTEGER CHECK (" + TIRED + " >= 0 AND " + TIRED + " <= 5), "
                + COMPUTED_SYMPTOMS + " DECIMAL, "
                + SLEEP + " INTEGER,"
                + RESPONSE + " TEXT, "
                + REACTION_TIME + " INTEGER"
                + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveRecord(
            int heartRate,
            int respiratoryRate,
            int fever,
            int nausea,
            int headache,
            int diarrhea,
            int soarThroat,
            int muscleAche,
            int noSmellTaste,
            int cough,
            int breathlessness,
            int tired,
            double computedSymptoms,
            int sleep,
            String response,
            int reaction_time
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(HEART_RATE, heartRate);
        cv.put(RESPIRATORY_RATE, respiratoryRate);
        cv.put(NAUSEA, nausea);
        cv.put(FEVER, fever);
        cv.put(HEADACHE, headache);
        cv.put(DIARRHEA, diarrhea);
        cv.put(SOAR_THROAT, soarThroat);
        cv.put(MUSCLE_ACHE, muscleAche);
        cv.put(NO_SMELL_TASTE, noSmellTaste);
        cv.put(COUGH, cough);
        cv.put(BREATHLESSNESS, breathlessness);
        cv.put(TIRED, tired);
        cv.put(COMPUTED_SYMPTOMS, computedSymptoms);
        cv.put(SLEEP, sleep);
        cv.put(RESPONSE, response);
        cv.put(REACTION_TIME,reaction_time);

        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1) {
            Toast.makeText(context, "Record was not saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Record saved successfully", Toast.LENGTH_SHORT).show();
        }

    }



    public Cursor getAllRecords() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
