package com.matters.ble.library.assertions;

import com.matters.ble.library.manager.BLEManager;

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

public class BLEManagerAssert extends GenericAssert<BLEManagerAssert, BLEManager> {

    public static BLEManagerAssert assertThat(final BLEManager manager) {
        return new BLEManagerAssert(manager);
    }

    private BLEManagerAssert(BLEManager actual) {
        super(BLEManagerAssert.class, actual);
    }

    public BLEManagerAssert isConnected() {
        isNotNull();

        Assertions.assertThat(actual.isConnected()).isTrue();
        return myself;
    }

    public BLEManagerAssert isDisconnected() {
        isNotNull();

        Assertions.assertThat(actual.isConnected()).isFalse();
        return myself;
    }
}
