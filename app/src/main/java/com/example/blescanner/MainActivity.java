// Trent Julich ~ 17 June 2021

package com.example.blescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Bluetooth low energy scanner application which discovers and lists nearby BLE devices. Allows
 * connecting to devices which allow connection.
 */
public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener{

    private static final int PERMISSION_CODE = 1;

    private static final String[] PERMISSIONS = {
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    };

    private ArrayList<BLEItem> mDevices;
    private BLEAdapter mDeviceListAdapter;
    private BluetoothAdapter mAdapter;
    private BluetoothLeScanner mBLEScanner;
    private BluetoothManager mManager;

    // Controls for starting/stopping scans.
    private Button mStartButton;
    private Button mStopButton;

    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);

        mScanning = false;

        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_CODE);

        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        RecyclerView deviceRecycler = findViewById(R.id.rvBLE);

        // Initialize BLE device list
        mDevices = new ArrayList<>();

        // Create ble adapter using list of BLE devices.
        mDeviceListAdapter = new BLEAdapter(mDevices, this);

        // Attach the adapter to the recyclerview to populate items
        deviceRecycler.setAdapter(mDeviceListAdapter);

        // Set layout manager to position the items
        deviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize a bluetooth manager
        mManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = mManager.getAdapter();

        // If mAdapter is null, bluetooth is not supported, so do not initialize scan controls.
        if (mAdapter != null) {
            initializeButtons();
        }
    }

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

    /**
     * Clears list of previously found devices, and begins a scan for unique BLE devices.
     */
    private void startScanning() {
        if (!mScanning) {
            mScanning = true;
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);

            mDevices.clear();
            mDeviceListAdapter.notifyDataSetChanged();
            AsyncTask.execute(() -> mBLEScanner.startScan(deviceScanCallBack));
        }
    }

    /**
     * Halts the current scan that is happening.
     */
    private void stopScanning() {
        if (mScanning) {
            mScanning = false;
            mStartButton.setEnabled(true);
            mStopButton.setEnabled(false);
            AsyncTask.execute(() -> mBLEScanner.stopScan(deviceScanCallBack));
        }
    }

    /**
     * Asks the user to enable bluetooth.
     */
    private void promptBluetooth() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage(getString(R.string.bt_prompt_title), getString(R.string.bt_prompt_message),
                    getString(R.string.def_confirm));
            }
        });
    }

    /**
     * Notify the user that they have succeeded in connecting to device at the given address.
     *
     * @param address The address of the device connected to.
     */
    private void notifyConnected(String address) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage(getString(R.string.con_title), getString(R.string.con_message, address),
                        getString(R.string.def_confirm));
            }
        });
    }

    /**
     * Used to construct and show a message to the user.
     *
     * @param title The title of the message to display.
     * @param message The main content of the message to display.
     * @param confirmMessage The text that should be placed in the "confirm" button.
     */
    private void showMessage(final String title, final String message, final String confirmMessage) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton(confirmMessage, (dialog1, which) -> {

        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    /**
     * Callback that is used whenever a new BLE device is detected during a scan.
     */
    private final ScanCallback deviceScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String name = device.getName() == null ? getString(R.string.no_name) : device.getName();

            BLEItem discoveredItem = new BLEItem(name, result.getRssi(), result.isConnectable(),
                    device.getAddress(), device);

            if (!mDevices.contains(discoveredItem)) {
                mDevices.add(discoveredItem);
            }

            // Notify adapter of data change.
            mDeviceListAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Initializes all of the buttons used to give the user control over scanning.
     */
    private void initializeButtons() {
        initStartButton();
        initStopButton();
        initSortButton();
    }

    private void initSortButton() {
        ToggleButton sortButton =  findViewById(R.id.sortToggleButton);

        // Toggle button that when pressed, will sort discovered devices by rssi.
        sortButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mDevices.sort(new BLEItem.RSSIComparator());
            } else {
                Collections.sort(mDevices);
            }
            mDeviceListAdapter.notifyDataSetChanged();
        });
    }

    private void initStopButton() {
        mStopButton =  findViewById(R.id.stopScanButton);
        mStopButton.setEnabled(false);
        mStopButton.setOnClickListener(v -> stopScanning());
    }

    private void initStartButton() {
        mStartButton =  findViewById(R.id.startScanButton);
        mStartButton.setEnabled(true);
        // Tie buttons to onClick event functions.
        mStartButton.setOnClickListener(v -> {
            // Check that all needed permissions have been granted.
            if (checkPermissions()) {
                // Ensure that bluetooth is turned on.
                if (mAdapter.isEnabled()) {
                    if (mBLEScanner == null) {
                        mBLEScanner = mAdapter.getBluetoothLeScanner();
                    }
                    startScanning();
                    // Ask user to turn on bluetooth if its off.
                } else {
                    promptBluetooth();
                }
                // Prompt the user for permissions required to run.
            } else {
                requestPermissions();
            }
        });
    }

    /**
     * On click listener which listens for clicks on recyclerview items.
     *
     * @param position The position in the list of the item that was clicked.
     */
    @Override
    public void onClick(int position) {
        BLEItem itemClicked = mDevices.get(position);
        if (itemClicked.getConnectable()) {
            mDevices.get(position).getDevice().connectGatt(this, false, connectCallback);
        }
    }

    /**
     * Callback object that is used when attempting to connect to a BT device.
     */
    private final BluetoothGattCallback connectCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                notifyConnected(gatt.getDevice().getAddress());
            } else {
                gatt.close();
                Log.d("BLUETOOTH NOT CONNECTED", "SOMETHING HAPPENED: status -> " + status + ", newState -> " + newState);
            }
        }
    };
}