package com.arhiser.todosample.app.provider.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import com.arhiser.todosample.app.model.Entity;
import com.arhiser.todosample.app.provider.contract.Contract;

import java.util.ArrayList;

/**
 * Created by SER on 06.12.2014.
 */
abstract public class EntityController <E extends Entity> {
    protected ArrayList<E> cache = new ArrayList<E>();

    abstract protected Contract<E> getContract();

    public void reload(Context context) {
        cache.clear();
        ContentResolver contentResolver = context.getContentResolver();
        Contract<E> contract = getContract();
        Cursor cursor = contentResolver.query(contract.getContentUri(), contract.getColumns(), null, null, null);
        if(cursor.getCount() == 0) {
            return;
        }
        cursor.moveToFirst();
        do {
            cache.add(contract.getMapper().fromCursor(cursor));
        } while (cursor.moveToNext());
    }

    public void update(Context context, E entity) {
        ContentResolver contentResolver = context.getContentResolver();
        Contract<E> contract = getContract();
        if (entity.id == null) {
            insert(context, entity);
            reload(context);
            return;
        }
        E existingEntity = getEntityById(entity.id);
        if (existingEntity == null) {
            insert(context, entity);
            reload(context);
            return;
        }
        contentResolver.update(contract.getContentUri(), contract.getMapper().toContentValues(entity), Contract.Entry._ID + "=?", new String[]{entity.id});
        reload(context);
    }

    private void insert(Context context, E entity) {
        ContentResolver contentResolver = context.getContentResolver();
        Contract<E> contract = getContract();
        contentResolver.insert(contract.getContentUri(), contract.getMapper().toContentValues(entity));
    }

    public void delete(Context context, E entity) {
        ContentResolver contentResolver = context.getContentResolver();
        Contract<E> contract = getContract();
        contentResolver.delete(contract.getContentUri().buildUpon().appendPath(entity.id).build(), null, null);
        cache.remove(entity);
    }

    public ArrayList<E> getEntities() {
        return cache;
    }

    public E getEntityById(String id) {
        for(E entity: cache) {
            if (entity.id.equals(id)) {
                return entity;
            }
        }
        return null;
    }

}
