// Trent Julich ~ 17 June 2021

package com.example.blescanner;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import java.util.Comparator;

/**
 * Model for a single BLE device.
 */
public class BLEItem implements Comparable<BLEItem> {

    private String mDeviceName;
    private int mRssi;
    private boolean mIsConnectable;
    private String mAddress;
    private BluetoothDevice mDevice;

    public BLEItem(String deviceName, int rssi, boolean isConnectable, String address, BluetoothDevice device) {
        mDeviceName = deviceName;
        mRssi = rssi;
        mIsConnectable = isConnectable;
        mAddress = address;
        mDevice = device;
    }

    public String getName() {
        return mDeviceName;
    }

    public int getRssi() {
        return mRssi;
    }

    public boolean getConnectable() {
        return mIsConnectable;
    }

    public String getAddress() {
        return mAddress;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BLEItem)) {
            return false;
        }

        BLEItem otherItem = (BLEItem) o;
        return this.getAddress().equals(otherItem.getAddress());
    }

    @Override
    public int compareTo(BLEItem o) {
        return mAddress.compareTo(o.getAddress());
    }

    public static class RSSIComparator implements Comparator<BLEItem> {
        public int compare(BLEItem first, BLEItem second) {
            return Integer.compare(second.getRssi(), first.getRssi());
        }
    }
}
