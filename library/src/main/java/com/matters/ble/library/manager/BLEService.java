package com.matters.ble.library.manager;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.matters.ble.library.callback.RangeCallback;

import static com.matters.ble.library.manager.BLEService.MESSAGE_START_RANGING;

/**
 * The type BLE service.
 */
public class BLEService extends Service {

    /**
     * The MESSAGE_START_RANGING.
     */
    static final int MESSAGE_START_RANGING = 1;

    /**
     * The MESSAGE_STOP_RANGING.
     */
    static final int MESSAGE_STOP_RANGING = 2;

    /**
     * The MESSAGE_DISCONNECT.
     */
    static final int MESSAGE_DISCONNECT = 3;

    private ServiceBinder serviceBinder;

    private Handler messagingHandler;
    private BLEManager.Configuration configuration = BLEManager.Configuration.NULL;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothAdapter.LeScanCallback callback;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceBinder = new ServiceBinder(this);
        messagingHandler = new MessagingHandler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    /**
     * Gets messaging handler.
     *
     * @return the messaging handler
     */
    Handler getMessagingHandler() {
        return messagingHandler;
    }

    /**
     * Sets configuration.
     *
     * @param configuration the configuration
     */
    void setConfiguration(BLEManager.Configuration configuration) {
        this.configuration = configuration;
    }

    BluetoothAdapter.LeScanCallback getCallback() {
        if (callback == null) {
            callback = new RangeCallback(configuration.rangingListener);
        }

        return callback;
    }

    void clearResources() {
        serviceBinder = null;
        messagingHandler = null;
        callback = null;
        configuration = BLEManager.Configuration.NULL;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void onRangingStart() {
        bluetoothAdapter.startLeScan(getCallback());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void onRangingStop() {
        bluetoothAdapter.stopLeScan(getCallback());
        callback = null;
    }

    private void onDisconnect() {
        clearResources();
    }

    /**
     * The type Service binder.
     */
    static class ServiceBinder extends Binder {
        private final BLEService service;

        private ServiceBinder(final BLEService service) {
            this.service = service;
        }

        /**
         * Gets service instance.
         *
         * @return the service instance
         */
        public BLEService getServiceInstance() {
            return service;
        }
    }

    private class MessagingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {

                case MESSAGE_START_RANGING:
                    onRangingStart();
                    break;

                case MESSAGE_STOP_RANGING:
                    onRangingStop();
                    break;

                case MESSAGE_DISCONNECT:
                    onDisconnect();
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported message with id: " + msg.what);
            }
        }
    }
}
