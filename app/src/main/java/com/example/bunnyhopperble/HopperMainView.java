package com.example.bunnyhopperble;

import android.content.Intent;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.example.bunnyhopperble.adapter.DiscoveredBluetoothDevice;
import com.example.bunnyhopperble.viewmodels.BlinkyViewModel;
import com.example.bunnyhopperble.viewmodels.ConnectFragment;
import com.example.bunnyhopperble.viewmodels.DataModel;
import com.example.bunnyhopperble.viewmodels.DrawerItemCustomAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.ble.livedata.state.ConnectionState;

/**
 * packages for menu drawer
 */


public class HopperMainView extends AppCompatActivity{
    private MaterialToolbar toolbar;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){

        }

        setContentView(R.layout.map_view);
        ButterKnife.bind(this);
        final Intent intent = getIntent();

        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set up views.
        initToolbar();
        setupDrawer();

        final Spinner terrain = findViewById(R.id.terrain);
        final Spinner condition = findViewById(R.id.condition);
        final Spinner gnarly = findViewById(R.id.gnarly);

        final MaterialToolbar start = findViewById(R.id.button_start);
        final MaterialToolbar upload = findViewById(R.id.button_upload);

        //container
        final LinearLayout progressContainer = findViewById(R.id.progress_container);
        final TextView connectionState = findViewById(R.id.connection_state);
        final View content = findViewById(R.id.device_container);
        final View notSupported = findViewById(R.id.not_supported);

        //Terrain dropdown setup
        ArrayAdapter<CharSequence> terrain_adapter = ArrayAdapter.createFromResource(this,
                R.array.terrain_array, android.R.layout.simple_spinner_item);
        terrain_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        terrain.setAdapter(terrain_adapter);

        terrain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (terrain.getSelectedItem().toString().trim().equals("Road")) {
                    //stuff can happen
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Gnarly dropdown setup
        ArrayAdapter<CharSequence> gnarly_adapter = ArrayAdapter.createFromResource(this,
                R.array.gnarly_array, android.R.layout.simple_spinner_item);
        gnarly_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gnarly.setAdapter(gnarly_adapter);

        gnarly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (gnarly.getSelectedItem().toString().trim().equals("Easy")) {
                    //stuff can happen
                }
            }
            public void onNothingSelected (AdapterView < ? > parent){
            }
        });



    }


    /** Menu and drawer setup*/
    private void initToolbar(){
        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Main Hopper");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_cloud));
    }

    private void setupDrawer(){
        DataModel[] drawerItem = new DataModel[3];

        drawerItem[0] = new DataModel(R.drawable.ic_bluetooth_searching, "Connect");
        drawerItem[1] = new DataModel(R.drawable.ic_terrain, "Fixtures");
        drawerItem[2] = new DataModel(R.drawable.techniques, "Table");

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.menu.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setupDrawerToggle();
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new ConnectFragment();
                break;
            case 1:
                //fragment = new FixturesFragment();
                break;
            case 2:
                //fragment = new TableFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setupDrawerToggle(){
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }

}
