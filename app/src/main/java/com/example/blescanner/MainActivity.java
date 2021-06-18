package com.example.blescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<BLEItem> devices;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvBLE);

        // Initialize contacts
        devices = new ArrayList<BLEItem>();

        for (int i = 0; i < 10; i++) {
            BLEItem tempItem = new BLEItem("Temp", "RSSI", true);
            devices.add(tempItem);
        }

        // Create adapter passing in the sample user data
        BLEAdapter adapter = new BLEAdapter(devices);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

        // Initialize a bluetooth adapter.
//        final BluetoothManager bluetoothManager =
//            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    }
}