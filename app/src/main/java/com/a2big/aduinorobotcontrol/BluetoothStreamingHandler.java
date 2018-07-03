package com.a2big.aduinorobotcontrol;

public abstract  class BluetoothStreamingHandler {
    public abstract void onError(Exception e);
    public abstract void onConnected();
    public abstract void onDisconnected();
    public abstract void onData(byte[] buffer, int length);
    public final boolean close() {
        BluetoothSerialClient btSet = BluetoothSerialClient.getInstance();
        if(btSet != null)
            return btSet.close();
        return false;
    }

    public final boolean write(byte[] buffer) {
        BluetoothSerialClient btSet = BluetoothSerialClient.getInstance();
        if(btSet != null)
            return btSet.write(buffer);
        return false;
    }
}
