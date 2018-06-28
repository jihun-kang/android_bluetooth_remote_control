package com.a2big.aduinorobotcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.a2big.aduinorobotcontrol.listener.OnBluetoothEnabledListener;

public  class BluetoothUpActivity extends Activity {
    private static int REQUEST_ENABLE_BT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                upbluetoothDevice();
            }
        }, 100);
    }
    private void upbluetoothDevice() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT) ;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT) {
            OnBluetoothEnabledListener onBluetoothEnabledListener = BluetoothSerialClient.getInstance().mOnBluetoothUpListener;
            if (resultCode == Activity.RESULT_OK) {
                if(onBluetoothEnabledListener != null)
                    onBluetoothEnabledListener.onBluetoothEnabled(true);
                finish();
            } else {
                if(onBluetoothEnabledListener != null)
                    onBluetoothEnabledListener.onBluetoothEnabled(false);
                finish();
            }
        }
    }
}
// End BluetoothUpActivity


