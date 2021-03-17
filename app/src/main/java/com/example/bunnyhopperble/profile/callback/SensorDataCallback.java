package com.example.bunnyhopperble.profile.callback;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;


@SuppressWarnings("ConstantConditions")
public abstract class SensorDataCallback implements ProfileDataCallback,SensorCallback {

    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() == 0) {
            onInvalidDataReceived(device, data);
            return;
        }
        else{
            parse(device, data);
        }
    }


    private void parse(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        final byte[] value = data.getValue();
        int len = data.getValue().length;
        onDataChanged(device,value);
    }

}