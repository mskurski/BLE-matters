package com.matters.ble.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.matters.ble.Utils;
import com.matters.ble.library.device.BLEDevice;
import com.matters.ble.library.manager.BLEManager;

import javax.inject.Inject;

public class MainActivity extends ContractActivity<MainActivity.Contract> implements BLEManager.RangingListener {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    @Inject
    BLEManager bleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContract().injectDependencies(this);
        bleManager.setRangingListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!bleManager.isBluetoothEnabled()) {
            enableBluetoothAndStartRanging();
        } else if(bleManager.isConnected()) {
            startRanging();
        } else {
            connectAndStartRanging();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if(resultCode == Activity.RESULT_OK) {
                connectAndStartRanging();
            } else {
                Utils.showToast(this, "Bluetooth not enabled");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRanging();
    }

    @Override
    protected void onDestroy() {
        bleManager.disconnect();
        super.onDestroy();
    }

    @Override
    public void onDeviceFound(BLEDevice bleDevice) {
        Utils.showToast(this, bleDevice.toString());
    }

    private void connectAndStartRanging() {
        bleManager.connect(new BLEManager.ServiceBoundCallback() {
            @Override
            public void onServiceBound() throws RemoteException {
                bleManager.startRanging();
            }
        });
    }

    private void enableBluetoothAndStartRanging() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    private void startRanging() {
        try {
            bleManager.startRanging();
        } catch (RemoteException e) {
            Utils.showToast(this, e.getMessage());
        }
    }

    private void stopRanging() {
        try {
            bleManager.stopRanging();
        } catch (RemoteException e) {
            Utils.showToast(this, e.getMessage());
        }
    }

    public interface Contract {
        void injectDependencies(Object object);
    }

}
