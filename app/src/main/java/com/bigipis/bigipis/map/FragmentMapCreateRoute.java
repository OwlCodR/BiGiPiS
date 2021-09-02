package com.bigipis.bigipis.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bigipis.bigipis.FragmentError;
import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.bigipis.bigipis.source.Route;
import com.bigipis.bigipis.source.SQLiteHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.bigipis.bigipis.MainActivity.FRAGMENT_TAG;
import static com.bigipis.bigipis.MainActivity.user;

public class FragmentMapCreateRoute extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private View view;
    @Nullable private GoogleMap googleMap;
    private FloatingActionButton fabStartFinish, fabNewPoint;
    private Button buttonSave;
//    private ImageView imageViewStartFinishMarker, imageViewPointMarker;
    private List<com.google.android.gms.maps.model.LatLng> listLatLng;
    private List<MarkerOptions> listMarkerOptions;
    private ProgressBar progressBar;
    private Handler progressHandler;

    private int realProgress;
    // saveRouteOnServer() - 50%    Summary realProgress = 100%
    // saveRouteOnSQLite() - 50%

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_create_route, container, false);

//        imageViewStartFinishMarker = view.findViewById(R.id.imageViewStartFinishPointMarker);
//        imageViewPointMarker = view.findViewById(R.id.imageViewPointPointMarker);

        fabStartFinish = view.findViewById(R.id.floatingActionButtonStartFinish);
        fabNewPoint = view.findViewById(R.id.floatingActionButtonAddMarker);
        buttonSave = view.findViewById(R.id.buttonSaveRoute);
        progressBar = view.findViewById(R.id.progressBarRoute);

        fabStartFinish.setOnClickListener(this);
        fabNewPoint.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        fabNewPoint.setEnabled(false);

        progressHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (progressBar.getProgress() == progressBar.getMax() && realProgress == progressBar.getMax()) {
                    progressBar.setVisibility(View.GONE);
                    buttonSave.setVisibility(View.GONE);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.content_frame, new FragmentMapMainMenu())
                            .remove(fragmentManager.getFragments().get(fragmentManager.getFragments().size()-1))
                            .addToBackStack(null)
                            .commit();
                }
            }
        };

        startMap();
        return view;
    }

    private void startMap() {
        listLatLng = new ArrayList<>();
        listMarkerOptions = new ArrayList<>();

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
        googleMap.getUiSettings().setMapToolbarEnabled(false);
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
                case R.id.floatingActionButtonAddMarker: {
                    if (listLatLng.size() != 0 && !isFinish()) {
                        listLatLng.add(googleMap.getCameraPosition().target);
                        MarkerOptions marker = new MarkerOptions()
                                .position(listLatLng.get(listLatLng.size() - 1))
                                //.draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_marker));
                        googleMap.addMarker(marker);
                        listMarkerOptions.add(marker);

                        updateMapRoute();
                        checkActiveButtons();
                    }
                    break;
                }
                case R.id.floatingActionButtonStartFinish: {
                    if (listLatLng.size() == 0) {
                        listLatLng.add(googleMap.getCameraPosition().target);
                        MarkerOptions marker = new MarkerOptions()
                                .position(listLatLng.get(listLatLng.size() - 1))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_flag))
                                .title("Старт");
                        googleMap.addMarker(marker);
                        listMarkerOptions.add(marker);

                        checkActiveButtons();
                    } else {
                        listLatLng.add(googleMap.getCameraPosition().target);
                        MarkerOptions marker = new MarkerOptions()
                                .position(listLatLng.get(listLatLng.size() - 1))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_flag))
                                //.draggable(true)
                                .title("Финиш");
                        googleMap.addMarker(marker);
                        listMarkerOptions.add(marker);
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.creating);
                        buttonSave.setVisibility(View.VISIBLE);
                        buttonSave.startAnimation(animation);

                        updateMapRoute();
                        checkActiveButtons();
                    }
                    break;
                }
                case R.id.buttonSaveRoute: {
                    realProgress = 0;
                    showDialogWhereSave();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("onClickCreateRoute", e.getMessage());
        }
    }

    private void checkActiveButtons() {
        Log.d("ActiveButtons", "StartFinish" + listLatLng.size());
        if (listLatLng.size() == 0) {
            fabNewPoint.setEnabled(false);
            fabStartFinish.setEnabled(true);
            Log.d("ActiveButtons", "start");
        } else if (isFinish()){
            fabNewPoint.setEnabled(false);
            fabStartFinish.setEnabled(false);
            Log.d("ActiveButtons", "finish");
        } else {
            fabNewPoint.setEnabled(true);
            fabStartFinish.setEnabled(true);
            Log.d("ActiveButtons", "No start/finish");
        }
    }

    private void showDialogWhereSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Сохранение")
                .setMessage("Хотите сразу опубликовать этот маршрут?")
                .setCancelable(false)
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Вы можете сделать это в любое время в своём профиле", Toast.LENGTH_LONG).show();
                        try {
                            saveRoute(new ArrayList<String>(), false);
                        } catch (Exception e) {
                            Log.e("ERROR-dialogWhereSave1", e.getMessage());
                        }
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            showDialogTags();
                            dialogInterface.cancel();
                        } catch (Exception e) {
                            Log.e("ERROR-dialogWhereSave2", e.getMessage());
                        }
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();

    }

    private void showDialogTags() {
        final boolean[] booleanCheckedTags = {false, false, false, false};
        final ArrayList<String> checkedTags = new ArrayList<>();
        final ArrayList<String> tags = new ArrayList<>();
        tags.add("Прогулка");
        tags.add("Спорт");
        tags.add("Красиво");
        tags.add("Достопримечательности");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите подходящие теги")
                .setCancelable(false)
                .setMultiChoiceItems(tags.toArray(new String[0]), booleanCheckedTags,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked)
                                    checkedTags.add(tags.get(which));
                                else
                                    checkedTags.remove(tags.get(which));
                                booleanCheckedTags[which] = isChecked;
                            }
                        })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Вы можете сделать это в любое время в своём профиле", Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (booleanCheckedTags[0] || booleanCheckedTags[1]) {
                            if (booleanCheckedTags[0] && booleanCheckedTags[1]) {
                                Toast.makeText(getActivity(), "Выберите только один из тегов:\n'Прогулка' или 'Спорт'", Toast.LENGTH_LONG).show();
                            } else {
                                saveRoute(checkedTags, true);
                                dialogInterface.cancel();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Выберите тег 'Прогулка' или 'Спорт'", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .create()
                .show();
    }

    private void saveRoute(ArrayList<String> tags, boolean isPublic) {
        progressBar.setVisibility(View.VISIBLE);
        makeProgress(1);
        saveRouteOnServer(tags, isPublic);
        saveRouteOnSQLite();
    }

    private void saveRouteOnServer(List<String> tags, boolean isPublic) {
        tags.add(0, "Маршрут");
        List<Map<String, Double>> pointsList = new ArrayList<>();
        for (int i = 0; i < listLatLng.size(); i++) {
            Map<String, Double> doubleLatLngList = new HashMap<>();
            doubleLatLngList.put("latitude", listLatLng.get(i).latitude);
            doubleLatLngList.put("longitude", listLatLng.get(i).longitude);
            pointsList.add(doubleLatLngList);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //Toast.makeText(getActivity(), user.getUid(), Toast.LENGTH_SHORT).show();

        long milliseconds = Calendar.getInstance().getTimeInMillis();

        Route route = new Route(firebaseAuth.getCurrentUser().getUid(), user.getNickname(), milliseconds, pointsList, 0, tags, isPublic);

        FirebaseFirestore.getInstance().collection("routes")
                .add(route)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firebase", "DocumentSnapshot written with ID: " + documentReference.getId());
                        googleMap.clear();
                        makeProgress(50);
                        realProgress += 50;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase", "Error adding document", e);
                        makeProgress(50);
                        Toast.makeText(getActivity(), "Произошла ошибка!\nПроверьте подключение к сети", Toast.LENGTH_SHORT).show();
                        realProgress += 50;
                    }
                });
    }

    private void makeProgress(final int speed) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (realProgress != 0) {
                    while (progressBar.getProgress() <= realProgress) {
                        progressBar.setProgress(progressBar.getProgress() + 1);
                        try {
                            Thread.sleep(1000 / speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    while (progressBar.getProgress() < progressBar.getMax() * 0.9) {
                        progressBar.setProgress(progressBar.getProgress() + 1);
                        try {
                            Thread.sleep(1000 / speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                progressHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void saveRouteOnSQLite() {
        makeProgress(50);
        try {
            user.setUid(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            ((MainActivity) getActivity()).sqLiteHelper.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        realProgress += 50;
    }

    private void updateMapRoute() {
        PolylineOptions line = new PolylineOptions();
        line.add(listLatLng.get(listLatLng.size() - 1));
        line.add(listLatLng.get(listLatLng.size() - 2));
        line.width(15);
        line.endCap(new RoundCap());
        line.startCap(new RoundCap());
        line.color(0xaa2980b9);
        try {
            googleMap.addPolyline(line);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    private boolean isFinish() {
        for (int i = 0; i < listMarkerOptions.size(); i++)
            try {
                if (listMarkerOptions.get(i).getTitle().equals("Финиш"))
                    return true;
            } catch (Exception e) {
                Log.e("ERROR-MarkerOptions", "null object - ok");
            }
        return false;
    }
}
