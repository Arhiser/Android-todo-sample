package com.arhiser.todosample.app.ui.adapter;

import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.arhiser.todosample.app.R;

/**
 * Created by SER on 07.12.2014.
 */
public class ItemListAdapter extends CursorAdapter implements View.OnClickListener {

    private ItemListListener listener;

    public interface ItemListListener {
        void onItemCompletedChange(String itemId, boolean completed);
        void onItemDeleted(String itemId);
    }

    public ItemListAdapter(Context context, ItemListListener listener) {
        super(context, null, true);
        this.listener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ToggleButton completeToggle = (ToggleButton)view.findViewById(R.id.item_complete_toggle);
        TextView taskName = (TextView)view.findViewById(R.id.item_name);
        ImageView deleteButton = (ImageView)view.findViewById(R.id.item_delete);
        taskName.setText(cursor.getString(2));
        completeToggle.setChecked(cursor.getInt(3) == 1);
        deleteButton.setTag(cursor.getString(0));
        completeToggle.setTag(cursor.getString(0));
        deleteButton.setOnClickListener(this);
        completeToggle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_complete_toggle:
                ToggleButton tb = (ToggleButton)v;
                listener.onItemCompletedChange((String)v.getTag(), tb.isChecked());
                break;
            case R.id.item_delete:
                listener.onItemDeleted((String)v.getTag());
        }
    }
}
