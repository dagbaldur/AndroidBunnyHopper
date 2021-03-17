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
	private Context context;
	public final static UUID BUNNY_UUID_SERVICE = UUID.fromString("833092d2-693b-11eb-9439-0242ac130002");
	public final static UUID SENSOR_CHARACTERISTIC_UUID = UUID.fromString("00002a58-0000-1000-8000-00805f9b34da");
	private RecordAdapter record;
	private final DBAdapter db;
	private BluetoothGattCharacteristic sensorCharacteristic;
	private LogSession logSession;
	private boolean supported;
	public int roadType = 0;
	public int techniqueType[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

	@RequiresApi(api = Build.VERSION_CODES.O)
	public BlinkyManager(@NonNull final Context cntx) {
		super(cntx);
		context = cntx;
		record = new RecordAdapter(context);
		db = new DBAdapter(context);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void saveRecording(){
		Log.i("DBAdapter","Calling to save");
		int save_state = db.getSavedState().getValue();

		//check stave status (not in progress)
		if(save_state != 1 && save_state != 2 ) {
			//stop recording first
			if (record.getRecordingState().getValue())
				setRecordingState();

			db.setFile(record.getFilename());
			db.saveData();
			//only delete if we dont find the value, otherwise get back to recording
			if (db.getSavedState().getValue() < 3) {
				record.deleteRecording();
			}
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	public void startRecording(){
		if(!record.getRecordingState().getValue()) {
			db.setSavedState(0);
			record.newFile();
			Log.i("RecordAdapter", "Starting new recording");
		}
	}

	private final MutableLiveData<float[]> sensorData = new MutableLiveData<>();

	private final MutableLiveData<String> ax = new MutableLiveData<>();
	private final MutableLiveData<String> ay = new MutableLiveData<>();
	private final MutableLiveData<String> az = new MutableLiveData<>();

	private final MutableLiveData<String> gx = new MutableLiveData<>();
	private final MutableLiveData<String> gy = new MutableLiveData<>();
	private final MutableLiveData<String> gz = new MutableLiveData<>();

	private final MutableLiveData<String> mx = new MutableLiveData<>();
	private final MutableLiveData<String> my = new MutableLiveData<>();
	private final MutableLiveData<String> mz = new MutableLiveData<>();

	public final LiveData<Boolean> getRecordingState(){ return record.getRecordingState(); }
	public final LiveData<Integer> getSavingState(){ return db.getSavedState(); }
	public void setRecordingState(){ record.changeRecording(); }

	public final LiveData<String> getAx() { return ax;	}
	public final LiveData<String> getAy() { return ay;	}
	public final LiveData<String> getAz() { return az;	}

	public final LiveData<String> getGx() { return gx;	}
	public final LiveData<String> getGy() { return gy;	}
	public final LiveData<String> getGz() { return gz;	}

	public final LiveData<String> getMx() { return mx;	}
	public final LiveData<String> getMy() { return my;	}
	public final LiveData<String> getMz() { return mz;	}


	//filter methods
	public void setRoadTag(int road){
		roadType = road;
	}

	public void setTechniqueTag(int techniqueIndex){
		techniqueType[techniqueIndex] = techniqueType[techniqueIndex] * -1;
	}
	@NonNull
	@Override
	protected BleManagerGattCallback getGattCallback() {
		return new BlinkyBleManagerGattCallback();
	}

	/**
	 * Sets the log session to be used for low level logging.
	 * @param session the session, or null, if nRF Logger is not installed.
	 */
	public void setLogger(@Nullable final LogSession session) {
		logSession = session;
	}

	@Override
	public void log(final int priority, @NonNull final String message) {
		// The priority is a Log.X constant, while the Logger accepts it's log levels.
		Logger.log(logSession, LogContract.Log.Level.fromPriority(priority), message);
	}

	@Override
	protected boolean shouldClearCacheWhenDisconnected() {
		return !supported;
	}


	private final SensorDataCallback sensorCallback = new SensorDataCallback() {

		@Override
		public void onDataChanged(@NonNull final BluetoothDevice device, final byte[] value) {
			//log(LogContract.Log.Level.APPLICATION, "MX: " + value);
			float[] floatArray = getFloatArray(value);
			DecimalFormat df = new DecimalFormat("#.#####");
			df.setRoundingMode(RoundingMode.CEILING);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String stringAcc = "";
			String stringGyro = "";
			String stringMag = "";

			record.addRecord(roadType,techniqueType,"AX",timestamp,floatArray[0]);
			record.addRecord(roadType,techniqueType,"AY",timestamp,floatArray[1]);
			record.addRecord(roadType,techniqueType,"AZ",timestamp,floatArray[2]);
			record.addRecord(roadType,techniqueType,"MX",timestamp,floatArray[3]);
			record.addRecord(roadType,techniqueType,"MY",timestamp,floatArray[4]);
			record.addRecord(roadType,techniqueType,"MZ",timestamp,floatArray[5]);
			record.addRecord(roadType,techniqueType,"GX",timestamp,floatArray[6]);
			record.addRecord(roadType,techniqueType,"GY",timestamp,floatArray[7]);
			record.addRecord(roadType,techniqueType,"GZ",timestamp,floatArray[8]);

			ax.setValue(df.format(floatArray[0]));
			ay.setValue(df.format(floatArray[1]));
			az.setValue(df.format(floatArray[2]));

			gx.setValue(df.format(floatArray[3]));
			gy.setValue(df.format(floatArray[4]));
			gz.setValue(df.format(floatArray[5]));

			mx.setValue(df.format(floatArray[6]));
			my.setValue(df.format(floatArray[7]));
			mz.setValue(df.format(floatArray[8]));

			sensorData.setValue(floatArray);
			for(int i = 0; i < floatArray.length;i++){
				if(i < 3){
					stringAcc = stringAcc + String.valueOf(floatArray[i]) + "|";
				}
				else if(i < 5){
					stringGyro = stringGyro + String.valueOf(floatArray[i]) + "|";
				}
				else{
					stringMag = stringMag + String.valueOf(floatArray[i]) + "|";
				}
			}
		}

		private float[] getFloatArray(byte[] bytes){

			float[] floatArray = new float[bytes.length / 4];
			ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floatArray);
			//for(int i = 0; i < 9; i++){
			//	Log.i("ArrayValueReceived","Value position "+i+" of "+floatArray[i]);
			//}
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
			saveRecording();
			sensorCharacteristic = null;
		}

	}
}
