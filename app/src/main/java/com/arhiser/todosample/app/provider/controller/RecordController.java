package com.arhiser.todosample.app.provider.controller;

import android.content.Context;
import com.arhiser.todosample.app.model.Record;
import com.arhiser.todosample.app.provider.contract.Contract;
import com.arhiser.todosample.app.provider.contract.RecordContract;

import java.util.ArrayList;

/**
 * Created by SER on 06.12.2014.
 */
public class RecordController extends EntityController<Record> {

    private static RecordController INSTANCE = new RecordController();

    private RecordController() {
    }

    public static RecordController getInstance() {
        return INSTANCE;
    }

    @Override
    protected Contract<Record> getContract() {
        return RecordContract.getInstance();
    }

    public ArrayList<Record> getRecordByListId(String listId) {
        ArrayList<Record> records = new ArrayList<Record>();
        for(Record record: cache) {
            if (record.listId.equals(listId)) {
                records.add(record);
            }
        }
        return records;
    }

    public void setCompletedState(Context context, String recordId, boolean completed) {
        Record record = getEntityById(recordId);
        record.completed = completed;
        update(context, record);
    }
}
