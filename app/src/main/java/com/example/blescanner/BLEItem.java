// Trent Julich ~ 17 June 2021

package com.example.blescanner;

/**
 * Model for a single BLE device.
 */
public class BLEItem {

    private String deviceName;
    private int rssi;
    private boolean isConnectable;
    private String address;

    public BLEItem(String deviceName, int rssi, boolean isConnectable, String address) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.isConnectable = isConnectable;
        this.address = address;
    }

    public String getName() {
        return deviceName;
    }

    public int getRssi() {
        return rssi;
    }

    public String getConnectable() {
        if (isConnectable) {
            return "Connectable";
        }
        return "Not Connectable";
    }

    public String getAddress() {
        return address;
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
}
