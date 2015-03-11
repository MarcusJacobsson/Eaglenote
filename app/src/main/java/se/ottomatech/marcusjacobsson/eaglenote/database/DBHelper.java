package se.ottomatech.marcusjacobsson.eaglenote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marcus Jacobsson on 2015-01-20.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "qwknote.db";
    // FOLDER
    public static final String TABLE_NAME_FOLDER = "folder";
    public static final String COLUMN_FOLDER_ID = "_id";

    //NOTE
    public static final String TABLE_NAME_NOTE = "note";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_TIME = "time";
    public static final String COLUMN_NOTE_NOTE = "note";
    public static final String COLUMN_NOTE_FOLDER_ID = "folder_id";

    public static final int DATABASE_VERSION = 1;

    public static final String INITIAL_INSERT =
            "INSERT INTO "
                    + TABLE_NAME_FOLDER
                    +
                    " VALUES ('MAIN');";


    public static final String DATABASE_CREATE_FOLDER =
            "create table "
                    + TABLE_NAME_FOLDER
                    + " ( "
                    + COLUMN_FOLDER_ID + " text primary key);";

    public static final String DATABASE_CREATE_NOTE =
            "create table "
                    + TABLE_NAME_NOTE
                    + " ( "
                    + COLUMN_NOTE_ID + " integer primary key autoincrement,"
                    + COLUMN_NOTE_TITLE + " text ,"
                    + COLUMN_NOTE_NOTE + " text,"
                    + COLUMN_NOTE_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
                    + COLUMN_NOTE_FOLDER_ID + " text not null, FOREIGN KEY (" + COLUMN_NOTE_FOLDER_ID + ") REFERENCES " + TABLE_NAME_FOLDER + "(" + COLUMN_FOLDER_ID + ")"
                    + ");";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_FOLDER);
        db.execSQL(DATABASE_CREATE_NOTE);
        db.execSQL(INITIAL_INSERT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FOLDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTE);
        onCreate(db);
    }
}
