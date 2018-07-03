package com.a2big.aduinorobotcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.a2big.aduinorobotcontrol.listener.OnBluetoothEnabledListener;
import com.a2big.aduinorobotcontrol.listener.OnScanListener;

public class MainActivity extends Activity {

    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private EditText mEditTextInput;
    private TextView mTextView;
    private Button mButtonSend,mButtonConnect;
    private Button mButtonUp,mButtonDown;
    private Button mButtonLeft,mButtonRight,mButtonStop;
    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private BluetoothSerialClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClient = BluetoothSerialClient.getInstance();

        if(mClient == null) {
            Toast.makeText(getApplicationContext(), "Cannot use the Bluetooth device.", Toast.LENGTH_SHORT).show();
            finish();
        }
        initProgressDialog();
        initDeviceListDialog();
        initWidget();

    }


    @Override
    protected void onPause() {
        mClient.cancelScan(getApplicationContext());
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableBluetooth();


    }

    private void initProgressDialog() {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
    }

    private void initWidget() {
        /*
        mTextView = (TextView) findViewById(R.id.textViewTerminal);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mEditTextInput = (EditText) findViewById(R.id.editTextInput);
        mButtonSend = (Button) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStringData(mEditTextInput.getText().toString());
                mEditTextInput.setText("");
            }
        });
        */
        //
        mButtonConnect = (Button) findViewById(R.id.btnConnect);
        mButtonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connect = mClient.isConnection();
                    if (!connect) {
                        mDeviceListDialog.show();
                    } else {
                        mBTHandler.close();
                    }
               /*
                } else {
                    showCodeDlg();
                    return true;
                }
                */
            }
        });


        mButtonUp = (Button) findViewById(R.id.btnUp);
        mButtonUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("JH", "UP");
                sendStringData("1");
            }
        });

        mButtonDown = (Button) findViewById(R.id.btnDown);
        mButtonDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("JH", "Down");
                sendStringData("2");

            }
        });

        mButtonLeft = (Button) findViewById(R.id.btnLeft);
        mButtonLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("JH", "Left");
                sendStringData("3");

            }
        });

        mButtonRight = (Button) findViewById(R.id.btnRight);
        mButtonRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("JH", "Right");
                sendStringData("4");

            }
        });


        mButtonStop = (Button) findViewById(R.id.btnStop);
        mButtonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("JH", "Stop");
                sendStringData("0");

            }
        });
    }

    private void initDeviceListDialog() {
        mDeviceArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_device);
        ListView listView = new ListView(getApplicationContext());
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select bluetooth device");
        builder.setView(listView);
        builder.setPositiveButton("Scan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanDevices();
                    }
                });
        mDeviceListDialog = builder.create();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
    }

    private void addDeviceToArrayAdapter(BluetoothDevice device) {
        if(mBluetoothDevices.contains(device)) {
            mBluetoothDevices.remove(device);
            mDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
        }
        mBluetoothDevices.add(device);
        mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress() );
        mDeviceArrayAdapter.notifyDataSetChanged();

    }


    private void enableBluetooth() {
        BluetoothSerialClient btSet =  mClient;
        btSet.enableBluetooth(this, new OnBluetoothEnabledListener() {
            @Override
            public void onBluetoothEnabled(boolean success) {
                if(success) {
                    getPairedDevices();
                } else {
                    finish();
                }
            }
        });
    }

    private void addText(String text) {
        /*
        mTextView.append(text);
        final int scrollAmount = mTextView.getLayout().getLineTop(mTextView.getLineCount()) - mTextView.getHeight();
        if (scrollAmount > 0)
            mTextView.scrollTo(0, scrollAmount);
        else
            mTextView.scrollTo(0, 0);
        */
        Log.e("JH",text);
    }


    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  mClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
    }

    private void scanDevices() {
        BluetoothSerialClient btSet = mClient;
        btSet.scanDevices(getApplicationContext(), new OnScanListener() {
            String message ="";
            @Override
            public void onStart() {
                Log.d("Test", "Scan Start.");
                mLoadingDialog.show();
                message = "Scanning....";
                mLoadingDialog.setMessage("Scanning....");
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        BluetoothSerialClient btSet = mClient;
                        btSet.cancelScan(getApplicationContext());
                    }
                });
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) {
                addDeviceToArrayAdapter(bluetoothDevice);
                message += "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress();
                mLoadingDialog.setMessage(message);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Scan finish.");
                message = "";
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                mDeviceListDialog.show();
            }
        });
    }


    private void connect(BluetoothDevice device) {
        mLoadingDialog.setMessage("Connecting....");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        BluetoothSerialClient btSet =  mClient;
        btSet.connect(getApplicationContext(), device, mBTHandler);
    }

    private BluetoothStreamingHandler mBTHandler = new BluetoothStreamingHandler() {
        ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);

        @Override
        public void onError(Exception e) {
            mLoadingDialog.cancel();
            addText("Messgae : Connection error - " +  e.toString() + "\n");
           ////// mMenu.getItem(0).setTitle(R.string.action_connect);
        }

        @Override
        public void onDisconnected() {
           /////////// mMenu.getItem(0).setTitle(R.string.action_connect);
            mLoadingDialog.cancel();
            addText("Messgae : Disconnected.\n");
        }
        @Override
        public void onData(byte[] buffer, int length) {
            if(length == 0) return;
            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer, 0, length);
            if(buffer[length - 1] == '\0') {
                addText(mClient.getConnectedDevice().getName() + " : " +
                        new String(mmByteBuffer.array(), 0, mmByteBuffer.position()) + '\n');
                mmByteBuffer.clear();
            }
        }

        @Override
        public void onConnected() {
            addText("Messgae : Connected. " + mClient.getConnectedDevice().getName() + "\n");
            mLoadingDialog.cancel();
         ///////   mMenu.getItem(0).setTitle(R.string.action_disconnect);
        }
    };

    public void sendStringData(String data) {
        data += '\0';
        byte[] buffer = data.getBytes();
        if(mBTHandler.write(buffer)) {
            addText("Me : " + data + '\n');
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mClient.claer();
    };


    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean connect = mClient.isConnection();
        if(item.getItemId() == R.id.action_connect) {
            if (!connect) {
                mDeviceListDialog.show();
            } else {
                mBTHandler.close();
            }
            return true;
        } else {
            showCodeDlg();
            return true;
        }
    }
    */
    private void showCodeDlg() {
        TextView codeView = new TextView(this);
        codeView.setText(Html.fromHtml(readCode()));
        codeView.setMovementMethod(new ScrollingMovementMethod());
        codeView.setBackgroundColor(Color.parseColor("#202020"));
        new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_DialogWhenLarge)
                .setView(codeView)
                .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private String readCode() {
        try {
            InputStream is = getAssets().open("HC_06.txt");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();
            String code = new String(buffer);
            buffer = null;
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}







