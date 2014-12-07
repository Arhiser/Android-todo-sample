package com.arhiser.todosample.app.provider.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.arhiser.todosample.app.model.Record;

/**
 * Created by SER on 06.12.2014.
 */
public class RecordContract extends Contract<Record> {

    private static final RecordContract INSTANCE = new RecordContract();

    private RecordContract() {
    }

    public static RecordContract getInstance() {
        return INSTANCE;
    }

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(Entry.TABLE_NAME).build();
    public static final Uri LIST_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("records").build();
    public static final String CONTENT_DIR_TYPE = BASE_DIR_CONTENT_TYPE + Entry.TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = BASE_ITEM_CONTENT_TYPE + Entry.TABLE_NAME;

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + TYPE_INTEGER + TYPE_PK + COMMA_SEP +
                    Entry.COLUMN_NAME_LIST_ID + TYPE_TEXT + COMMA_SEP +
                    Entry.COLUMN_NAME_TEXT + TYPE_TEXT + COMMA_SEP +
                    Entry.COLUMN_NAME_COMPLETED + TYPE_INTEGER + ")";

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
    public Mapper<Record> getMapper() {
        return RecordMapper.INSTANCE;
    }

    public static class Entry implements Contract.Entry {
        public static final String TABLE_NAME = "record";

        public static final String COLUMN_NAME_LIST_ID = "list_id";
        public static final String COLUMN_NAME_TEXT = "record_text";
        public static final String COLUMN_NAME_COMPLETED = "completed";

        public static final String[] ALL_COLUMN_NAMES = {
                Entry._ID,
                Entry.COLUMN_NAME_LIST_ID,
                Entry.COLUMN_NAME_TEXT,
                Entry.COLUMN_NAME_COMPLETED,
        };
    }

    public static class RecordMapper extends Mapper<Record> {

        private static final RecordMapper INSTANCE = new RecordMapper();

        @Override
        public Record newEntity() {
            return new Record();
        }

        @Override
        public ContentValues toContentValues(Record entity) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Entry._ID, entity.id);
            setStringValue(contentValues, Entry.COLUMN_NAME_LIST_ID, entity.listId);
            setStringValue(contentValues, Entry.COLUMN_NAME_TEXT, entity.text);
            contentValues.put(Entry.COLUMN_NAME_COMPLETED, entity.completed ? 1 : 0);
            return contentValues;
        }

        @Override
        public Record fromCursor(Cursor cursor) {
            return new Record(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3) == 1);
        }
    }
}
