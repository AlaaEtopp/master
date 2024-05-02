package com.example.projet;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.projet.MyDBHelper;

import java.util.ArrayList;
import java.util.List;

public class PictureDAO {
    private SQLiteDatabase db;
    private MyDBHelper dbHelper;

    public PictureDAO(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }


    public void close() {
        dbHelper.close();
    }

    public void insertURL(String url) {
        try {
            ContentValues values = new ContentValues();
            values.put(MyDBHelper.COLUMN_URL, url);
            open(); // Open the database connection before inserting
            long newRowId = db.insert(MyDBHelper.TABLE_NAME, null, values);

            if (newRowId == -1) {
                // Insert failed
                Log.e(TAG, "Failed to insert URL: " + url);
            } else {
                // Insert successful
                Log.d(TAG, "Inserted URL: " + url);
            }
        } catch (SQLiteException e) {
            // SQLiteException occurred during insertion
            Log.e(TAG, "SQLiteException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Other exception occurred
            Log.e(TAG, "Error inserting URL: " + url, e);
        }
    }


    public List<String> getAllURLs() {
        List<String> urls = new ArrayList<>();
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(MyDBHelper.COLUMN_URL));
                urls.add(url);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return urls;
    }

    // Implement other CRUD operations as needed
}
