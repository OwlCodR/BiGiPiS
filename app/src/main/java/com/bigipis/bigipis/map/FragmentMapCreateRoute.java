package com.bigipis.bigipis.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bigipis.bigipis.FragmentError;
import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import static com.bigipis.bigipis.MainActivity.FRAGMENT_TAG;

public class FragmentMapCreateRoute extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private View view;
    @Nullable private GoogleMap googleMap;
    private FloatingActionButton fabStartFinish, fabNewPoint;
    private Button buttonSave;
//    private ImageView imageViewStartFinishMarker, imageViewPointMarker;
    private ArrayList<LatLng> newRoute;
    private ArrayList<MarkerOptions> arrayListMarkerOptions;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_create_route, container, false);

//        imageViewStartFinishMarker = view.findViewById(R.id.imageViewStartFinishPointMarker);
//        imageViewPointMarker = view.findViewById(R.id.imageViewPointPointMarker);

        fabStartFinish = view.findViewById(R.id.floatingActionButtonStartFinish);
        fabNewPoint = view.findViewById(R.id.floatingActionButtonNewMarker);
        buttonSave = view.findViewById(R.id.buttonSaveRoute);

        fabStartFinish.setOnClickListener(this);
        fabNewPoint.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        startMap();
        return view;
    }

    private void startMap() {
        newRoute = new ArrayList<>();
        arrayListMarkerOptions = new ArrayList<>();

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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((MainActivity) getActivity()).showDialogMapPermission();
        }
        else {
            googleMap.setMyLocationEnabled(true);
        }
        Log.i("MAP", "Map is ready");
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.floatingActionButtonNewMarker: {
                    if (newRoute.size() != 0 && !isFinish()) {
                        newRoute.add(googleMap.getCameraPosition().target);
                        MarkerOptions marker = new MarkerOptions()
                                .position(newRoute.get(newRoute.size() - 1))
                                //.draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_marker));
                        googleMap.addMarker(marker);
                        arrayListMarkerOptions.add(marker);
                    }
                    break;
                }
                case R.id.floatingActionButtonStartFinish: {
                    if (newRoute.size() == 0) {
                        newRoute.add(googleMap.getCameraPosition().target);
                        MarkerOptions marker = new MarkerOptions()
                                .position(newRoute.get(newRoute.size() - 1))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_flag))
                                //.draggable(true)
                                .title("Старт");
                        // @TODO Check DRAG!
                        // @TODO Check if there is no start and trying to add pos
                        googleMap.addMarker(marker);
                        arrayListMarkerOptions.add(marker);
                    } else if (!isFinish()) {
                        newRoute.add(googleMap.getCameraPosition().target);
                        MarkerOptions marker = new MarkerOptions()
                                .position(newRoute.get(newRoute.size() - 1))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_flag))
                                //.draggable(true)
                                .title("Финиш");
                        googleMap.addMarker(marker);
                        arrayListMarkerOptions.add(marker);
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.creating);
                        buttonSave.setVisibility(View.VISIBLE);
                        // @TODO offset анимации http://developer.alexanderklimov.ru/android/animation/tweenanimation.php#translate
                        // @TODO линии через PolyLine https://habr.com/ru/post/341548/
                        buttonSave.startAnimation(animation);
                    }
                    break;
                }
                case R.id.buttonSaveRoute: {

                    break;
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    private boolean isFinish() {
        for (int i = 0; i < arrayListMarkerOptions.size(); i++)
            try {
                if (arrayListMarkerOptions.get(i).getTitle().equals("Финиш"))
                    return true;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        return false;
    }
}
