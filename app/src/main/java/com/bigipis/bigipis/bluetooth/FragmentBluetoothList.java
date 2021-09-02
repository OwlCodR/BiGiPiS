package com.bigipis.bigipis.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.google.android.material.snackbar.Snackbar;

public class FragmentBluetoothList extends Fragment implements AdapterView.OnItemClickListener, BluetoothLeService.OnBluetoothScanCallback, BluetoothLeService.OnBluetoothEventCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private ListView listViewDevices;
    private ListAdapterBluetooth listAdapterBluetooth;
    private ProgressBar progressBar;
    private TextView textViewNotFound;
    private BluetoothLeService bluetoothService = (BluetoothLeService) BluetoothLeService.getDefaultInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_bluetooth, container,false);
        listViewDevices = view.findViewById(R.id.listViewBluetooth);
        listViewDevices.setOnItemClickListener(this);

        progressBar = view.findViewById(R.id.progressBarListBluetooth);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapterBluetooth = new ListAdapterBluetooth(getActivity(), bluetoothAdapter.getBondedDevices());
        listViewDevices.setAdapter(listAdapterBluetooth);

        textViewNotFound = view.findViewById(R.id.textViewBluetoothNotFound);
        textViewNotFound.setVisibility(View.GONE);

        setHasOptionsMenu(true);

        int permissionStatus = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            btAdapter.enable(); // Включаю Bluetooth

            bluetoothService.setOnScanCallback(this);
            bluetoothService.setOnEventCallback(this);
            bluetoothService.startScan();
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_REQUEST_CODE);
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bluetoothService.setOnScanCallback(this);
                bluetoothService.setOnEventCallback(this);
                bluetoothService.startScan();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bluetoothService.connect(listAdapterBluetooth.getBluetoothDevices().get(i));
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        listAdapterBluetooth.addDevice(device);
        listViewDevices.setAdapter(listAdapterBluetooth);
    }

    @Override
    public void onStartScan() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopScan() {
        progressBar.setVisibility(View.GONE);
        listViewDevices.setAdapter(listAdapterBluetooth);
        if (listAdapterBluetooth.getBluetoothDevices().size() < 1)
            textViewNotFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_exit).setVisible(false);
        menu.findItem(R.id.action_sign).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDataRead(byte[] bytes, int i) {

    }

    @Override
    public void onStatusChange(BluetoothStatus bluetoothStatus) {
        try {
            if (bluetoothStatus == BluetoothStatus.CONNECTING) {
                ((MainActivity) getActivity()).isLacersConnected = false;
                Snackbar.make(view, "Подключение...", Snackbar.LENGTH_SHORT).show();
            }
            if (bluetoothStatus == BluetoothStatus.CONNECTED) {
                ((MainActivity) getActivity()).isLacersConnected = true;
                Snackbar.make(view, "Вы успешно подключили Lacers!", Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }
            if (bluetoothStatus == BluetoothStatus.NONE) {
                ((MainActivity) getActivity()).isLacersConnected = false;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void onDeviceName(String s) {

    }

    @Override
    public void onToast(String s) {

    }

    @Override
    public void onDataWrite(byte[] bytes) {

    }
}
