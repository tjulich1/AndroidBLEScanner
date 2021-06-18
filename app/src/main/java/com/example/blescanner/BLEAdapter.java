package com.example.blescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.ViewHolder>{

    private List<BLEItem> devices;

    public BLEAdapter(List<BLEItem> devices)
    {
        this.devices = devices;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public BLEAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.bledevice_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(BLEAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        BLEItem item = devices.get(position);

        // Set item views based on your views and data model
        TextView nameView = holder.deviceName;
        nameView.setText(item.getName());

        TextView rssiView = holder.deviceRssi;
        rssiView.setText(item.getRssi());

        TextView connectView = holder.deviceConnectable;
        connectView.setText(item.getConnectable());
    }

    @Override
    public int getItemCount() {
        return this.devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Member field for each view that should be filled.
        public TextView deviceName;
        public TextView deviceRssi;
        public TextView deviceConnectable;

        public ViewHolder(View itemView) {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceRssi = (TextView) itemView.findViewById(R.id.device_rssi);
            deviceConnectable = (TextView) itemView.findViewById(R.id.device_connectable);
        }

    }

}
