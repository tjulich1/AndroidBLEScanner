// Trent Julich ~ 17 June 2021

package com.example.blescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for recyclerview which displays discovered BLE devices.
 */
public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.BLEViewHolder>{

    private List<BLEItem> mDevices;

    public BLEAdapter(List<BLEItem> devices)
    {
        mDevices = devices;
    }

    @Override
    public BLEAdapter.BLEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate xml layout for recyclerview
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Layout defined in res/layout/bledevice_item.xml
        View deviceView = inflater.inflate(R.layout.bledevice_item, parent, false);

        BLEViewHolder viewHolder = new BLEViewHolder(deviceView);
        return viewHolder;
    }

    // Used to populate the recyclerview with data found in mDevices.
    @Override
    public void onBindViewHolder(BLEAdapter.BLEViewHolder holder, int position) {
        // Get the data model based on position
        BLEItem item = mDevices.get(position);

        // Update the frontend views with the data for each device.
        TextView nameView = holder.deviceName;
        nameView.setText(item.getName());

        TextView rssiView = holder.deviceRssi;
        rssiView.setText(item.getRssi());

        TextView connectView = holder.deviceConnectable;
        connectView.setText(item.getConnectable());
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    /**
     * Holder for single recycler view item. Each item corresponds to single ble device.
     */
    public class BLEViewHolder extends RecyclerView.ViewHolder {

        // Member field for each property of the device that should be displayed.
        public TextView deviceName;
        public TextView deviceRssi;
        public TextView deviceConnectable;

        public BLEViewHolder(View itemView) {
            super(itemView);

            // Connect the adapter to the front end views for each device property.
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceRssi = (TextView) itemView.findViewById(R.id.device_rssi);
            deviceConnectable = (TextView) itemView.findViewById(R.id.device_connectable);
        }
    }
}
