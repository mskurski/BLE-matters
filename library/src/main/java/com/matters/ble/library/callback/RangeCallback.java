package com.matters.ble.library.callback;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.matters.ble.library.device.BLEDevice;
import com.matters.ble.library.manager.BLEManager;
import com.matters.ble.library.util.HashCodeBuilder;
import com.matters.ble.library.util.LimitedLinkedHashMap;

/**
 * The type Range callback.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RangeCallback implements BluetoothAdapter.LeScanCallback {

    private static final int CACHE_SIZE = 20;

    private final LimitedLinkedHashMap<Integer, MutableBLEDevice> cache =
                                                    new LimitedLinkedHashMap<Integer, MutableBLEDevice>(CACHE_SIZE);
    private final BLEManager.RangingListener rangingListener;

    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    /**
     * Instantiates a new Range callback.
     *
     * @param rangingListener the ranging listener
     */
    public RangeCallback(BLEManager.RangingListener rangingListener) {
        this.rangingListener = rangingListener;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        final int hashCode = HashCodeBuilder.init()
                                            .append(device.getAddress())
                                            .append(device.getName())
                                            .append(device.getType())
                                            .append(device.getBondState())
                                            .append(scanRecord)
                                            .build();

        MutableBLEDevice bleDevice = cache.get(hashCode);

        if(bleDevice == null) {
            bleDevice = new MutableBLEDevice(device, scanRecord, hashCode);
            cache.put(hashCode, bleDevice);
        }

        bleDevice.setRssi(rssi);

        final BLEDevice result = bleDevice;

        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                rangingListener.onDeviceFound(result);
            }
        });
    }
}
