package com.matters.ble.library.manager;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.matters.ble.library.device.BLEDevice;

/**
 * The type BLE manager.
 */
public class BLEManager {

    /**
     * The constant TAG.
     */
    public static final String TAG = BLEManager.class.getSimpleName();

    private Context context;

    private ServiceConnection serviceConnection;

    private Messenger serviceMessenger;

    private BluetoothManager systemBluetoothManager;

    private Configuration.Builder configurationBuilder = new Configuration.Builder();

    private Configuration configuration = Configuration.NULL;

    private State state = State.IDLE;

    /**
     * New instance.
     *\
     * @param context the context
     * @return the bLE manager
     */
    public static BLEManager newInstance(final Context context) {
        return new BLEManager(context);
    }

    private BLEManager(final Context context) {
        this.context = context;
        this.systemBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    /**
     * Sets ranging listener.
     *
     * @param rangingListener the ranging listener
     */
    public void setRangingListener(final RangingListener rangingListener) {
        Preconditions.checkNotNull(rangingListener, "RangingListener is null");
        configurationBuilder.setRangingListener(rangingListener);
    }

    /**
     * Connect void.
     *\
     * @param callback the callback
     */
    public synchronized void connect(final ServiceBoundCallback callback) {
        try {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    configuration = configurationBuilder.build();
                    configurationBuilder.clear();
                    BLEService service = ((BLEService.ServiceBinder) binder).getServiceInstance();
                    service.setConfiguration(configuration);
                    serviceMessenger = new Messenger(service.getMessagingHandler());
                    try {
                        callback.onServiceBound();
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Unknown error occured");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            Preconditions.checkState(context.bindService(new Intent(context, BLEService.class), serviceConnection, Context.BIND_AUTO_CREATE),
                                     "Could not connect to manager's service");
        } catch (IllegalStateException e) {
            serviceConnection = null;
            throw e;
        }
    }

    public synchronized boolean isConnected() {
        return serviceConnection != null;
    }

    /**
     * Disconnect void.
     */
    public synchronized void disconnect() {

        if(! isConnected()) {
            Log.d(TAG, "Manager already disconnected.");
            return;
        }

        try {
            serviceMessenger.send(Message.obtain(null, BLEService.MESSAGE_DISCONNECT));
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            context.unbindService(serviceConnection);
            context = null;
            serviceConnection = null;
            configuration = Configuration.NULL;
            serviceMessenger = null;
        }
    }

    /**
     * Start ranging.
     */
    public synchronized void startRanging() throws RemoteException {
        if(isConnected() && isIdle()) {
            Preconditions.checkNotNull(serviceMessenger, "BLEManager not connected");
            serviceMessenger.send(Message.obtain(null, BLEService.MESSAGE_START_RANGING));
        } else {
            Log.d(TAG, "either disconnected or in Idle state");
        }
    }

    /**
     * Stop ranging.
     */
    public synchronized void stopRanging() throws RemoteException {

        if(!isRanging()) {
            Log.d(TAG, "not ranging");
        }

        Preconditions.checkNotNull(serviceMessenger, "BLEManager not connected");
        serviceMessenger.send(Message.obtain(null, BLEService.MESSAGE_STOP_RANGING));
    }

    /**
     * Is idle.
     *
     * @return the boolean
     */
    public synchronized boolean isIdle() {
        return state == State.IDLE;
    }

    /**
     * Is ranging.
     *
     * @return the boolean
     */
    public synchronized boolean isRanging() {
        return state == State.RANGING;
    }

    /**
     * Is bluetooth le supported.
     *
     * @return the boolean
     */
    public boolean isBluetoothLeSupported() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Checks whether the Bluetooth is enabled on the device.
     *
     * @return the boolean indicating whether the Bluetooth is enabled.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isBluetoothEnabled() {
        return systemBluetoothManager.getAdapter().isEnabled();
    }


    /**
     * Set state.
     *
     * @param state the state
     */
    void setState(State state){
        this.state = state;
    }

    private enum State {
        IDLE,
        RANGING
    }

    /**
     * The interface Ranging listener.
     */
    public interface RangingListener {

        /**
         * The constant NULL.
         */
        public static final RangingListener NULL = new RangingListener() {
            @Override
            public void onDeviceFound(BLEDevice bleDevice) {

            }
        };

        /**
         * On device found.
         * @param bleDevice the ble device
         */
        void onDeviceFound(BLEDevice bleDevice);
    }

    /**
     * The interface Service bound callback.
     */
    public interface ServiceBoundCallback {
        /**
         * On service bound.
         */
        void onServiceBound() throws RemoteException;
    }

    /**
     * The type Configuration.
     */
    static class Configuration {

        /**
         * The NULL.
         */
        static final Configuration NULL = new Builder()
                                                .setRangingListener(RangingListener.NULL).build();

        /**
         * The Ranging listener.
         */
        final RangingListener rangingListener;

        private Configuration(Builder builder) {
            this.rangingListener = builder.rangingListener;
        }

        /**
         * The type Builder.
         */
        public static final class Builder {

            private RangingListener rangingListener = RangingListener.NULL;

            /**
             * Sets ranging listener.
             *
             * @param rangingListener the ranging listener
             * @return the ranging listener
             */
            Builder setRangingListener(RangingListener rangingListener) {
                this.rangingListener = rangingListener;
                return this;
            }

            /**
             * Build configuration.
             *
             * @return the configuration
             */
            Configuration build() {
                return new Configuration(this);
            }

            /**
             * Clear void.
             */
            void clear() {
                rangingListener = null;
            }
        }
    }

}
