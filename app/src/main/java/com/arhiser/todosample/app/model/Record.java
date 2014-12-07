package com.arhiser.todosample.app.model;

/**
 * Created by SER on 06.12.2014.
 */
public class Record extends Entity {

    public String listId;
    public String text;
    public boolean completed;

    public Record() {
    }

    public Record(String id, String listId, String text, boolean completed) {
        this.id = id;
        this.listId = listId;
        this.text = text;
        this.completed = completed;
    }

    public Record(String text, String listId) {
        this.text = text;
        this.listId = listId;
        this.completed = false;
    }
}
