package com.matters.ble.library;


import android.os.Build;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class LibraryRobolectrictTestRunner extends RobolectricTestRunner {
    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = Build.VERSION_CODES.JELLY_BEAN_MR2;

    public LibraryRobolectrictTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String projectDirectory = System.getProperty("user.dir");
        return new AndroidManifest(Fs.fileFromPath(projectDirectory + TestDataProvider.LIBRARY_ANDROID_MANIFEST_PATH),
                                   Fs.fileFromPath(projectDirectory + TestDataProvider.APP_RES_PATH),
                                   Fs.fileFromPath(projectDirectory + TestDataProvider.APP_ASSETS_PATH)) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }
        };
    }

    
}