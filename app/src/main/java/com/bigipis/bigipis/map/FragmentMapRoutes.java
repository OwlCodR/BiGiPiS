package com.bigipis.bigipis.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bigipis.bigipis.FragmentError;
import com.bigipis.bigipis.R;
import com.bigipis.bigipis.source.ClusterIconRender;
import com.bigipis.bigipis.source.MyMarker;
import com.bigipis.bigipis.source.Route;
import com.bigipis.bigipis.source.ViewPagerAdapter;
import com.bigipis.bigipis.tutorial.FragmentTutorial;
import com.bigipis.bigipis.tutorial.FragmentTutorialOpenGoogleMaps;
import com.bigipis.bigipis.tutorial.FragmentTutorialPermission;
import com.bigipis.bigipis.tutorial.FragmentTutorialPressLocation;
import com.bigipis.bigipis.tutorial.FragmentTutorialStartNavigation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static com.bigipis.bigipis.MainActivity.FRAGMENT_TAG;

public class FragmentMapRoutes extends Fragment implements PopupMenu.OnMenuItemClickListener, OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<MyMarker>, View.OnClickListener {
    private View view;
    private GoogleMap googleMap;
    private List<Route> routesList;
    private ClusterManager<MyMarker> clusterManager;
    private Button buttonGoToGoogleMaps;
    private int numberRoute;
    private DotsIndicator dotsIndicator;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<Polyline> polylineArrayList;
    private ArrayList<Marker> markerArrayList;
    private boolean settingsOnce;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_routes, container, false);
        setHasOptionsMenu(true);
        startMap();
        routesList = new ArrayList<>();
        polylineArrayList = new ArrayList<>();
        markerArrayList = new ArrayList<>();
        numberRoute = -1;
        settingsOnce = false;

        buttonGoToGoogleMaps = view.findViewById(R.id.buttonGoToGoogleMaps);
        buttonGoToGoogleMaps.setOnClickListener(this);

        dotsIndicator = (DotsIndicator) view.findViewById(R.id.dots_indicator);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerTutorial);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.filter_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
        popupMenu.inflate(R.menu.routes_filter);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fresh: {
                removePolylines();
                removeMarkers();
                clusterManager.clearItems();
                routesList = new ArrayList<>();
                getAndShowMarkers("Свежее");
                break;
            }
            case R.id.action_best: {
                removePolylines();
                removeMarkers();
                clusterManager.clearItems();
                routesList = new ArrayList<>();
                getAndShowMarkers("Лучшее");
                break;
            }
            case R.id.action_nearest: {
                removePolylines();
                removeMarkers();
                clusterManager.clearItems();
                routesList = new ArrayList<>();
                getAndShowMarkers("Ближайшее");
                break;
            }
        }
        return true;
    }

    private void getAndShowMarkers(String FILTER) {
        long milliseconds = Calendar.getInstance().getTimeInMillis();
        milliseconds -= 7 * 24 * 60 * 60 * 1000;
        if (FILTER.equals("Свежее")) {
            FirebaseFirestore.getInstance()
                    .collection("routes")
                    .whereGreaterThan("date", milliseconds)
                    .limit(10)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                Route route = queryDocumentSnapshots.getDocuments().get(i).toObject(Route.class);
                                routesList.add(route);
                            }
                            showMyMarkers();
                        }
                    });
        } else if (FILTER.equals("Лучшее")) {
            FirebaseFirestore.getInstance()
                    .collection("routes")
                    .whereGreaterThan("rating", 0)
                    .orderBy("rating", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                                Route route = queryDocumentSnapshots.getDocuments().get(i).toObject(Route.class);
                                routesList.add(route);
                            }
                            showMyMarkers();
                        }
                    });
        } else if (FILTER.equals("Ближайшее")) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getAllProviders();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showDialogMapPermission();
            }
            else {
                Location location = null;
                for (int i = 0; i < providers.size(); i++) {
                    if (locationManager.getLastKnownLocation(providers.get(i)) != null) {
                        location = locationManager.getLastKnownLocation(providers.get(i));
                    }
                }
                if (location != null)
                    setNearestMarkers(location);
                else
                    Toast.makeText(getActivity(), "Произошла ошибка!\nПопробуйте включить GPS!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setNearestMarkers(Location location) {
        showMyMarkers();
        /*
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 12.0f);
        Log.i("FragmentMapRoute", "minLat = " + (latLng.latitude - kilometresToLatitude(2)));
        Log.i("FragmentMapRoute", "maxLat = " + (latLng.latitude + kilometresToLatitude(2)));
        Log.i("FragmentMapRoute", "minLong = " + (latLng.longitude - kilometresToLongitude(2, latLng.latitude)));
        Log.i("FragmentMapRoute", "maxLong = " + (latLng.longitude + kilometresToLongitude(2, latLng.latitude)));
        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("routes");
        collectionReference
                .whereGreaterThan("latitude", latLng.latitude - kilometresToLatitude(2))
                .whereLessThan("latitude", latLng.latitude + kilometresToLatitude(2))
                .limit(0)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            Route route = queryDocumentSnapshots.getDocuments().get(i).toObject(Route.class);
                            double longitude = route.getPointsList().get(0).get("longitude");
                            if (longitude > latLng.longitude - kilometresToLongitude(5, latLng.latitude))
                                if (longitude < latLng.longitude + kilometresToLongitude(5, latLng.latitude))
                                    routesList.add(route);
                        }
                        showMyMarkers();
                    }
                });

         */
    }

    private void showDialogMapPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Внимание")
                .setMessage("Для работы этого фильтра нужно разрешение о местоположении устройства!\nЭти данные используются исключительно для вашей навигации!")
                .setCancelable(true)
                .setNegativeButton("Нет, мне не нужен этот фильтр", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Очень жаль, теперь фильтр не работает!\nНо вы всё еще можете дать разрешение в настройках", Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                })
                .create()
                .show();
    }

    private double kilometresToLatitude(double km) {
        double meridian = 40008.55; //km
        double answer = km / (meridian / 360.0);
        //Log.i("FragmentMapRoute", "answer = " + answer);
        return answer;
    }

    private double kilometresToLongitude(double km, double latitude) {
        double equator = 40075.696; //km
        double kmPerDeg = Math.cos(latitude) * (equator / 360.0);
        double answer = km / kmPerDeg;
        Log.i("FragmentMapRoute", "answer = " + answer);
        return answer;
    }

    private void showMyMarkers() {
        if (routesList.size() != 0) {
            for (int i = 0; i < routesList.size(); i++) {
                LatLng latLngStart = new LatLng(routesList.get(i).getPointsList().get(0).get("latitude"), routesList.get(i).getPointsList().get(0).get("longitude"));
                Log.i("FragmentMapRoute", "latLngStart = " + latLngStart);
                //Toast.makeText(getActivity(), "LatLng = " + latLngStart, Toast.LENGTH_LONG).show();
                if (routesList.get(i).getChipsNames().contains("Прогулка")) {
                    if (routesList.get(i).getRating() > 0)
                        clusterManager.addItem(new MyMarker(latLngStart, "Прогулка", "Рейтинг: +" + routesList.get(i).getRating(), R.mipmap.blue_flag));
                    else
                        clusterManager.addItem(new MyMarker(latLngStart, "Прогулка", "Рейтинг: " + routesList.get(i).getRating(), R.mipmap.blue_flag));
                }
                    /*
                    googleMap.addMarker(new MarkerOptions().position(latLngStart)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_marker))
                            .title("Прогулка"));
                     */
                else if (routesList.get(i).getChipsNames().contains("Спорт")) {
                    if (routesList.get(i).getRating() > 0)
                        clusterManager.addItem(new MyMarker(latLngStart, "Спорт", "Рейтинг: +" + routesList.get(i).getRating(), R.mipmap.orange_flag));
                    else
                        clusterManager.addItem(new MyMarker(latLngStart, "Спорт", "Рейтинг: " + routesList.get(i).getRating(), R.mipmap.orange_flag));
                }
                    /*
                    googleMap.addMarker(new MarkerOptions().position(latLngStart)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.orange_marker))
                            .title("Спорт"));
                     */
            }
            //clusterManager.setRenderer(new ClusterIconRender(getActivity().getApplicationContext(), googleMap, clusterManager));
            //clusterManager.cluster();
            Log.i("clusterManager", "clusterManager render" + routesList.size());
        } else {
            Snackbar.make(view, "К сожалению, по данному фильтру не были найдены маршруты", Snackbar.LENGTH_LONG).show();
        }
    }

    private void startMap() {
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
    public void onMapReady(GoogleMap gmap) {
        googleMap = gmap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (!settingsOnce) {

            settingsOnce = true;
        }
        clusterManager = new ClusterManager<>(getActivity().getApplicationContext(), googleMap);
        clusterManager.setRenderer(new ClusterIconRender(getActivity().getApplicationContext(), googleMap, clusterManager));
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(this);

        Log.i("MAP", "" + clusterManager.getMarkerCollection());
    }

    @Override
    public boolean onClusterItemClick(MyMarker myMarker) {
        try {
            removePolylines();
            removeMarkers();
            clusterManager.cluster();
            //clusterManager.clearItems();
            for (int i = 0; i < routesList.size(); i++) {
                Map<String, Double> pointsMap = routesList.get(i).getPointsList().get(0);
                if (pointsMap.get("latitude") == myMarker.getPosition().latitude) {
                    if (pointsMap.get("longitude") == myMarker.getPosition().longitude) {
                        numberRoute = i;
                        showRoute();
                        //showMyMarkers();
                        //Log.i("routesList", "" + routesList);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FragmentMapRoutes", e.getMessage());
        }
        showButton();
        return false;
    }

    private void removePolylines() {
        for (int i = 0; i < polylineArrayList.size(); i++)
            polylineArrayList.get(i).remove();
        polylineArrayList.clear();
    }

    private void removeMarkers() {
        for (int i = 0; i < markerArrayList.size(); i++)
            markerArrayList.get(i).remove();
        markerArrayList.clear();
    }

    private void showButton() {
        buttonGoToGoogleMaps.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.creating);
        buttonGoToGoogleMaps.startAnimation(animation);
    }

    private void showRoute() {
        showPolylines();
        try {
            for (int i = 1; i < routesList.get(numberRoute).getPointsList().size(); i++) {
                LatLng latLng = new LatLng(routesList.get(numberRoute).getPointsList().get(i).get("latitude"), routesList.get(numberRoute).getPointsList().get(i).get("longitude"));
                if (routesList.get(numberRoute).getChipsNames().contains("Прогулка")) {
                    if (i != routesList.get(numberRoute).getPointsList().size() - 1) {
                        markerArrayList.add(googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_marker))));
                    }
                    else {
                        markerArrayList.add(googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_flag))));
                    }
                }
                else {
                    if (i != routesList.get(numberRoute).getPointsList().size() - 1) {
                        markerArrayList.add(googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.orange_marker))));
                    }
                    else {
                        markerArrayList.add(googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.orange_flag))));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FragmentMapRoutes", e.getMessage());
        }
    }

    private void showPolylines() {
        try {
            PolylineOptions line = new PolylineOptions();
            List<Map<String, Double>> pointsMap = routesList.get(numberRoute).getPointsList();
            for (int i = 0; i < pointsMap.size(); i++) {
                line.add(new LatLng(pointsMap.get(i).get("latitude"), pointsMap.get(i).get("longitude")));
                line.width(15);
                line.endCap(new RoundCap());
                line.startCap(new RoundCap());
                line.color(0xaa2980b9);
                try {
                    polylineArrayList.add(googleMap.addPolyline(line));
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("FragmentMapRoutes", e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        buttonGoToGoogleMaps.setVisibility(View.GONE);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());
        viewPagerAdapter.addFragment(new FragmentTutorialOpenGoogleMaps(), "Обучение");
        viewPagerAdapter.addFragment(new FragmentTutorialPressLocation(), "Обучение");
        viewPagerAdapter.addFragment(new FragmentTutorialPermission(), "Обучение");

        Fragment fragment = new FragmentTutorialStartNavigation();
        Bundle bundle = new Bundle();
        bundle.putSerializable("pointsList", (Serializable) routesList.get(numberRoute).getPointsList());
        fragment.setArguments(bundle);
        viewPagerAdapter.addFragment(fragment, "Обучение");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    ((FragmentTutorial) viewPagerAdapter.getItem(position)).startAnimation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(viewPagerAdapter);
        dotsIndicator.setViewPager(viewPager);
    }
}
