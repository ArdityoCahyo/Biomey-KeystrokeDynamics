package com.kanjengdev.biomey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "keystrokeDB";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel
    private static final String TABLE_USER = "user";
    private static final String TABLE_TEMP = "temp";
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_KEYSTROKE = "keystroke";

    // Kolom-kolom yang digunakan dalam kedua tabel
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_INDEX = "idx";      // Menggunakan 'idx' karena 'index' adalah keyword di SQL
    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_SID = "sid";
    private static final String COLUMN_KALIMAT = "kalimat";
    private static final String COLUMN_PRESS = "press";
    private static final String COLUMN_RELEASE = "release";
    private static final String COLUMN_KEY = "key";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel user
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_UID + " TEXT);";
        db.execSQL(createUserTable);

        // Membuat tabel temp
        String createLoginTable = "CREATE TABLE " + TABLE_LOGIN + " (" +
                COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UID + " TEXT, " +
                COLUMN_SID + " TEXT, " +
                COLUMN_KALIMAT + " TEXT, " +
                COLUMN_PRESS + " INTEGER, " +
                COLUMN_RELEASE + " INTEGER, " +
                COLUMN_KEY + " TEXT);";
        db.execSQL(createLoginTable);

        // Membuat tabel temp
        String createTempTable = "CREATE TABLE " + TABLE_TEMP + " (" +
                COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UID + " TEXT, " +
                COLUMN_SID + " TEXT, " +
                COLUMN_KALIMAT + " TEXT, " +
                COLUMN_PRESS + " INTEGER, " +
                COLUMN_RELEASE + " INTEGER, " +
                COLUMN_KEY + " TEXT);";
        db.execSQL(createTempTable);

        // Membuat tabel keystroke
        String createKeystrokeTable = "CREATE TABLE " + TABLE_KEYSTROKE + " (" +
                COLUMN_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UID + " TEXT, " +
                COLUMN_SID + " TEXT, " +
                COLUMN_KALIMAT + " TEXT, " +
                COLUMN_PRESS + " INTEGER, " +
                COLUMN_RELEASE + " INTEGER, " +
                COLUMN_KEY + " TEXT);";
        db.execSQL(createKeystrokeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tabel jika ada versi baru
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEYSTROKE);
        onCreate(db);
    }

    // Menambahkan data ke tabel temp
    public void insertUser(String user, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user);
        values.put(COLUMN_UID, uid);

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Menambahkan data ke tabel temp
    public void insertLogin(String uid, int sid, int kalimat, long press, long release, String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, uid);
        values.put(COLUMN_SID, sid);
        values.put(COLUMN_KALIMAT, kalimat);
        values.put(COLUMN_PRESS, press);
        values.put(COLUMN_RELEASE, release);
        values.put(COLUMN_KEY, key);

        db.insert(TABLE_LOGIN, null, values);
        db.close();
    }

    // Menambahkan data ke tabel temp
    public void insertIntoTemp(String uid, int sid, int kalimat, long press, long release, String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, uid);
        values.put(COLUMN_SID, sid);
        values.put(COLUMN_KALIMAT, kalimat);
        values.put(COLUMN_PRESS, press);
        values.put(COLUMN_RELEASE, release);
        values.put(COLUMN_KEY, key);

        db.insert(TABLE_TEMP, null, values);
//        db.close();
    }

    // Memindahkan data dari tabel temp ke tabel keystroke
    public void moveTempToKeystroke() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Mengambil semua data dari tabel temp
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEMP, null);

        if (cursor.moveToFirst()) {
            do {
                String uid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID));
                String sid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SID));
                String kalimat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KALIMAT));
                long press = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRESS));
                long release = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RELEASE));
                String key = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEY));

                // Memasukkan data ke tabel keystroke
                ContentValues values = new ContentValues();
                values.put(COLUMN_UID, uid);
                values.put(COLUMN_SID, sid);
                values.put(COLUMN_KALIMAT, kalimat);
                values.put(COLUMN_PRESS, press);
                values.put(COLUMN_RELEASE, release);
                values.put(COLUMN_KEY, key);
                db.insert(TABLE_KEYSTROKE, null, values);

            } while (cursor.moveToNext());
        }

        cursor.close();

        // Mengosongkan tabel temp
        db.execSQL("DELETE FROM " + TABLE_TEMP);
        db.close();
    }

    public void resetLogin(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOGIN);
        db.close();
    }

    public void resetTemp(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TEMP);
        db.close();
    }

    public void resetKeyDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_KEYSTROKE);
        db.close();
    }

    public JSONArray getKeystrokeAsJson() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_KEYSTROKE, null);

        JSONArray jsonArray = new JSONArray();

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(COLUMN_INDEX, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INDEX)));
                    jsonObject.put(COLUMN_UID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
                    jsonObject.put(COLUMN_SID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SID)));
                    jsonObject.put(COLUMN_KALIMAT, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KALIMAT)));
                    jsonObject.put(COLUMN_PRESS, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRESS)));
                    jsonObject.put(COLUMN_RELEASE, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RELEASE)));
                    jsonObject.put(COLUMN_KEY, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEY)));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jsonArray;
    }

    public JSONArray getLoginAsJson() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGIN, null);

        JSONArray jsonArray = new JSONArray();

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(COLUMN_INDEX, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INDEX)));
                    jsonObject.put(COLUMN_UID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
                    jsonObject.put(COLUMN_SID, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SID)));
                    jsonObject.put(COLUMN_KALIMAT, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KALIMAT)));
                    jsonObject.put(COLUMN_PRESS, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRESS)));
                    jsonObject.put(COLUMN_RELEASE, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RELEASE)));
                    jsonObject.put(COLUMN_KEY, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEY)));

                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jsonArray;
    }

}