package com.example.blescanner;

public class BLEItem {

    private String deviceName;
    private String rssi;
    private boolean isConnectable;

    public BLEItem(String deviceName, String rssi, boolean isConnectable) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.isConnectable = isConnectable;
    }

    public String getName() {
        return deviceName;
    }

    public String getRssi() {
        return rssi;
    }

    public String getConnectable() {
        if (isConnectable) {
            return "Connectable";
        }
        return "Not Connectable";
    }

}
