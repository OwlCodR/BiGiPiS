package com.bigipis.bigipis.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bigipis.bigipis.FragmentError;
import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import static com.bigipis.bigipis.MainActivity.FRAGMENT_TAG;


public class FragmentMapMainMenu extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    private View myInflateView;
    private FloatingActionButton fabRoutes, fabCreateRoute;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflateView = inflater.inflate(R.layout.fragment_map_main_menu, container, false);
        fabRoutes = myInflateView.findViewById(R.id.floatingActionButtonRoutes);
        fabCreateRoute = myInflateView.findViewById(R.id.floatingActionButtonCreateRoute);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        try {
            ((SupportMapFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG)).getMapAsync(this);
        } catch (Exception e) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FragmentError())
                    .addToBackStack(null)
                    .commit();
            Log.e("ERROR", e.getMessage());
        }

        fabRoutes.setOnClickListener(this);
        fabCreateRoute.setOnClickListener(this);
        return myInflateView;
    }



    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (Objects.requireNonNull(mainActivity).isUserSignedIn()) {
            int id = v.getId();
            switch (id) {
                case R.id.floatingActionButtonCreateRoute:
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.content_frame, new FragmentMapCreateRoute())
                            .remove(this)
                            .addToBackStack(null)
                            .commit();
                    break;
                }
                case R.id.floatingActionButtonRoutes:
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.content_frame, new FragmentMapRoutes())
                            .remove(this)
                            .addToBackStack(null)
                            .commit();
                    break;
                }
            }
        } else {
            mainActivity.showDialogAccessError();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
    }
}
