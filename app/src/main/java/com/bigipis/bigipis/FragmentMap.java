package com.bigipis.bigipis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;


public class FragmentMap extends SupportMapFragment {
    private View myInflateView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflateView = inflater.inflate(R.layout.fragment_map, container, false);

        return myInflateView;
    }
}
