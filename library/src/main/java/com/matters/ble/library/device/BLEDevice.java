package com.matters.ble.library.device;

/**
 * The interface BLE device.
 */
public interface BLEDevice {

    /**
     * Gets rssi.
     *
     * @return the rssi
     */
    int getRssi();

    /**
     * Gets address.
     *
     * @return the address
     */
    String getAddress();

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Get scan response.
     *
     * @return the byte [ ]
     */
    byte[] getScanResponse();
}
