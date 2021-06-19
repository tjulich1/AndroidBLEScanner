// Trent Julich ~ 17 June 2021

package com.example.blescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for recyclerview which displays discovered BLE devices.
 */
public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.BLEViewHolder>{

    private final List<BLEItem> mDevices;
    private final RecyclerViewClickListener mListener;

    public BLEAdapter(List<BLEItem> devices, RecyclerViewClickListener listener)
    {
        mListener = listener;
        mDevices = devices;
    }

    @NonNull
    @Override
    public BLEAdapter.BLEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate xml layout for recyclerview
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Layout defined in res/layout/bledevice_item.xml
        View deviceView = inflater.inflate(R.layout.bledevice_item, parent, false);

        return new BLEViewHolder(deviceView);
    }

    // Used to populate the recyclerview with data found in mDevices.
    @Override
    public void onBindViewHolder(BLEAdapter.BLEViewHolder holder, int position) {
        // Get the data model based on position
        BLEItem item = mDevices.get(position);

        final Context c = holder.itemView.getContext();

        // Update the frontend views with the data for each device.
        TextView nameView = holder.deviceName;
        nameView.setText(c.getString(R.string.device_label, item.getName()));

        TextView rssiView = holder.deviceRssi;
        rssiView.setText(c.getString(R.string.rssi_label, item.getRssi()));

        TextView connectView = holder.deviceConnectable;
        boolean connectable = item.getConnectable();
        if (connectable) {
            connectView.setText(c.getString(R.string.connectable));
        } else {
            connectView.setText(c.getString(R.string.not_connectable));
        }

        TextView addressView = holder.deviceAddress;
        addressView.setText(c.getString(R.string.address_label, item.getAddress()));
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
        public TextView deviceAddress;

        public BLEViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> mListener.onClick(getBindingAdapterPosition()));

            // Connect the adapter to the front end views for each device property.
            deviceName = itemView.findViewById(R.id.device_name);
            deviceRssi = itemView.findViewById(R.id.device_rssi);
            deviceConnectable = itemView.findViewById(R.id.device_connectable);
            deviceAddress = itemView.findViewById(R.id.device_address);
        }
    }
}
