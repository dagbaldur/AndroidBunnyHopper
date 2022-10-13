package com.example.bunnyhopperble.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import android.os.ParcelUuid;
import android.text.method.HideReturnsTransformationMethod;

import java.util.ArrayList;
import java.util.List;

import com.example.bunnyhopperble.adapter.DiscoveredBluetoothDevice;
import com.example.bunnyhopperble.profile.BlinkyManager;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;


public class DevicesLiveData extends LiveData<List<DiscoveredBluetoothDevice>> {
	private static final ParcelUuid FILTER_BH_UUID = new ParcelUuid(BlinkyManager.BUNNY_UUID_SERVICE);
	private static final ParcelUuid FILTER_AM_UUID = new ParcelUuid(BlinkyManager.SENSOR_CHARACTERISTIC_UUID);
	private static final int FILTER_RSSI = -50; // [dBm] -> INCREASE OR DECREASE FOR DISTANCE FILTER

	@NonNull
	private final List<DiscoveredBluetoothDevice> devices = new ArrayList<>();
	@Nullable
	private List<DiscoveredBluetoothDevice> filteredDevices = null;
	private boolean filterUuidRequired;
	private boolean filterNearbyOnly;

	/* package */ DevicesLiveData(final boolean filterUuidRequired, final boolean filterNearbyOnly) {
		this.filterUuidRequired = filterUuidRequired;
		this.filterNearbyOnly = filterNearbyOnly;
	}

	/* package */ synchronized void bluetoothDisabled() {
		devices.clear();
		filteredDevices = null;
		postValue(null);
	}

	/* package */  boolean filterByUuid(final boolean uuidRequired) {
		filterUuidRequired = uuidRequired;
		return applyFilter();
	}

	/* package */  boolean filterByDistance(final boolean nearbyOnly) {
		filterNearbyOnly = nearbyOnly;
		return applyFilter();
	}

	/* package */ synchronized boolean deviceDiscovered(@NonNull final ScanResult result) {
		DiscoveredBluetoothDevice device;

		// Check if it's a new device.
		final int index = indexOf(result);
		if (index == -1) {
			device = new DiscoveredBluetoothDevice(result);
			devices.add(device);
		} else {
			device = devices.get(index);
		}

		// Update RSSI and name.
		device.update(result);

		// Return true if the device was on the filtered list or is to be added.

		return (filteredDevices != null && filteredDevices.contains(device))
				|| (matchesUuidFilter(result) && matchesNearbyFilter(device.getHighestRssi()));
    }

	/**
	 * Clears the list of devices.
	 */
	public synchronized void clear() {
		devices.clear();
		filteredDevices = null;
		postValue(null);
	}

	/**
	 * Refreshes the filtered device list based on the filter flags.
	 */
	/* package */ synchronized boolean applyFilter() {
		final List<DiscoveredBluetoothDevice> tmp = new ArrayList<>();
		for (final DiscoveredBluetoothDevice device : devices) {
			final ScanResult result = device.getScanResult();
			if (matchesUuidFilter(result) && matchesNearbyFilter(device.getHighestRssi())) {
				tmp.add(device);
			}
		}
		filteredDevices = tmp;
        postValue(filteredDevices);
        return !filteredDevices.isEmpty();
	}

	/**
	 * Finds the index of existing devices on the device list.
	 *
	 * @param result scan result.
	 * @return Index of -1 if not found.
	 */
	private int indexOf(@NonNull final ScanResult result) {
		int i = 0;
		for (final DiscoveredBluetoothDevice device : devices) {
			if (device.matches(result))
				return i;
			i++;
		}
		return -1;
	}

	@SuppressWarnings("SimplifiableIfStatement")
	private boolean matchesUuidFilter(@NonNull final ScanResult result) {
		if (!filterUuidRequired)
			return true;

		final ScanRecord record = result.getScanRecord();
		if (record == null)
			return false;

		final List<ParcelUuid> uuids = record.getServiceUuids();
		if (uuids == null)
			return false;

		return uuids.contains(FILTER_BH_UUID);
		//return uuids.contains(FILTER_UUID);
	}


	@SuppressWarnings("SimplifiableIfStatement")
	private boolean matchesNearbyFilter(final int rssi) {
		if (!filterNearbyOnly)
			return true;

		return rssi >= FILTER_RSSI;
	}
}
