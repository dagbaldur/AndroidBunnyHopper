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
	public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";

	private BlinkyViewModel viewModel;//viewmodel for device 1
	private BlinkyViewModel viewModel2;//viewmodel for device 2

	//@BindView(R.id.button_tool_bar) SwitchMaterial record;
	//@BindView(R.id.button_state) TextView record_msg;
	//@BindView(R.id.button_save_bar) SwitchMaterial save;
	//@BindView(R.id.button_save_state) TextView save_msg;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.tags, menu);
		menu.findItem(R.id.road).setChecked(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
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
		setContentView(R.layout.activity_blinky);
		ButterKnife.bind(this);

		final Intent intent = getIntent();
		final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
		final String deviceName = device.getName();
		final String deviceAddress = device.getAddress();

		final MaterialToolbar toolbar = findViewById(R.id.toolbar);
		// Set up views.
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

		final MaterialToolbar record = findViewById(R.id.button_tool_bar);
		final MaterialToolbar save = findViewById(R.id.button_save_bar);
		//container
		final LinearLayout progressContainer = findViewById(R.id.progress_container);
		final TextView connectionState = findViewById(R.id.connection_state);
		final View content = findViewById(R.id.device_container);
		final View notSupported = findViewById(R.id.not_supported);



		toolbar.setTitle(deviceName != null ? deviceName : getString(R.string.unknown_device));
		toolbar.setSubtitle(deviceAddress);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Configure the view model.
		viewModel = new ViewModelProvider(this).get(BlinkyViewModel.class);
		viewModel.connect(device);

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

		viewModel.getAx().observe(this, value-> { tax.setText(value); });
		viewModel.getAy().observe(this, value-> { tay.setText(value); });
		viewModel.getAz().observe(this, value-> { taz.setText(value); });

		viewModel.getGx().observe(this, value-> { tgx.setText(value); });
		viewModel.getGy().observe(this, value-> { tgy.setText(value); });
		viewModel.getGz().observe(this, value-> { tgz.setText(value); });

		viewModel.getMx().observe(this, value-> { tmx.setText(value); });
		viewModel.getMy().observe(this, value-> { tmy.setText(value); });
		viewModel.getMz().observe(this, value-> { tmz.setText(value); });


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
