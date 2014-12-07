package com.arhiser.todosample.app;

import android.app.Application;
import com.arhiser.todosample.app.provider.controller.ListController;
import com.arhiser.todosample.app.provider.controller.RecordController;

/**
 * Created by SER on 06.12.2014.
 */
public class TodoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RecordController.getInstance().reload(this);
        ListController.getInstance().reload(this);
    }
}
