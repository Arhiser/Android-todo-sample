package com.arhiser.todosample.app.provider.contract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.arhiser.todosample.app.model.Entity;

/**
 * Created by SER on 06.12.2014.
 */
abstract public class Contract<E extends Entity> {
    protected Contract() {
    }

    protected static final String TYPE_TEXT = " TEXT";
    protected static final String TYPE_INTEGER = " INTEGER";
    protected static final String TYPE_TIMESTAMP = " TIMESTAMP";
    protected static final String TYPE_PK = " PRIMARY KEY AUTOINCREMENT";
    protected static final String COMMA_SEP = ",";

    public static final String CONTENT_AUTHORITY = "com.arhiser.todosample";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String BASE_DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
    public static final String BASE_ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";

    public abstract Uri getContentUri();
    public abstract String getTableName();
    public abstract String[] getColumns();
    public abstract Mapper<E> getMapper();

    public interface Entry extends BaseColumns {

    }

    public abstract static class Mapper<E extends Entity> {

        public abstract E newEntity();

        public abstract ContentValues toContentValues(E entity);

        public abstract E fromCursor(Cursor cursor);

        protected static void setStringValue(ContentValues contentValues, String column, String value) {
            if (TextUtils.isEmpty(value)) {
                contentValues.putNull(column);
            } else {
                contentValues.put(column, value);
            }
        }
    }
}
