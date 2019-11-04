package com.bigipis.bigipis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FragmentMap extends Fragment implements View.OnClickListener {
    private View myInflateView;
    private FloatingActionButton fabRoutes, fabCreateRoute;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflateView = inflater.inflate(R.layout.fragment_map, container, false);
        fabRoutes = myInflateView.findViewById(R.id.floatingActionButtonRoutes);
        fabCreateRoute = myInflateView.findViewById(R.id.floatingActionButtonAddRoute);

        fabRoutes.setOnClickListener(this);
        fabCreateRoute.setOnClickListener(this);
        return myInflateView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.floatingActionButtonAddRoute:
            {
                break;
            }
            case R.id.floatingActionButtonRoutes:
            {

                break;
            }
        }
    }
}
