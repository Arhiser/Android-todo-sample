package com.arhiser.todosample.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import com.arhiser.todosample.app.model.List;
import com.arhiser.todosample.app.model.Record;
import com.arhiser.todosample.app.provider.contract.ListContract;
import com.arhiser.todosample.app.provider.contract.RecordContract;
import com.arhiser.todosample.app.provider.controller.ListController;
import com.arhiser.todosample.app.provider.controller.RecordController;
import com.arhiser.todosample.app.ui.adapter.ItemListAdapter;
import com.arhiser.todosample.app.ui.adapter.ListSpinnerAdapter;


public class MainActivity extends Activity {

    private static final int LOADER_LIST = 1;
    private static final int LOADER_ITEMS = 2;

    private static final String KEY_LIST_ID = "list_id";
    private static final String KEY_HIDE_COMPLETED = "hide_completed";

    ListSpinnerAdapter listSpinnerAdapter;
    ItemListAdapter itemListAdapter;
    CheckBox checkBox;
    private List selectedList;
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listSpinnerAdapter = new ListSpinnerAdapter(this);
        itemListAdapter = new ItemListAdapter(this, itemListListener);

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(listSpinnerAdapter, onNavigationListener);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xff7070a0));

        getLoaderManager().restartLoader(LOADER_LIST, null, loaderCallback);

        findViewById(R.id.delete_list).setOnClickListener(onClickListener);
        itemList = (ListView)findViewById(R.id.task_list_view);
        itemList.setAdapter(itemListAdapter);
        checkBox = (CheckBox) findViewById(R.id.show_completed);
        checkBox.setOnClickListener(onClickListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_list:
                addNewList();
                return true;
            case R.id.action_add_record:
                addNewRecord();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewRecord() {
        if (selectedList == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(MainActivity.this);
        builder.setTitle("Add")
                .setMessage("Add new record")
                .setView(editText)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RecordController.getInstance().update(MainActivity.this, new Record(editText.getText().toString(), selectedList.id));
                        reloadItems();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        }).show();
    }

    private void addNewList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(MainActivity.this);
        builder.setTitle("Add")
                .setMessage("Add new list")
                .setView(editText)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ListController.getInstance().update(MainActivity.this, new List(editText.getText().toString()));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        }).show();
    }

    private void reloadItems() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LIST_ID, selectedList.id);
        bundle.putBoolean(KEY_HIDE_COMPLETED, checkBox.isChecked());
        getLoaderManager().restartLoader(LOADER_ITEMS, bundle, loaderCallback);
    }

    ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            selectedList = ListController.getInstance().getEntities().get(itemPosition);
            reloadItems();
            return false;
        }
    };

    LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            switch (id) {
                case LOADER_LIST:
                    cursorLoader = new CursorLoader(MainActivity.this, ListContract.getInstance().getContentUri(), ListContract.getInstance().getColumns(), null, null, null);
                    break;
                case LOADER_ITEMS:
                    boolean hideCompleted = args.getBoolean(KEY_HIDE_COMPLETED);
                    if (hideCompleted) {
                        cursorLoader = new CursorLoader(
                                MainActivity.this,
                                RecordContract.LIST_CONTENT_URI.buildUpon().appendPath(args.getString(KEY_LIST_ID)).build(),
                                ListContract.getInstance().getColumns(),
                                RecordContract.Entry.COLUMN_NAME_COMPLETED + "=?", new String[]{"0"}, null
                        );
                    } else {
                        cursorLoader = new CursorLoader(
                                MainActivity.this,
                                RecordContract.LIST_CONTENT_URI.buildUpon().appendPath(args.getString(KEY_LIST_ID)).build(),
                                ListContract.getInstance().getColumns(), null, null, null
                        );
                    }
                    break;
            }
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case LOADER_LIST:
                    listSpinnerAdapter.swapCursor(data);
                    break;
                case LOADER_ITEMS:
                    itemListAdapter.swapCursor(data);
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            switch (loader.getId()) {
                case LOADER_LIST:
                    listSpinnerAdapter.swapCursor(null);
                    break;
                case LOADER_ITEMS:
                    itemListAdapter.swapCursor(null);
                    break;
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.delete_list:
                    if (selectedList != null) {
                        ListController.getInstance().delete(MainActivity.this, selectedList);
                        itemListAdapter.getCursor().close();
                        itemListAdapter.swapCursor(null);
                        selectedList = null;
                    }
                    break;
                case R.id.show_completed:
                    if (selectedList != null) {
                        reloadItems();
                    }
                    break;
            }
        }
    };

    ItemListAdapter.ItemListListener itemListListener = new ItemListAdapter.ItemListListener() {
        @Override
        public void onItemCompletedChange(String itemId, boolean completed) {
            Record record = RecordController.getInstance().getEntityById(itemId);
            record.completed = completed;
            RecordController.getInstance().update(MainActivity.this, record);
            reloadItems();
        }

        @Override
        public void onItemDeleted(String itemId) {
            Record record = RecordController.getInstance().getEntityById(itemId);
            RecordController.getInstance().delete(MainActivity.this, record);
            reloadItems();
        }
    };
}
