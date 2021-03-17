/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.bunnyhopperble.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import no.nordicsemi.android.ble.livedata.state.ConnectionState;
import com.example.bunnyhopperble.adapter.DiscoveredBluetoothDevice;
import com.example.bunnyhopperble.profile.BlinkyManager;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class BlinkyViewModel extends AndroidViewModel {
	private final BlinkyManager blinkyManager;
	//private final BlinkyManager device2Manager;
	private BluetoothDevice device;
	private BluetoothDevice device2;

	@RequiresApi(api = Build.VERSION_CODES.O)
	public BlinkyViewModel(@NonNull final Application application) {
		super(application);

		// Initialize the manager.
		blinkyManager = new BlinkyManager(getApplication());
		//device2Manager = new BlinkyManager(getApplication());
	}

	public LiveData<ConnectionState> getConnectionState() { return blinkyManager.getState(); }



	public void setRecordingState(){
		blinkyManager.setRecordingState();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void saveRecording(){
		blinkyManager.saveRecording();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void startRecording(){ blinkyManager.startRecording(); }

	public LiveData<Boolean> getRecordingState(){
		return blinkyManager.getRecordingState();
	}
	public LiveData<Integer> getSavedState(){ return blinkyManager.getSavingState(); }

	public LiveData<String> getAx() { return blinkyManager.getAx();	}
	public LiveData<String> getAy() { return blinkyManager.getAy(); }
	public LiveData<String> getAz() { return blinkyManager.getAz(); }

	public LiveData<String> getGx() { return blinkyManager.getGx(); }
	public LiveData<String> getGy() { return blinkyManager.getGy(); }
	public LiveData<String> getGz() { return blinkyManager.getGz(); }

	public LiveData<String> getMx() { return blinkyManager.getMx(); }
	public LiveData<String> getMy() { return blinkyManager.getMy(); }
	public LiveData<String> getMz() { return blinkyManager.getMz(); }

	//filter methods
	public void setRoadTag(int road){
		blinkyManager.setRoadTag(road);
	}

	public void setTechniqueTag(int techniqueIndex){
		blinkyManager.setTechniqueTag(techniqueIndex);
	}

	public void connect(@NonNull final DiscoveredBluetoothDevice target) {
		if (device == null) {
			device = target.getDevice();
			final LogSession logSession = Logger
					.newSession(getApplication(), null, target.getAddress(), target.getName());
			blinkyManager.setLogger(logSession);
			reconnect();
		}
		//uncomment for second hopper
		/**if(device2 == null && target.getAddress() != device.getAddress()){//add a second hopper
			device2 = target.getDevice();
			final LogSession logSession = Logger
					.newSession(getApplication(), null, target.getAddress(), target.getName());
		 	device2Manager.setLogger(logSession);
			reconnect();
		}*/
	}

	public void reconnect() {
		if (device != null) {
			blinkyManager.connect(device)
					.retry(3, 100)
					.useAutoConnect(true)
					.enqueue();
		}

		/**if(device2 != null){
		 	device2Manager.connect(device2)
					.retry(3,100)
					.useAutoConnect(true)
					.enqueue();
		}*/
	}

	/**
	 * Disconnect from peripheral.
	 */
	private void disconnect() {
		device = null;
		blinkyManager.disconnect().enqueue();
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		if (blinkyManager.isConnected()) {
			disconnect();
		}
	}
}
