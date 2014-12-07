package com.arhiser.todosample.app.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.arhiser.todosample.app.provider.contract.ListContract;
import com.arhiser.todosample.app.provider.contract.RecordContract;

/**
 * Created by SER on 06.12.2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_FILE = "todo.sqlite";

    public DatabaseHelper(Context context) {
        super(context, DB_FILE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ListContract.SQL_CREATE_ENTRIES);
        db.execSQL(RecordContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ListContract.SQL_DELETE_ENTRIES);
        db.execSQL(RecordContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    synchronized public SQLiteDatabase getDatabase() {
        return getWritableDatabase();
    }

}
