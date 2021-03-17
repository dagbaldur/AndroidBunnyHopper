package com.example.bunnyhopperble.profile;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

public interface BleManagerCallbacks {

    /**
     * Called when the device has been connected. This does not mean that the application may start communication. A service discovery will be handled automatically after this call. Service discovery
     * may ends up with calling {@link #onServicesDiscovered(boolean)} or {@link #onDeviceNotSupported()} if required services have not been found.
     */
    public void onDeviceConnected();

    /**
     * Called when user initialized disconnection.
     */
    public void onDeviceDisconnecting();

    /**
     * Called when the device has disconnected (when the callback returned {@link BluetoothGattCallback#onConnectionStateChange(BluetoothGatt, int, int)} with state DISCONNECTED.
     */
    public void onDeviceDisconnected();

    /**
     * This callback is invoked when the Ble Manager lost connection to a device that has been connected with autoConnect option. Otherwise a {@link #onDeviceDisconnected()}
     * method will be called on such event.
     */
    public void onLinklossOccur();

    /**
     * Called when service discovery has finished and primary services has been found. The device is ready to operate. This method is not called if the primary, mandatory services were not found
     * during service discovery. For example in the Blood Pressure Monitor, a Blood Pressure service is a primary service and Intermediate Cuff Pressure service is a optional secondary service.
     * Existence of battery service is not notified by this call.
     *
     * @param optionalServicesFound
     *            if <code>true</code> the secondary services were also found on the device.
     */
    public void onServicesDiscovered(final boolean optionalServicesFound);

    /**
     * Method called when all initialization requests has been completed.
     */
    public void onDeviceReady();

    /**
     * Called when battery value has been received from the device.
     *
     * @param value
     *            the battery value in percent
     */
    public void onBatteryValueReceived(final int value);

    /**
     * Called when an {@link BluetoothGatt#GATT_INSUFFICIENT_AUTHENTICATION} error occurred and the device bond state is NOT_BONDED
     */
    public void onBondingRequired();

    /**
     * Called when the device has been successfully bonded.
     */
    public void onBonded();

    /**
     * Called when a BLE error has occurred
     *
     * @param message
     *            the error message
     * @param errorCode
     *            the error code
     */
    public void onError(final String message, final int errorCode);

    /**
     * Called when service discovery has finished but the main services were not found on the device.
     */
    public void onDeviceNotSupported();
}