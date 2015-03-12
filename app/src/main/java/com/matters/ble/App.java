package com.matters.ble;

import android.app.Application;

import com.matters.ble.module.ApplicationModule;
import com.matters.ble.ui.MainActivity;

import dagger.ObjectGraph;

public class App extends Application implements MainActivity.Contract {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new ApplicationModule(this));
    }

    @Override
    public void injectDependencies(Object dependencyHolder) {
        objectGraph.inject(dependencyHolder);
    }
}
