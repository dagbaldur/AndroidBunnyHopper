package com.example.bunnyhopperble.viewmodels;
import com.example.bunnyhopperble.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class ConnectFragment extends Fragment {
    public ConnectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("ResourceType") View rootView = inflater.inflate(R.menu.fragment_connect, container, false);
        return rootView;
    }
}
