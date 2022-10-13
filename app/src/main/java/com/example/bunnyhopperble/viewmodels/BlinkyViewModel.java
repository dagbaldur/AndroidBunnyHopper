package com.example.bunnyhopperble.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.bunnyhopperble.adapter.DBAdapter;
import no.nordicsemi.android.ble.livedata.state.ConnectionState;

import com.example.bunnyhopperble.adapter.DiscoveredBluetoothDevice;
import com.example.bunnyhopperble.adapter.RecordAdapter;
import com.example.bunnyhopperble.profile.BlinkyManager;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class BlinkyViewModel extends AndroidViewModel {
	private final BlinkyManager frontManager;
	private final BlinkyManager backManager;
	private BluetoothDevice bunnyFront;
	private BluetoothDevice bunnyBack;
	private final DBAdapter db;
	private RecordAdapter record;
	public MediatorLiveData<String[]> dataMerger = new MediatorLiveData<>();
	private String device1OrientationTag;
	private String device2OrientationTag;
	public int roadType = 0;
	public int techniqueType[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

	@RequiresApi(api = Build.VERSION_CODES.O)
	public BlinkyViewModel(@NonNull final Application application) {
		super(application);
		db = new DBAdapter(super.getApplication().getApplicationContext());
		record = new RecordAdapter(super.getApplication().getApplicationContext());
		frontManager = new BlinkyManager(getApplication());
		backManager = new BlinkyManager(getApplication());
		device1OrientationTag = "front";
		device2OrientationTag = "back";
		dataMerger.addSource(frontManager.getArray(),
				new Observer<float[]>(){
					@Override
					public void onChanged(float[] values){
						DecimalFormat df = new DecimalFormat("#.###");
						df.setRoundingMode(RoundingMode.CEILING);
						recordValues(device1OrientationTag,values);
						String[] sendValues = new String[values.length + 1];
						sendValues[0] = device1OrientationTag;
						for(int i=0;i<values.length;i++){
							sendValues[i+1] = df.format(values[i]);
						}
						dataMerger.setValue(sendValues);
					}
				}
		);
		dataMerger.addSource(backManager.getArray(),
				new Observer<float[]>(){
					@Override
					public void onChanged(float[] values){
						DecimalFormat df = new DecimalFormat("#.###");
						df.setRoundingMode(RoundingMode.CEILING);
						recordValues(device2OrientationTag,values);
						String[] sendValues = new String[values.length + 1];
						sendValues[0] = device2OrientationTag;
						for(int i=0;i<values.length;i++){
							sendValues[i+1] = df.format(values[i]);
						}
						dataMerger.setValue(sendValues);
					}
				}
		);
	}

	public LiveData<ConnectionState> getConnectionState() { return frontManager.getState(); }
	public LiveData<Boolean> getRecordingState(){
		return record.getRecordingState();
	}
	public LiveData<Integer> getSavedState(){ return db.getSavedState(); }

	public LiveData<float[]> getFrontValues(){
		LiveData<float[]> value = frontManager.getArray();
		//recordValues("Front",value.getValue());
		return value;
	}
	public LiveData<float[]> getBackValues(){
		return backManager.getArray();
	}

	public void swapFront(){
		String temp = device1OrientationTag;
		device1OrientationTag = device2OrientationTag;
		device2OrientationTag = temp;
	}
	public LiveData<String[]> getAllValues(){
		return dataMerger;
	}
	@RequiresApi(api = Build.VERSION_CODES.O)
	public void saveRecording(){
		Log.i("ViewModel","Calling to save");
		int save_state = db.getSavedState().getValue();

		if(save_state != 1 && save_state != 2 ) {
			if (record.getRecordingState().getValue()){
				record.changeRecording();
			}

			db.setFile(record.getFilename());
			//async
			new Thread(new Runnable(){
				@Override
				public void run(){
					db.saveData();
					if (db.getSavedState().getValue() < 3) {
						record.deleteRecording();
					}
				}
			}).start();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void startRecording(){
		Log.i("ViewModel","Calling to start recording");
		if(!record.getRecordingState().getValue()) {
			db.setSavedState(0);
			if(record.isDeleted())
				record.newFile();
			else
				record.changeRecording();
		}
	}

	public void pauseRecording(){
		Log.i("ViewModel","Pausing recording");
		if(record.getRecordingState().getValue()){
			record.changeRecording();
		}
	}

	public void recordValues(String location, float[] values){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		for(int i = 0; i < values.length; i++){
			record.addRecord(roadType,techniqueType,location,i,timestamp,values[i]);
		}
	}


	public void setRoadTag(int road){
		roadType = road;
	}

	public void setTechniqueTag(int techniqueIndex){
		techniqueType[techniqueIndex] = techniqueType[techniqueIndex] * -1;
	}

	public void connect(@NonNull final DiscoveredBluetoothDevice target1,@NonNull final DiscoveredBluetoothDevice target2) {
		if (bunnyFront == null) {
			bunnyFront = target1.getDevice();
			final LogSession logSession = Logger
					.newSession(getApplication(), null, target1.getAddress(), target1.getName());
			frontManager.setLogger(logSession);
			//dataMerger.addSource(frontManager.getArray(),value -> dataMerger.setValue(value));
		}
		if(bunnyBack == null){
			bunnyBack = target2.getDevice();
			final LogSession logSession2 = Logger
					.newSession(getApplication(), null, target2.getAddress(), target2.getName());
			backManager.setLogger(logSession2);
			//dataMerger.addSource(backManager.getArray(),value -> dataMerger.setValue(value));
		}
		reconnect();
	}

	public void reconnect() {
		if (bunnyFront != null) {
			frontManager.connect(bunnyFront)
					.retry(3, 100)
					.useAutoConnect(true)
					.enqueue();
		}

		if(bunnyBack != null){
		 	backManager.connect(bunnyBack)
					.retry(3,100)
					.useAutoConnect(true)
					.enqueue();
		}
	}

	private void disconnect(BlinkyManager manager, BluetoothDevice device) {
		device = null;
		manager.disconnect().enqueue();
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		if (frontManager.isConnected()) {
			disconnect(frontManager,bunnyFront);
		}
		if(backManager.isConnected()){
			disconnect(backManager,bunnyBack);
		}
	}
}
