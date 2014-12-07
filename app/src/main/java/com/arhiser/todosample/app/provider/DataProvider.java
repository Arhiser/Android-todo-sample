package com.arhiser.todosample.app.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.arhiser.todosample.app.provider.contract.Contract;
import com.arhiser.todosample.app.provider.contract.ListContract;
import com.arhiser.todosample.app.provider.contract.RecordContract;
import com.arhiser.todosample.app.provider.controller.ListController;
import com.arhiser.todosample.app.provider.controller.RecordController;

/**
 * Created by SER on 06.12.2014.
 */
public class DataProvider extends ContentProvider {

    public static final String AUTHORITY = "com.arhiser.todosample";

    private static final int LISTS = 1;
    private static final int LIST = 2;
    private static final int RECORDS_BY_LIST = 3;
    private static final int RECORD = 4;
    private static final int RECORDS = 5;

    DatabaseHelper dbHelper;

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        matcher.addURI(AUTHORITY,"list", LISTS);
        matcher.addURI(AUTHORITY,"list/*", LIST);
        matcher.addURI(AUTHORITY,"records/*", RECORDS_BY_LIST);
        matcher.addURI(AUTHORITY,"record/*", RECORD);
        matcher.addURI(AUTHORITY,"record", RECORDS);
    }

    private static Contract getContract(int matchedId) {
        switch (matchedId) {
            case LISTS:
            case LIST:
                return ListContract.getInstance();
            case RECORDS_BY_LIST:
            case RECORD:
            case RECORDS:
                return RecordContract.getInstance();
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = dbHelper.getDatabase();
        ContentResolver contentResolver = getContext().getContentResolver();
        int matchedId = matcher.match(uri);
        Contract contract = getContract(matchedId);
        switch(matchedId) {
            case LISTS:
                cursor = db.query(contract.getTableName(), contract.getColumns(), null, null, null, null, null);
                break;
            case LIST:
                cursor = db.query(contract.getTableName(), contract.getColumns(), Contract.Entry._ID + " = ?", new String[] {uri.getLastPathSegment()}, null, null, null);
                break;
            case RECORDS_BY_LIST:
                if(selection == null) {
                    cursor = db.query(contract.getTableName(), contract.getColumns(), RecordContract.Entry.COLUMN_NAME_LIST_ID + " = ?", new String[]{uri.getLastPathSegment()}, null, null, null);
                } else {
                    cursor = db.query(contract.getTableName(), contract.getColumns(), RecordContract.Entry.COLUMN_NAME_LIST_ID + " = ? AND " + RecordContract.Entry.COLUMN_NAME_COMPLETED + "=?",
                            new String[]{uri.getLastPathSegment(), "0"}, null, null, null);
                }
                break;
            case RECORD:
                cursor = db.query(contract.getTableName(), contract.getColumns(), Contract.Entry._ID + " = ?", new String[] {uri.getLastPathSegment()}, null, null, null);
                break;
            case RECORDS:
                cursor = db.query(contract.getTableName(), contract.getColumns(), null, null, null, null, null);
        }
        cursor.setNotificationUri(contentResolver, uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = matcher.match(uri);
        switch (match) {
            case LISTS:
                return ListContract.CONTENT_DIR_TYPE;
            case LIST:
                return ListContract.CONTENT_ITEM_TYPE;
            case RECORDS_BY_LIST:
                return RecordContract.CONTENT_DIR_TYPE;
            case RECORD:
                return RecordContract.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int matched = matcher.match(uri);
        final Contract contract = getContract(matched);
        final ContentResolver contentResolver = getContext().getContentResolver();
        SQLiteDatabase db = dbHelper.getDatabase();
        Long id = db.insert(contract.getTableName(), null, values);
        contentResolver.notifyChange(contract.getContentUri(), null);
        contentResolver.notifyChange(uri, null);
        if(matched == RECORD) {
            contentResolver.notifyChange(RecordContract.LIST_CONTENT_URI.buildUpon().appendPath(values.getAsString(RecordContract.Entry.COLUMN_NAME_LIST_ID)).build(), null);
        }
        return contract.getContentUri().buildUpon().appendPath(Long.toString(id)).build();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int matched = matcher.match(uri);
        final Contract contract = getContract(matched);
        final ContentResolver contentResolver = getContext().getContentResolver();
        SQLiteDatabase db = dbHelper.getDatabase();
        int affected = 0;
        switch (matched) {
            case LISTS:
                affected = db.delete(ListContract.getInstance().getTableName(), null, null);
                db.delete(RecordContract.getInstance().getTableName(), null, null);
                contentResolver.notifyChange(ListContract.CONTENT_URI, null);
                contentResolver.notifyChange(RecordContract.CONTENT_URI, null);
                break;
            case LIST:
                affected = db.delete(contract.getTableName(), Contract.Entry._ID + " = ?", new String[]{uri.getLastPathSegment()});
                contentResolver.notifyChange(ListContract.CONTENT_URI, null);
            case RECORDS_BY_LIST:
                affected = db.delete(RecordContract.getInstance().getTableName(), RecordContract.Entry.COLUMN_NAME_LIST_ID + "=?", new String[]{uri.getLastPathSegment()});
                contentResolver.notifyChange(RecordContract.CONTENT_URI, null);
                break;
            case RECORD:
                affected = db.delete(contract.getTableName(), Contract.Entry._ID + " = ?", new String[]{uri.getLastPathSegment()});
                contentResolver.notifyChange(RecordContract.CONTENT_URI, null);
                break;
        }
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int matched = matcher.match(uri);
        final Contract contract = getContract(matched);
        final ContentResolver contentResolver = getContext().getContentResolver();
        SQLiteDatabase db = dbHelper.getDatabase();
        db.update(contract.getTableName(), values, Contract.Entry._ID + "=?", new String[]{values.getAsString(Contract.Entry._ID)});
        contentResolver.notifyChange(contract.getContentUri(), null);
        contentResolver.notifyChange(uri, null);
        return 1;
    }
}
