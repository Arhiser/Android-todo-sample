package com.arhiser.todosample.app.model;

import java.util.ArrayList;

/**
 * Created by SER on 06.12.2014.
 */
public class List extends Entity {

    public String name;
    public ArrayList<Record> records;

    public List() {
    }

    public List(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public List(String name) {
        this.name = name;
    }
}
