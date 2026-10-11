package com.example.bluetoothserial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private TextView statusText;
    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        ListView deviceList = findViewById(R.id.deviceList);
        Button sendButton = findViewById(R.id.sendButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            statusText.setText("Bluetooth not supported");
            return;
        }

        requestPermissions();
        loadPairedDevices(deviceList);

        sendButton.setOnClickListener(v -> sendValue("1"));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
        }
    }

    @SuppressLint("MissingPermission")
    private void loadPairedDevices(ListView listView) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> deviceNames = new ArrayList<>();
        ArrayList<BluetoothDevice> devices = new ArrayList<>();

        for (BluetoothDevice device : pairedDevices) {
            deviceNames.add(device.getName() + "\n" + device.getAddress());
            devices.add(device);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_list_item_1, deviceNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            connectToDevice(devices.get(position));
        });
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            statusText.setText("Connected to " + device.getName());
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            statusText.setText("Connection failed: " + e.getMessage());
        }
    }

    private void sendValue(String value) {
        if (outputStream == null) {
            statusText.setText("Not connected. Tap a device first.");
            return;
        }
        try {
            outputStream.write((value + "\n").getBytes());
            statusText.setText("Sent: " + value);
        } catch (IOException e) {
            statusText.setText("Send failed: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket = null) bluetoothSocket.close();
        } catch (IOException e) {
            // Ignore
        }
    }
}
