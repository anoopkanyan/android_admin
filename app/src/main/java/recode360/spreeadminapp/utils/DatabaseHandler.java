package recode360.spreeadminapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import recode360.spreeadminapp.models.State;

/**
 * Class to handle database CRUD operations.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "cacheManager";

    // States table name
    private static final String TABLE_STATES = "states";

    // States Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE_ID = "state_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STATES_TABLE = "CREATE TABLE " + TABLE_STATES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_STATE_ID + " TEXT" + ")";
        db.execSQL(CREATE_STATES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding a new state
    public void addState(State state) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, state.getName()); // State Name
        values.put(KEY_STATE_ID, Integer.toString(state.getId())); // State Id

        // Inserting Row
        db.insert(TABLE_STATES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single state
    public State getState(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STATES, new String[]{KEY_ID,
                        KEY_NAME, KEY_STATE_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        State state = new State();

        state.setName(cursor.getString(1));
        state.setId(Integer.parseInt(cursor.getString(2)));
        // return state
        return state;
    }

    // Getting All States
    public List<State> getAllStates() {
        List<State> stateList = new ArrayList<State>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STATES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                State state = new State();
                state.setName(cursor.getString(1));
                state.setId(Integer.parseInt(cursor.getString(2)));
                // Adding state to list
                stateList.add(state);
            } while (cursor.moveToNext());
        }

        // return state list
        return stateList;
    }

    // Updating single state
    public int updateState(State state) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, state.getName());
        values.put(KEY_STATE_ID, state.getId());

        // updating row
        //to be updated to work perfectly, use something else instead of the ids
        return db.update(TABLE_STATES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(state.getId())});
    }

    // Deleting single state
    //to be updated to work perfectly, use something else instead of the ids
    public void deleteState(State state) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STATES, KEY_ID + " = ?",
                new String[]{String.valueOf(state.getId())});
        db.close();
    }


    // Getting states Count
    public int getStatesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STATES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
