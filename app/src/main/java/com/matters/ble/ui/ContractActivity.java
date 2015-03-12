package com.matters.ble.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

abstract class ContractActivity<T> extends ActionBarActivity {

    private T contract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            contract = (T) getApplication();
        } catch(ClassCastException e) {
            throw new IllegalStateException(String.format("Application does not implement %s's contract", getClass().getSimpleName()));
        }
    }

    @Override
    protected void onDestroy() {
        contract = null;
        super.onDestroy();
    }

    public final T getContract() {
        return contract;
    }
}
