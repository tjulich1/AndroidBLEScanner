// Trent Julich ~ 17 June 2021

package com.example.blescanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1;

    private String[] PERMISSIONS = {
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    };

    private ActivityResultLauncher<String> requestLocationLauncher;
    private ActivityResultLauncher<String> requestBluetoothLauncher;

    ArrayList<BLEItem> mDevices;
    BLEAdapter mDeviceListAdapter;
    BluetoothAdapter mAdapter;
    BluetoothManager mManager;
    BluetoothLeScanner mBLEScanner;

    // Controls for starting/stopping scans.
    Button mStartButton;
    Button mStopButton;
    ToggleButton mSortButton;

    /**
     * Requests all necessary permissions for a ble scan.
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
    }

    /**
     * Checks that all permissions for a ble scan are granted.
     *
     * @return True if all are granted, false otherwise.
     */
    private boolean checkPermissions() {
        boolean hasAll = true;
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION ERROR", "PERMISSION NOT GRANTED: " + permission);
                hasAll = false;
                break;
            }
        }
        return hasAll;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);

        // Initialize permission launcher for location services.
        requestLocationLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        });

        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        RecyclerView deviceRecycler = (RecyclerView) findViewById(R.id.rvBLE);

        // Initialize BLE device list
        mDevices = new ArrayList<BLEItem>();

        // Create ble adapter using list of BLE devices.
        mDeviceListAdapter = new BLEAdapter(mDevices);

        // Attach the adapter to the recyclerview to populate items
        deviceRecycler.setAdapter(mDeviceListAdapter);

        // Set layout manager to position the items
        deviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize a bluetooth adapter, manager, and scanner.
        mManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = mManager.getAdapter();

        // Device does not support bluetooth.
        if (mAdapter == null) {

        // Bluetooth is not enabled.
        } else if (!mAdapter.isEnabled()) {
            Log.d("BLUETOOTH ERROR", "BLUETOOTH IS NOT ENABLED");
        } else {
            mBLEScanner = mAdapter.getBluetoothLeScanner();
            initializeButtons();
        }
    }

    /**
     * Callback that is used whenever a new BLE device is detected during a scan.
     */
    private ScanCallback deviceScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // Add device to list.
            Log.d("DEVICE FOUND", "Device name: " + result.getDevice().getName());
            mDevices.add(new BLEItem(result.getDevice().getName(), String.valueOf(result.getRssi()), result.isConnectable()));

            // Notify adapter of data change.
            mDeviceListAdapter.notifyDataSetChanged();
        }
    };

    private void initializeButtons() {
        mStartButton = (Button) findViewById(R.id.startScanButton);
        mStopButton = (Button) findViewById(R.id.stopScanButton);
        mSortButton = (ToggleButton) findViewById(R.id.sortToggleButton);

        mStartButton.setEnabled(true);
        mStopButton.setEnabled(false);

        // Tie buttons to onClick event functions.
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startScanning();
                    mStartButton.setEnabled(false);
                    mStopButton.setEnabled(true);
                } else {
                    requestPermissions();
                }
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanning();
                mStartButton.setEnabled(true);
            }
        });

        // Toggle button that when pressed, will sort discovered devices by rssi.
        mSortButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDevices.sort(new RSSIComparator());
                    mDeviceListAdapter.notifyDataSetChanged();
                } else {

                }
            }
        });
    }

    public void startScanning() {
        mDevices.clear();
        mDeviceListAdapter.notifyDataSetChanged();
        AsyncTask.execute(new Runnable() {
           @Override
           public void run() {
               mBLEScanner.startScan(deviceScanCallBack);
           }
        });
    }

    public void stopScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mBLEScanner.stopScan(deviceScanCallBack);
            }
        });
    }
}