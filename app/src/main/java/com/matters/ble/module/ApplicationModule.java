package com.matters.ble.module;

import com.matters.ble.App;
import com.matters.ble.library.manager.BLEManager;
import com.matters.ble.ui.MainActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class
        }
)
public final class ApplicationModule {

    private final App app;

    public ApplicationModule(final App app) {
        this.app = app;
    }

    @Provides public BLEManager provideBLEManager() {
        return BLEManager.newInstance(app);
    }

}
