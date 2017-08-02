package com.rashintha.opensource.bluetoothrccarcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    final int BLUETOOTH_MSG_STATE = 0;

    private BluetoothAdapter bluetoothAdapter;
    private ImageButton btnBluetoothEnable;
    private Set<BluetoothDevice> pairedDevices;

    Handler bluetoothHandler;

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

        bluetoothHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == BLUETOOTH_MSG_STATE){
                    String message = (String) msg.obj;

                    //Handle message
                }
            }
        };

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

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setIcon(R.mipmap.bluetooth_connect);
                dialogBuilder.setTitle("Select RC Car");

                final ArrayList<String> deviceMACs = new ArrayList<>();

                ArrayAdapter<String> deviceDetails = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

                for(BluetoothDevice device : pairedDevices){
                    deviceDetails.add(device.getName() + " " + device.getAddress());
                    deviceMACs.add(device.getAddress());
                }

                dialogBuilder.setAdapter(deviceDetails, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), deviceMACs.get(which), Toast.LENGTH_SHORT).show();
                    }
                });

                dialogBuilder.show();
            }
        });
    }

    private class ConnectedThread extends Thread{
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket){
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                Log.wtf("IO E", "Construct");
            }

            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while(true){
                try {
                    bytes = inputStream.read(buffer);
                    String message = new String(buffer, 0, bytes);
                    bluetoothHandler.obtainMessage(BLUETOOTH_MSG_STATE, bytes, -1, message).sendToTarget();
                }catch (IOException e){
                    Log.wtf("IO E", "run");
                    break;
                }
            }
        }

        public void write(String input){
            byte[] buffer = input.getBytes();
            try{
                outputStream.write(buffer);
            }catch (IOException e){
                Log.wtf("IO E", "write");
            }
        }
    }
}
