package se.ottomatech.marcusjacobsson.eaglenote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;

/**
 * Created by Marcus Jacobsson on 2015-01-20.
 */
public class Datasource {
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public Datasource(Context ctx) {
        dbHelper = new DBHelper(ctx);
    }

    // Open the DB connection
    public void open() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    // Close the DB connection
    public void close() throws SQLException {
        dbHelper.close();
    }

    // FOLDER
    public ArrayList<Folder> getAllFolders() {
        ArrayList<Folder> allFolders = new ArrayList<Folder>();
        Cursor cursor = database.query(DBHelper.TABLE_NAME_FOLDER, null, null, null,
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Folder tmpFolder = new Folder();
            tmpFolder.setName(cursor.getString(0));
            allFolders.add(tmpFolder);
            cursor.moveToNext();
        }
        cursor.close();
        return allFolders;
    }

    public long createFolder(String folderName) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FOLDER_ID, folderName);
        return database.insert(DBHelper.TABLE_NAME_FOLDER, null, values);
    }

    public boolean updateFolderName(int id, String newFolderName) {
        return false;
    }

    public int removeFolder(String name) {
        return database.delete(DBHelper.TABLE_NAME_FOLDER, DBHelper.COLUMN_FOLDER_ID + "='" + name+"'", null);
    }

    //NOTE
    public long createNote(String title, String note, String folder) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NOTE_TITLE, title);
        values.put(DBHelper.COLUMN_NOTE_NOTE, note);
        values.put(DBHelper.COLUMN_NOTE_FOLDER_ID, folder);
        return database.insert(DBHelper.TABLE_NAME_NOTE, null, values);
    }

    public Note getNote(int id) {
        Note tmpNote = new Note();
        String restrict = DBHelper.COLUMN_NOTE_ID + "=" + id;
        Cursor cursor = database.query(true, DBHelper.TABLE_NAME_NOTE, null,
                restrict, null, null, null, null, null);
        if (cursor != null && !cursor.isAfterLast()) {
            cursor.moveToFirst();
            tmpNote.setId(cursor.getInt(0));
            tmpNote.setTitle(cursor.getString(1));
            tmpNote.setNote(cursor.getString(2));
            tmpNote.setTimestamp(cursor.getString(3));
            tmpNote.setFolder(cursor.getString(4));
            cursor.close();
            return tmpNote;
        } else {
            return null;
        }
    }

    public long updateNoteNote(String noteId, String newNote) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NOTE_NOTE, newNote);
        String restrict = DBHelper.COLUMN_NOTE_ID + "=" + noteId;
        return database.update(DBHelper.TABLE_NAME_NOTE, values, restrict, null);
    }

    public long updateNoteFolder(String noteId, String newFolder) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NOTE_FOLDER_ID, newFolder);
        String restrict = DBHelper.COLUMN_NOTE_ID + "=" + noteId;
        return database.update(DBHelper.TABLE_NAME_NOTE, values, restrict, null);
    }

    public ArrayList<Note> getAllNotes(String currentFolder) {
        ArrayList<Note> allNotes = new ArrayList<Note>();
        String selection = DBHelper.COLUMN_NOTE_FOLDER_ID + "='" + currentFolder + "'";
        Cursor cursor = database.query(DBHelper.TABLE_NAME_NOTE, null, selection, null,
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note tmpNote = new Note();
            tmpNote.setId(cursor.getInt(0));
            tmpNote.setTitle(cursor.getString(1));
            tmpNote.setNote(cursor.getString(2));
            tmpNote.setTimestamp(cursor.getString(3));
            tmpNote.setFolder(cursor.getString(4));
            allNotes.add(tmpNote);
            cursor.moveToNext();
        }
        return allNotes;
    }

    public long removeNote(String noteId) {
        return database.delete(DBHelper.TABLE_NAME_NOTE, DBHelper.COLUMN_NOTE_ID + "=" + noteId,
                null);
    }
}
