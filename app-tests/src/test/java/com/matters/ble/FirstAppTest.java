package com.matters.ble;

import android.os.Build;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR2, manifest = Config.NONE)
public class FirstAppTest {

    @Test
    public void dummyTest() {
        Assertions.assertThat(true).isTrue();
    }

}
