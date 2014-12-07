package com.arhiser.todosample.app.provider.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.arhiser.todosample.app.model.List;

/**
 * Created by SER on 06.12.2014.
 */
public class ListContract extends Contract<List> {
    private static final ListContract INSTANCE = new ListContract();

    private ListContract() {
    }

    public static ListContract getInstance() {
        return INSTANCE;
    }

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(Entry.TABLE_NAME).build();
    public static final String CONTENT_DIR_TYPE = BASE_DIR_CONTENT_TYPE + Entry.TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = BASE_ITEM_CONTENT_TYPE + Entry.TABLE_NAME;

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + TYPE_INTEGER + TYPE_PK + COMMA_SEP +
                    Entry.COLUMN_NAME_NAME + TYPE_TEXT + ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public String getTableName() {
        return Entry.TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return Entry.ALL_COLUMN_NAMES;
    }

    @Override
    public Mapper<List> getMapper() {
        return ListMapper.INSTANCE;
    }

    public static class Entry implements Contract.Entry {
        public static final String TABLE_NAME = "list";

        public static final String COLUMN_NAME_NAME = "name";

        public static final String[] ALL_COLUMN_NAMES = {
                Entry._ID,
                Entry.COLUMN_NAME_NAME
        };
    }

    public static class ListMapper extends Mapper<List> {

        private static final ListMapper INSTANCE = new ListMapper();

        @Override
        public List newEntity() {
            return new List();
        }

        @Override
        public ContentValues toContentValues(List entity) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Entry._ID, entity.id);
            setStringValue(contentValues, Entry.COLUMN_NAME_NAME, entity.name);
            return contentValues;
        }

        @Override
        public List fromCursor(Cursor cursor) {
            return new List(cursor.getString(0), cursor.getString(1));
        }
    }
}
