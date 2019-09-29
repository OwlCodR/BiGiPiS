package com.bigipis.bigipis;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback {

    public static User user;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseFirestore fDatabase;
    private static ConstraintLayout constraintLayoutEntry;
    private static ConstraintLayout constraintLayoutUser;
    private LinearLayout linearLayoutNavHeader;
    private DrawerLayout drawer;
    public static ImageView imageViewNakersConnected;
    public static TextView textViewNickname;
    private static ProgressBar progressBarHeader;
    //public static TextView textViewBattery;
    public static TextView textViewRating;
    //public static TextView textViewStick;
    public static ImageView imageViewIcon;
    public static BluetoothService service;
    //private String API_KEY = getResources().getString(R.string.google_api_key);
    private int width;
    final private int PERMISSION_CODE = 1;
    public static boolean isNakersConnected;


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
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mapFragment)
                    .addToBackStack(null)
                    .commit();
        }
        mapFragment.getMapAsync(this);
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
        if (firebaseAuth.getCurrentUser() != null) {
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
            if (firebaseAuth.getCurrentUser() == null) {
                LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                View layout = inflater.inflate(R.layout.dialog_access_error, (ViewGroup) findViewById(R.id.linearLayoutAccessError));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new FragmentSignTabs();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }).create();
            } else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString(USER_ID, firebaseAuth.getUid());
                bundle.putString(USER_NAME, user.getNickname());

                Fragment fragment = new FragmentProfileTabs();
                fragment.setArguments(bundle);
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        } else if (id == R.id.nav_routes) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new FragmentRoutes();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_map) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map_fragment);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, mapFragment)
                        .addToBackStack(null)
                        .commit();
            }
            mapFragment.getMapAsync(this);

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
        if (constraintLayoutEntry.getVisibility() == View.VISIBLE) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new FragmentSignTabs();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // @TODO Start Fragment Profile
        }
    }

    public static void updateUI() {
        firebaseAuth = FirebaseAuth.getInstance();

        if (isNakersConnected)
            imageViewNakersConnected.setVisibility(View.VISIBLE);
        else
            imageViewNakersConnected.setVisibility(View.GONE);

        if (firebaseAuth.getCurrentUser() == null) {
            constraintLayoutEntry.setVisibility(View.VISIBLE);
            constraintLayoutUser.setVisibility(View.GONE);
        } else {
            user.setUid(firebaseAuth.getUid());

            fDatabase = FirebaseFirestore.getInstance();
            Log.i("User", firebaseAuth.getUid());
            Log.i("User", firebaseAuth.getCurrentUser().getUid());
            if (fDatabase != null) {
                DocumentReference docRef = fDatabase.collection("users").document(Objects.requireNonNull(user.getUid()));
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);

                    if (user != null) {
                        Log.i("User", "Info successfully has been getted!");

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
                        Log.i("User", "user == null");
                        user = new User();
                        imageViewIcon.setVisibility(View.INVISIBLE);
                        progressBarHeader.setVisibility(View.VISIBLE);
                    }
                }
            });

            constraintLayoutEntry.setVisibility(View.GONE);
            constraintLayoutUser.setVisibility(View.VISIBLE);

            /*
            if (!user.getSerial_S().equals("") || !user.getSerial_W().equals("")) {
                textVhaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaewStick.setVisibility(View.VISIBLE);
                textViewBattery.setVisibility(View.VISIBLE);
                textViewBattery.setText("Батарея: " + user.getBattery());
            } else {
                textViewBattery.setVisibility(View.GONE);
                textViewStick.setVisibility(View.GONE);
            }
            */
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        else googleMap.setMyLocationEnabled(true);
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
}

// @TODO Do normal start check if user != null and do nav_header