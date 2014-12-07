package com.arhiser.todosample.app.provider.controller;

import com.arhiser.todosample.app.model.List;
import com.arhiser.todosample.app.provider.contract.Contract;
import com.arhiser.todosample.app.provider.contract.ListContract;

/**
 * Created by SER on 06.12.2014.
 */
public class ListController extends EntityController<List> {

    private static ListController INSTANCE = new ListController();

    private ListController() {
    }

    public static ListController getInstance() {
        return INSTANCE;
    }

    @Override
    protected Contract<List> getContract() {
        return ListContract.getInstance();
    }
}
