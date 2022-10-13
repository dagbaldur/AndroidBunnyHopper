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

public class HopperActivity extends AppCompatActivity {
    public static final String DEVICE = "com.example.bunnyhopperble.DEVICE";
    public static final String EXTRA_DEVICE = "com.example.bunnyhopperble.EXTRA_DEVICE";
    private String device1Address;
    private String device2Address;

    private BlinkyViewModel viewModel;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            selected = savedInstanceState.getInt(status);
        }
        setContentView(R.layout.new_activity);
        ButterKnife.bind(this);

        final Intent intent = getIntent();

        //Retrieve the passed devices
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(DEVICE);
        final DiscoveredBluetoothDevice device2 = intent.getParcelableExtra(EXTRA_DEVICE);

        device1Address = device.getAddress();
        device2Address = device2.getAddress();

        // Set up views.
        final MaterialToolbar toolbar = findViewById(R.id.toolbar);
        final MaterialToolbar record = findViewById(R.id.button_record);
        final MaterialToolbar save = findViewById(R.id.button_save);
        final MaterialToolbar upload = findViewById(R.id.button_cloud);
        final MaterialToolbar values_view = findViewById(R.id.button_vals);

        //container
        final LinearLayout progressContainer = findViewById(R.id.progress_container);
        final TextView connectionState = findViewById(R.id.connection_state);
        final View content = findViewById(R.id.device_container);
        final View notSupported = findViewById(R.id.not_supported);

        toolbar.setTitle("Hop");
        toolbar.setSubtitle("Recorder");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(BlinkyViewModel.class);
        viewModel.connect(device,device2);

        record.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Boolean status = viewModel.getRecordingState().getValue();

                        if(!status){
                            record.setLogo(android.R.drawable.ic_media_pause);
                            viewModel.startRecording();
                        }else {
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
                        record.setLogo(android.R.drawable.ic_media_play);
                        viewModel.saveRecording();
                    }

                }
        );

        upload.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        record.setLogo(android.R.drawable.ic_media_play);
                        viewModel.saveRecording();
                        //todo add cloud upload function
                    }
                }
        );

        values_view.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //todo add view for value representation of sensors
                    }
                }
        );


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
                record.setLogo(android.R.drawable.ic_media_pause);
            }
            else {
                record.setLogo(android.R.drawable.ic_media_play);
            }
        });

        viewModel.getSavedState().observe(this, value-> {
            if(value == 0){
                //todo add visible markings of saving status (not saved)
            }
            else if(value == 1){
                //todo add visible markings of saving status (saving, progress bar?)
            }
            else if(value == 2){
                //todo add visible markings of saving status (saved confirmation? view?)
            }
            else if(value >= 3){
                //todo add visible markings of saving status (Save failed)
            }
        });

        viewModel.getAllValues().observe(this, value-> {
            if(value[0].contentEquals("front")){
                //todo set view of sensors for front values (value[n])
            }
            else{
                //todo set view of sensors for back values (value[n])
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
