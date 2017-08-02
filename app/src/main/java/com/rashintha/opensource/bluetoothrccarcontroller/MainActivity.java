package com.rashintha.opensource.bluetoothrccarcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private ImageButton btnBluetoothEnable;
    private Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onResume() {
        super.onResume();

        if(bluetoothAdapter.isEnabled()){
            btnBluetoothEnable.setImageResource(R.mipmap.bluetooth_enable_true);
        }else{
            btnBluetoothEnable.setImageResource(R.mipmap.bluetooth_enable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnStop = (ImageButton) findViewById(R.id.btnStop);
        ImageButton btnUp = (ImageButton) findViewById(R.id.btnUp);
        ImageButton btnDown = (ImageButton) findViewById(R.id.btnDown);
        ImageButton btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        ImageButton btnRight = (ImageButton) findViewById(R.id.btnRight);
        btnBluetoothEnable = (ImageButton) findViewById(R.id.btnBluetoothEnable);
        ImageButton btnBluetoothConnect = (ImageButton) findViewById(R.id.btnBluetoothConnect);
        ImageButton btnBluetoothDisconnect = (ImageButton) findViewById(R.id.btnBluetoothDisconnect);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnBluetoothEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothAdapter.isEnabled()){
                    Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enable, 0);
                }else{
                    Toast.makeText(getApplicationContext(), "Already enabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBluetoothConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevices = bluetoothAdapter.getBondedDevices();

                ListView lstDevices = new ListView(getApplicationContext());

                final ArrayList<String> deviceMACs = new ArrayList<>();

                ArrayAdapter<String> deviceDetails = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item);

                for(BluetoothDevice device : pairedDevices){
                    deviceDetails.add(device.getName() + " " + device.getAddress());
                    deviceMACs.add(device.getAddress());
                }

                lstDevices.setAdapter(deviceDetails);

                lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getApplicationContext(), deviceMACs.get(position), Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
                dialogBuilder.setTitle("Select RC Car");

                dialogBuilder.setView(lstDevices);

                AlertDialog dialog = dialogBuilder.show();
                dialog.show();
            }
        });
    }
}
