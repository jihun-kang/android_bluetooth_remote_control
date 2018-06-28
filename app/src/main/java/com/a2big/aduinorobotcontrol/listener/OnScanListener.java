package com.a2big.aduinorobotcontrol.listener;

import android.bluetooth.BluetoothDevice;

public  interface OnScanListener {
    public void onStart();
    public void onFoundDevice(BluetoothDevice bluetoothDevice);
    public void onFinish();
}
