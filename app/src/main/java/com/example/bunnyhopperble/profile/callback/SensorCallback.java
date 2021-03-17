package com.example.bunnyhopperble.profile.callback;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface SensorCallback {
    void onDataChanged(@NonNull final BluetoothDevice device, final byte[] value);
}

