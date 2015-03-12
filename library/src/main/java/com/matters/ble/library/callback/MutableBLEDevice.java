package com.matters.ble.library.callback;

import android.bluetooth.BluetoothDevice;

import com.google.common.base.Joiner;
import com.matters.ble.library.device.BLEDevice;

/**
 * The type Mutable bLE device impl.
 */
final class MutableBLEDevice implements BLEDevice {

    private final BluetoothDevice bluetoothDevice;

    private final byte[] scanResponse;

    private final int hashCode;

    private int rssi;

    /**
     * Instantiates a new BLE device.
     *
     * @param bluetoothDevice the bluetooth device
     * @param scanResponse the scan respnse
     * @param hashCode the device hash code
     */
    public MutableBLEDevice(BluetoothDevice bluetoothDevice, byte[] scanResponse, int hashCode) {
        this.bluetoothDevice = bluetoothDevice;
        this.scanResponse = scanResponse;
        this.hashCode = hashCode;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    @Override
    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return bluetoothDevice.getName();
    }

    /**
     * Get scan response.
     *
     * @return the byte [ ]
     */
    @Override
    public byte[] getScanResponse() {
        return scanResponse;
    }

    /**
     * Sets rssi.
     *
     * @param rssi the rssi
     */
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    /**
     * Gets rssi.
     *
     * @return the rssi
     */
    @Override
    public int getRssi() {
        return rssi;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return Joiner.on(" ").join("BLEDevice [", getName(), ", ", getAddress(), " ", "]");
    }
}
