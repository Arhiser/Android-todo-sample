package com.arhiser.todosample.app.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.arhiser.todosample.app.R;

/**
 * Created by SER on 06.12.2014.
 */
public class ListSpinnerAdapter extends CursorAdapter {


    public ListSpinnerAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
        view.setTag(cursor.getInt(0));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView)view).setText(cursor.getString(1));
    }
}
