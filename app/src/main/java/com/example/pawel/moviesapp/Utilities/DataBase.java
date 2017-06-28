package com.example.pawel.moviesapp.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pawel.moviesapp.R;
import com.example.pawel.moviesapp.Utilities.Models.MovieModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2017-05-30.
 */


public class DataBase {

    private static final String DEBUG_TAG = "SqLiteProduct";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_FAVOURITE_MOVIES_TABLE = "favouriteMovies";

    public static final String KEY_ID = "id";
    public static final String KEY_ID_MOVIE = "id_movie";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_TITLE = "title";

    public static String DB_CREATE_MOVIES_TABLE = "CREATE TABLE " + DB_FAVOURITE_MOVIES_TABLE + " ( " + KEY_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT , " + KEY_ID_MOVIE + " INTEGER, " + KEY_TITLE + " TEXT, " + KEY_POSTER_PATH + " TEXT );";

    // private static final String DROP_PRODUCT_TABLE =
    //         "DROP TABLE IF EXISTS " + DB_PROCUCT_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public DataBase(Context context) {
        this.context = context;
    }

    public DataBase open() {
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }


    public void close() {
        dbHelper.close();
    }

    public long insertMovie(int id_movie, String title, String posterPath) {
        if (id_movie == 0) {
            return -1;
        } else if (!isContaining(id_movie)) {        //if does not contain
            ContentValues newProductValues = new ContentValues();
            newProductValues.put(KEY_ID_MOVIE, id_movie);
            newProductValues.put(KEY_TITLE, title);
            newProductValues.put(KEY_POSTER_PATH, posterPath);
            return db.insert(DB_FAVOURITE_MOVIES_TABLE, null, newProductValues);
        }
        return -2;              //if contains
    }

    public String insertMovieResultInterpreter(long number) {
        String result = "";
        if (number == -1)
            result = context.getString(R.string.errorOccurred);
        else if (number == -2)
            result = context.getString(R.string.existsMovie);
        else if (number >= 0)
            result = context.getString(R.string.movieAddedToList);
        return result;
    }

    // private static final String DROP_PRODUCT_TABLE =
    //         "DROP TABLE IF EXISTS " + DB_FAVOURITE_MOVIES_TABLE;

    //check if the movie is in the data base
    public boolean isContaining(int id) {
        String query = "SELECT * FROM " + DB_FAVOURITE_MOVIES_TABLE + " WHERE " + KEY_ID_MOVIE + "=" + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            return true;
        return false;
    }

    public int deleteMovie(int id) {
        if (id == 0)
            return -1;

        String whereClause = KEY_ID_MOVIE + "=" + id;
        return db.delete(DB_FAVOURITE_MOVIES_TABLE, whereClause, null);
    }

    public String deleteMovieResultInterpreter(int number) {
        if (number != 0)
            return context.getString(R.string.removedFromList);
        else
            return context.getString(R.string.errorOccurred);
    }

    public void dropBase() {
        context.deleteDatabase(DB_NAME);
    }

    public MovieModel getMovie(int id) {
        //might does not work
        String query = "SELECT * FROM " + DB_FAVOURITE_MOVIES_TABLE + " WHERE " + KEY_ID_MOVIE + "=" + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            MovieModel movieModel = new MovieModel(cursor.getInt(cursor.getColumnIndex(KEY_ID_MOVIE)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_POSTER_PATH)));
            return movieModel;
        }
        return null;
    }

    public List<MovieModel> getAllMovies() {
        String[] columns = {KEY_ID, KEY_ID_MOVIE, KEY_TITLE, KEY_POSTER_PATH};
        Cursor cursor = db.query(DB_FAVOURITE_MOVIES_TABLE, columns, null, null, null, null, null);

        List<MovieModel> list = new ArrayList<MovieModel>();
        if (cursor.moveToFirst()) {
            do {
                MovieModel movieModel = new MovieModel(cursor.getInt(cursor.getColumnIndex(KEY_ID_MOVIE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_POSTER_PATH)));
                list.add(movieModel);
            } while (cursor.moveToNext());
        }
        return list;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_MOVIES_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_FAVOURITE_MOVIES_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //   db.execSQL(DROP_PRODUCT_TABLE);
            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_FAVOURITE_MOVIES_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");
            //onCreate(db);
        }
    }
}
