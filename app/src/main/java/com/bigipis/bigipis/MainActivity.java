package com.bigipis.bigipis;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

import static com.bigipis.bigipis.FragmentProfileTabs.USER_ID;
import static com.bigipis.bigipis.FragmentProfileTabs.USER_NAME;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    final private int PERMISSION_CODE = 1;

    public static User user;
    public FirebaseFirestore fDatabase;
    public BluetoothService service;
    public GoogleMap googleMap;
    public boolean isNakersConnected;
    //public static TextView textViewBattery;
    //public static TextView textViewStick;
    //private String API_KEY = getResources().getString(R.string.google_api_key);

    private FirebaseAuth firebaseAuth;
    private ConstraintLayout constraintLayoutEntry;
    private ConstraintLayout constraintLayoutUser;
    private LinearLayout linearLayoutNavHeader;
    private DrawerLayout drawer;
    private ProgressBar progressBarHeader;
    private ImageView imageViewIcon;
    private TextView textViewRating;
    private TextView textViewNickname;
    private ImageView imageViewNakersConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = new User();

        setDrawer(toolbar);

        setHeader();

        setBluetoothConfig();

        startMap();
    }

    private void setDrawer(Toolbar toolbar) {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    //Updating UserDrawerInfo
                    if (firebaseAuth.getCurrentUser() != null) {
                        updateUI();
                    }
                }
                return true; //@TODO True or false
            }
        });
    }

    private void setHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        textViewNickname = headerView.findViewById(R.id.textViewNavNickname);
        progressBarHeader = headerView.findViewById(R.id.progressBarHeader);
        //textViewBattery = headerView.findViewById(R.id.textViewNavBattery);
        textViewRating = headerView.findViewById(R.id.textViewNavRating);
        //textViewStick = headerView.findViewById(R.id.textViewNavStick);
        imageViewIcon = headerView.findViewById(R.id.imageViewNavUserIcon);
        constraintLayoutEntry = headerView.findViewById(R.id.layoutEntry);
        constraintLayoutUser = headerView.findViewById(R.id.layoutUser);
        linearLayoutNavHeader = headerView.findViewById(R.id.linearNavHeader);
        imageViewNakersConnected = headerView.findViewById(R.id.imageViewNakersConnected);
        imageViewNakersConnected.setVisibility(View.GONE);

        linearLayoutNavHeader.setOnClickListener(this);
    }

    private void setBluetoothConfig() {
        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = this;
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "BiGiPiS";
        config.callListenersInMainThread = true;
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        BluetoothService.init(config);
        service = BluetoothService.getDefaultInstance();
    }

    private void startMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map_fragment);
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mapFragment)
                    .add(R.id.content_frame, new FragmentMap())
                    .addToBackStack(null)
                    .commit();
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FragmentError())
                    .addToBackStack(null)
                    .commit();
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            onResume();
        } else
            super.onBackPressed();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sign = menu.findItem(R.id.action_sign);
        MenuItem exit = menu.findItem(R.id.action_exit);
        if (isUserSignedIn()) {
            sign.setVisible(false);
            exit.setVisible(true);
        } else {
            sign.setVisible(true);
            exit.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentSignTabs();
        if (id == R.id.action_settings) {
            fragment = new FragmentSettings();
        } else if (id == R.id.action_sign) {
            fragment = new FragmentSignTabs();
        } else if (id == R.id.action_exit) {
            FirebaseAuth.getInstance().signOut();
            updateUI();
            fragment = new FragmentSignTabs();
        }

        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.commit();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            if (isUserSignedIn()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString(USER_ID, firebaseAuth.getUid());
                bundle.putString(USER_NAME, user.getNickname());

                Fragment fragment = new FragmentProfileTabs();
                fragment.setArguments(bundle);
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                View layout = inflater.inflate(R.layout.dialog_access_error, (ViewGroup) findViewById(R.id.linearLayoutAccessError));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);

                builder.setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new FragmentSignTabs();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }).create().show();
            }
        } else if (id == R.id.nav_routes) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new FragmentRoutes();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_map) {
            startMap();
        } else if (id == R.id.nav_settings) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new FragmentSettings();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_dev) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new FragmentDeveloper();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        // HeaderClick
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        drawer.closeDrawer(GravityCompat.START);
        if (isUserSignedIn()) {
            Fragment fragment = new FragmentSignTabs();
            ft.replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Fragment fragment = new FragmentProfileTabs();
            ft.replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public boolean isUserSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void updateUI() {
        firebaseAuth = FirebaseAuth.getInstance();

        if (isNakersConnected)
            imageViewNakersConnected.setVisibility(View.VISIBLE);
        else
            imageViewNakersConnected.setVisibility(View.GONE);

        if (isUserSignedIn()) {
            user.setUid(firebaseAuth.getUid());

            fDatabase = FirebaseFirestore.getInstance();
            Log.i("INFO", firebaseAuth.getUid());
            Log.i("INFO", firebaseAuth.getCurrentUser().getUid());
            if (fDatabase != null) {
                DocumentReference docRef = fDatabase.collection("users").document(Objects.requireNonNull(user.getUid()));
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);

                        if (user != null) {
                            Log.i("INFO", "Info successfully has been got!");

                            textViewNickname.setText(user.getNickname());
                            textViewRating.setText("Рейтинг: " + user.getRating());

                            imageViewIcon.setVisibility(View.VISIBLE);
                            progressBarHeader.setVisibility(View.GONE);
                            switch (user.getIconId()) {
                                case 1: {
                                    imageViewIcon.setImageResource(R.mipmap.default_user_ico_1);
                                    break;
                                }
                                case 2: {
                                    imageViewIcon.setImageResource(R.mipmap.default_user_ico_2);
                                    break;
                                }
                                case 3: {
                                    imageViewIcon.setImageResource(R.mipmap.default_user_ico_3);
                                    break;
                                }
                                case 4: {
                                    imageViewIcon.setImageResource(R.mipmap.default_user_ico_4);
                                    break;
                                }
                                case 5: {
                                    imageViewIcon.setImageResource(R.mipmap.default_user_ico_5);
                                    break;
                                }
                                case 6: {
                                    imageViewIcon.setImageResource(R.mipmap.default_user_ico_6);
                                    break;
                                }
                            }
                        } else {
                            Log.e("ERROR", "user == null");
                            user = new User();
                            imageViewIcon.setVisibility(View.INVISIBLE);
                            progressBarHeader.setVisibility(View.VISIBLE);
                        }
                    }
                });

                constraintLayoutEntry.setVisibility(View.GONE);
                constraintLayoutUser.setVisibility(View.VISIBLE);
            }
        } else {
            constraintLayoutEntry.setVisibility(View.VISIBLE);
            constraintLayoutUser.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Внимание")
                    .setMessage("Для работы карты необходимо разрешение о местоположении устройства!\nЭти данные используются исключительно для вашей навигации!")
                    .setCancelable(true)
                    .setNegativeButton("Нет, мне не нужна карта", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Очень жаль, теперь карта не работает!\nНо вы всё еще можете дать разрешение в настройках", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
                        }
                    })
                    .create()
                    .show();
        }
        else {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setOnMyLocationClickListener(this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CODE && permissions.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение успешно получено!\n", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Очень жаль, теперь карта не работает!\nНо вы всё еще можете дать разрешение в настройках", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}

// @TODO Do normal start check if user != null and do nav_header