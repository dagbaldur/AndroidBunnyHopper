package com.example.bunnyhopperble;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.ble.livedata.state.ConnectionState;
import com.example.bunnyhopperble.adapter.DiscoveredBluetoothDevice;
import com.example.bunnyhopperble.viewmodels.BlinkyViewModel;
import com.google.android.material.textview.MaterialTextView;

@SuppressWarnings("ConstantConditions")
public class BlinkyActivity extends AppCompatActivity {
	public static final String DEVICE = "com.example.bunnyhopperble.DEVICE";
	public static final String EXTRA_DEVICE = "com.example.bunnyhopperble.EXTRA_DEVICE";
	private String device1Address;
	private String device2Address;
	//public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";

	private BlinkyViewModel viewModel;//viewmodel for device 1
	private static String status = "-1";
	private int selected = -1;

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState){
		savedInstanceState.putInt(status,selected);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.tags, menu);
		menu.findItem(R.id.road).setChecked(true);
		menu.findItem((R.id.device1)).setChecked(true);
		menu.findItem((R.id.device1)).setTitle(device1Address);
		menu.findItem((R.id.device2)).setTitle(device2Address);
		/**
		switch(selected){
			case R.id.road:
				menuItem = (MenuItem) menu.findItem(selected);
				menuItem.setChecked();
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(0);
				return true;
			case R.id.mix:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(1);
				return true;
			case R.id.rock_garden:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(2);
				return true;
			case R.id.mud:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(3);
				return true;
			case R.id.bikepark:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(4);
				return true;
			case R.id.rough:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(5);
				return true;
			//technique type tags
			case R.id.riding:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(0);
				return true;
			case R.id.manual:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(1);
				return true;
			case R.id.bh:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(2);
				return true;
			case R.id.wheely:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(3);
				return true;
			case R.id.drop:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(4);
				return true;
			case R.id.turns:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(5);
				return true;
			case R.id.cornering:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(6);
				return true;
			case R.id.switchback:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(7);
				return true;
			case R.id.skidding:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(8);
				return true;
			case R.id.trackstand:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(9);
				return true;
			case R.id.pickbike:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(10);
				return true;
		}
		*/
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		selected = item.getItemId();
		status = String.valueOf(!item.isChecked());
		switch (selected) {
			//road type tags
			case R.id.road:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(0);
				return true;
			case R.id.mix:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(1);
				return true;
			case R.id.rock_garden:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(2);
				return true;
			case R.id.mud:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(3);
				return true;
			case R.id.bikepark:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(4);
				return true;
			case R.id.rough:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(5);
				return true;
			case R.id.pumptrack:
				item.setChecked(!item.isChecked());
				viewModel.setRoadTag(6);
				return true;
			//technique type tags
			case R.id.riding:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(0);
				return true;
			case R.id.manual:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(1);
				return true;
			case R.id.bh:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(2);
				return true;
			case R.id.wheely:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(3);
				return true;
			case R.id.drop:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(4);
				return true;
			case R.id.turns:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(5);
				return true;
			case R.id.cornering:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(6);
				return true;
			case R.id.switchback:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(7);
				return true;
			case R.id.skidding:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(8);
				return true;
			case R.id.trackstand:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(9);
				return true;
			case R.id.pickbike:
				item.setChecked(!item.isChecked());
				viewModel.setTechniqueTag(10);
				return true;
			//devices
			case R.id.device1:
				item.setChecked(!item.isChecked());
				viewModel.swapFront();
				return true;
			case R.id.device2:
				item.setChecked(!item.isChecked());
				viewModel.swapFront();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addDevice(){
		//final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE2);
		// Configure the view model.
		//viewModel2 = new ViewModelProvider(this).get(BlinkyViewModel.class);
		//viewModel2.connect(device);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null){
			selected = savedInstanceState.getInt(status);
		}
		setContentView(R.layout.activity_blinky);
		ButterKnife.bind(this);

		final Intent intent = getIntent();

		//Retrieve the passed devices
		final DiscoveredBluetoothDevice device = intent.getParcelableExtra(DEVICE);
		final DiscoveredBluetoothDevice device2 = intent.getParcelableExtra(EXTRA_DEVICE);

		device1Address = device.getAddress();
		device2Address = device2.getAddress();

		// Set up views.
		final MaterialToolbar toolbar = findViewById(R.id.toolbar);
		final MaterialTextView record_text = findViewById(R.id.button_state);
		final MaterialTextView save_text = findViewById(R.id.button_save_state);

		//Accelerometer view
		final TextView tax = findViewById(R.id.ax);
		final TextView tay = findViewById(R.id.ay);
		final TextView taz = findViewById(R.id.az);

		//Gyro view
		final TextView tgx = findViewById(R.id.gx);
		final TextView tgy = findViewById(R.id.gy);
		final TextView tgz = findViewById(R.id.gz);

		//Magne view
		final TextView tmx = findViewById(R.id.mx);
		final TextView tmy = findViewById(R.id.my);
		final TextView tmz = findViewById(R.id.mz);

		//Accelerometer view 2
		final TextView tax2 = findViewById(R.id.ax2);
		final TextView tay2 = findViewById(R.id.ay2);
		final TextView taz2 = findViewById(R.id.az2);

		//Gyro view 2
		final TextView tgx2 = findViewById(R.id.gx2);
		final TextView tgy2 = findViewById(R.id.gy2);
		final TextView tgz2 = findViewById(R.id.gz2);

		//Magne view 2
		final TextView tmx2 = findViewById(R.id.mx2);
		final TextView tmy2 = findViewById(R.id.my2);
		final TextView tmz2 = findViewById(R.id.mz2);


		final MaterialToolbar record = findViewById(R.id.button_tool_bar);
		final MaterialToolbar save = findViewById(R.id.button_save_bar);

		//container
		final LinearLayout progressContainer = findViewById(R.id.progress_container);
		final TextView connectionState = findViewById(R.id.connection_state);
		final View content = findViewById(R.id.device_container);
		final View notSupported = findViewById(R.id.not_supported);

		toolbar.setTitle("Hop");
		toolbar.setSubtitle("Recorder");

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Configure the view model, send both devices here!
		viewModel = new ViewModelProvider(this).get(BlinkyViewModel.class);
		viewModel.connect(device,device2);

		//record.setOnCheckedChangeListener((buttonView,isChecked) -> viewModel.setRecordingState(isChecked));
		record.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View view) {
						Boolean status = viewModel.getRecordingState().getValue();

						if(!status){
							record_text.setText("Recording...");
							record.setLogo(android.R.drawable.ic_media_pause);
							viewModel.startRecording();
						}else {//for pausing...right now inactive really
							record_text.setText("Recording paused.");
							record.setLogo(android.R.drawable.ic_media_play);
							viewModel.pauseRecording();
						}
					}

				}
		);
		save.setOnClickListener(
				new View.OnClickListener(){
					@Override
					public void onClick(View view) {

						//int status = viewModel.getSavedState().getValue();
						save_text.setText("Saving...");
						record_text.setText("Recording stopped.");
						record.setLogo(android.R.drawable.ic_media_play);
						viewModel.saveRecording();
					}

				}
		);

		//save.setOnCheckedChangeListener((buttonView,isChecked) -> viewModel.saveRecording());
		//viewModel.setRecordingState();
		viewModel.getConnectionState().observe(this, state -> {
			switch (state.getState()) {
				case CONNECTING:
					progressContainer.setVisibility(View.VISIBLE);
					notSupported.setVisibility(View.GONE);
					connectionState.setText(R.string.state_connecting);
					break;
				case INITIALIZING:
					connectionState.setText(R.string.state_initializing);
					break;
				case READY:
					progressContainer.setVisibility(View.GONE);
					content.setVisibility(View.VISIBLE);
					onConnectionStateChanged(true);
					break;
				case DISCONNECTED:
					if (state instanceof ConnectionState.Disconnected) {
						final ConnectionState.Disconnected stateWithReason = (ConnectionState.Disconnected) state;
						if (stateWithReason.isNotSupported()) {
							progressContainer.setVisibility(View.GONE);
							notSupported.setVisibility(View.VISIBLE);
						}
					}
					// fallthrough
				case DISCONNECTING:
					onConnectionStateChanged(false);
					break;
			}
		});

		
		viewModel.getRecordingState().observe(this, value-> {
			if(value){
				record_text.setText("Recording...");
				record.setLogo(android.R.drawable.ic_media_pause);
			}
			else {
				record_text.setText("Recording stopped.");
				record.setLogo(android.R.drawable.ic_media_play);
			}
		});

		viewModel.getSavedState().observe(this, value-> {
			if(value == 0){
				save_text.setText("New records not saved.");
			}
			else if(value == 1){
				save_text.setText("Saving...");
			}
			else if(value == 2){
				save_text.setText("Saved!");
			}
			else if(value >= 3){
				save_text.setText("Save failed");
			}
		});

		viewModel.getAllValues().observe(this, value-> {
			if(value[0].contentEquals("front")){
				tax.setText(String.valueOf(value[1]));
				tay.setText(String.valueOf(value[2]));
				taz.setText(String.valueOf(value[3]));
				tgx.setText(String.valueOf(value[7]));
				tgy.setText(String.valueOf(value[8]));
				tgz.setText(String.valueOf(value[9]));
				tmx.setText(String.valueOf(value[4]));
				tmy.setText(String.valueOf(value[5]));
				tmz.setText(String.valueOf(value[6]));
			}
			else{
				tax2.setText(String.valueOf(value[1]));
				tay2.setText(String.valueOf(value[2]));
				taz2.setText(String.valueOf(value[3]));
				tgx2.setText(String.valueOf(value[7]));
				tgy2.setText(String.valueOf(value[8]));
				tgz2.setText(String.valueOf(value[9]));
				tmx2.setText(String.valueOf(value[4]));
				tmy2.setText(String.valueOf(value[5]));
				tmz2.setText(String.valueOf(value[6]));
			}

		});

	}

	@OnClick(R.id.action_clear_cache)
	public void onTryAgainClicked() {
		viewModel.reconnect();
	}

	private void onConnectionStateChanged(final boolean connected) {
		if (!connected) {

		}
	}
}
