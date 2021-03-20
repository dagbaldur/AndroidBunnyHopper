package com.example.bunnyhopperble.profile;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.UUID;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

import com.example.bunnyhopperble.R;
import com.example.bunnyhopperble.adapter.RecordAdapter;
import com.example.bunnyhopperble.adapter.DBAdapter;

import com.example.bunnyhopperble.profile.callback.SensorDataCallback;

import static java.lang.String.format;

public class BlinkyManager extends ObservableBleManager {
	/** Bunny Hopper Service UUID. */
	public final static UUID BUNNY_UUID_SERVICE = UUID.fromString("833092d2-693b-11eb-9439-0242ac130002");
	public final static UUID SENSOR_CHARACTERISTIC_UUID = UUID.fromString("00002a58-0000-1000-8000-00805f9b34da");
	private BluetoothGattCharacteristic sensorCharacteristic;
	private LogSession logSession;
	private boolean supported;

	public BlinkyManager(@NonNull final Context context) {
		super(context);
	}

	private final MutableLiveData<float[]> sensorData = new MutableLiveData<>();
	public final LiveData<float[]> getArray(){ return sensorData;	}

	@NonNull
	@Override
	protected BleManagerGattCallback getGattCallback() {
		return new BlinkyBleManagerGattCallback();
	}


	public void setLogger(@Nullable final LogSession session) {
		logSession = session;
	}

	@Override
	public void log(final int priority, @NonNull final String message) {
		Logger.log(logSession, LogContract.Log.Level.fromPriority(priority), message);
	}

	@Override
	protected boolean shouldClearCacheWhenDisconnected() {
		return !supported;
	}

	private final SensorDataCallback sensorCallback = new SensorDataCallback() {

		@Override
		public void onDataChanged(@NonNull final BluetoothDevice device, final byte[] value) {
			float[] floatArray = getFloatArray(value);
			sensorData.setValue(floatArray);
		}

		private float[] getFloatArray(byte[] bytes){
			float[] floatArray = new float[bytes.length / 4];
			ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floatArray);
			return floatArray;
		}

		@Override
		public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
										  @NonNull final Data data) {
			log(Log.WARN, "Invalid data received: " + data);
		}
	};

	private class BlinkyBleManagerGattCallback extends BleManagerGattCallback {
		@RequiresApi(api = Build.VERSION_CODES.O)
		@Override
		protected void initialize() {
			//setNotificationCallback(buttonCharacteristic).with(buttonCallback);
			beginAtomicRequestQueue()
					.add(requestMtu(440)
							.with((device, mtu) -> log(Log.INFO, "MTU set to " + mtu))
							.fail((device,status) -> log(Log.WARN, "Requested MTU not supported: " + status)))
					.add(readCharacteristic(sensorCharacteristic)
							.with(sensorCallback))
					.add(enableNotifications(sensorCharacteristic))
					.done(device -> log(Log.INFO, "Target initialized"))
					.enqueue();

			setNotificationCallback(sensorCharacteristic).with(sensorCallback);
		}

		@Override
		public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {

			final BluetoothGattService bunny = gatt.getService(BUNNY_UUID_SERVICE);
			if (bunny != null) {
				log(Log.WARN, "Bunny found");
				sensorCharacteristic = bunny.getCharacteristic(SENSOR_CHARACTERISTIC_UUID);
			}

			supported = sensorCharacteristic != null;
			return supported;
		}

		@RequiresApi(api = Build.VERSION_CODES.O)
		@Override
		protected void onDeviceDisconnected() {
			sensorCharacteristic = null;
		}

	}
}
