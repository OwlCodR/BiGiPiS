package com.bigipis.bigipis.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigipis.bigipis.R;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class ListAdapterBluetooth extends BaseAdapter {
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private LayoutInflater layoutInflater;

    ListAdapterBluetooth(Context context, Set<BluetoothDevice> devices) {
        bluetoothDevices = new ArrayList<>(devices);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static List<BluetoothDeviceDecorator> decorateDevices(Collection<BluetoothDevice> btDevices) {
        List<BluetoothDeviceDecorator> devices = new ArrayList<>();
        for (BluetoothDevice dev : btDevices) {
            devices.add(new BluetoothDeviceDecorator(dev, 0));
        }
        return devices;
    }

    public ArrayList<BluetoothDevice> getBluetoothDevices(){
        return bluetoothDevices;
    }

    public void addDevice(BluetoothDevice device) {
        bluetoothDevices.add(device);
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return bluetoothDevices;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Возвращает готовый View пунтка списка
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_bluetooth, viewGroup, false);
        }

        TextView itemName = view.findViewById(R.id.itemName);
        TextView itemMac = view.findViewById(R.id.itemMac);

        itemName.setText(bluetoothDevices.get(i).getName());
        itemMac.setText(bluetoothDevices.get(i).getAddress());
        return view;
    }
}
