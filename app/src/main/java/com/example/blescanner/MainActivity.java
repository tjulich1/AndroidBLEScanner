// Trent Julich ~ 17 June 2021

package com.example.blescanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    ArrayList<BLEItem> mDevices;
    BLEAdapter mDeviceListAdapter;
    BluetoothAdapter mAdapter;
    BluetoothManager mManager;
    BluetoothLeScanner mBLEScanner;

    // Controls for starting/stopping scans.
    Button mStartButton;
    Button mStopButton;

    private void initializeButtons() {
        mStartButton = (Button) findViewById(R.id.startScanButton);
        mStopButton = (Button) findViewById(R.id.stopScanButton);

        // Tie buttons to onClick event functions.
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanning();
            }
        });

    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
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

    /**
     * Checks to see if the device currently has location services enabled. If not, request access.
     *
     * @return True if location permissions are enabled.
     */
    private boolean checkLocation() {
        boolean status = false;

        // First, check if location permissions are enabled.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("BLUETOOTH SCANNER", "LOCATION ENABLED");
            status = true;
        // If not, request permission.
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        return status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        RecyclerView deviceRecycler = (RecyclerView) findViewById(R.id.rvBLE);

        // Initialize contacts
        mDevices = new ArrayList<BLEItem>();

        // Create adapter passing in the sample user data
        mDeviceListAdapter = new BLEAdapter(mDevices);

        // Attach the adapter to the recyclerview to populate items
        deviceRecycler.setAdapter(mDeviceListAdapter);

        // Set layout manager to position the items
        deviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize a bluetooth adapter, manager, and scanner.
        mManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = mManager.getAdapter();
        mBLEScanner = mAdapter.getBluetoothLeScanner();

        /////////////////////////// ADD ERROR HANDLING

        // Make sure bluetooth is enabled.
        if (mAdapter != null && mAdapter.isEnabled()) {
            Log.d("BLUETOOTH", "Bluetooth is indeed... enabled.");
        }

        initializeButtons();

    }

    private ScanCallback deviceScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // Add device to list.
            mDevices.add(new BLEItem(result.getDevice().getName(), String.valueOf(result.getRssi()), result.isConnectable()));

            // Notify adapter of data change.

        }
    };

    public void startScanning() {
        Log.d("BLUETOOTH SCANNER", "STARTED SCANNING");
    }

    public void stopScanning() {
        Log.d("BLUETOOTH SCANNER", "STOPPED SCANNING");
    }
}