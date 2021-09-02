package com.bigipis.bigipis;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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

import com.bigipis.bigipis.authentication.FragmentSignTabs;
import com.bigipis.bigipis.bluetooth.BluetoothLeService;
import com.bigipis.bigipis.map.FragmentMapMainMenu;
import com.bigipis.bigipis.menu.FragmentDeveloper;
import com.bigipis.bigipis.menu.FragmentSettings;
import com.bigipis.bigipis.profile.FragmentProfileTabs;
import com.bigipis.bigipis.route.FragmentRoutes;
import com.bigipis.bigipis.source.SQLiteHelper;
import com.bigipis.bigipis.source.User;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

import static com.bigipis.bigipis.profile.FragmentProfileTabs.USER_ICON_ID;
import static com.bigipis.bigipis.profile.FragmentProfileTabs.USER_ID;
import static com.bigipis.bigipis.profile.FragmentProfileTabs.USER_NAME;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback {

    final private int PERMISSION_CODE = 1;
    final public static String FRAGMENT_TAG = "MAP_FRAGMENT";
    public static User user;
    public FirebaseFirestore fDatabase;
    public SQLiteHelper sqLiteHelper;
    public GoogleMap googleMap;
    public boolean isLacersConnected;
    public ImageView imageViewBattery;
    public static int batteryState;
    // 3 - 100%
    // 2 - 60%
    // 1 - 20%
    // 0 - Unknown
    private TextView textViewStick;
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
    private ImageView imageViewLacersConnected;
    private boolean isCameraSetOnce; // Just a flag to set camera only 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = new User();
        isLacersConnected = false;
        isCameraSetOnce = false;
        batteryState = 0;
        sqLiteHelper = new SQLiteHelper(this);

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
                return true;
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
        imageViewBattery = headerView.findViewById(R.id.imageViewBattery);
        textViewStick = headerView.findViewById(R.id.textViewNavStick);
        imageViewIcon = headerView.findViewById(R.id.imageViewNavUserIcon);
        constraintLayoutEntry = headerView.findViewById(R.id.layoutEntry);
        constraintLayoutUser = headerView.findViewById(R.id.layoutUser);
        linearLayoutNavHeader = headerView.findViewById(R.id.linearNavHeader);
        imageViewLacersConnected = headerView.findViewById(R.id.imageViewLacersConnected);
        imageViewLacersConnected.setVisibility(View.GONE);

        linearLayoutNavHeader.setOnClickListener(this);
    }

    private void setBluetoothConfig() {
        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothLeService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "BiGiPiS";
        config.callListenersInMainThread = true;

        config.uuidService = UUID.fromString("00001523-be61-11e7-8f1a-0800200c9a66"); // Required
        config.uuidCharacteristic = UUID.fromString("00001524-be61-11e7-8f1a-0800200c9a66"); // Required
        config.transport = BluetoothDevice.TRANSPORT_LE; // Required for dual-mode devices
        //config.uuid = UUID.fromString(null); // Used to filter found devices. Set null to find all devices.

        BluetoothLeService.init(config);
    }

    private void startMap() {
        isCameraSetOnce = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mapFragment, FRAGMENT_TAG)
                    .add(R.id.content_frame, new FragmentMapMainMenu())
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
        Fragment fragment = null;
        if (id == R.id.action_settings) {
            fragment = new FragmentSettings();
        } else if (id == R.id.action_sign) {
            fragment = new FragmentSignTabs();
        } else if (id == R.id.action_exit) {
            FirebaseAuth.getInstance().signOut();
            updateUI();
            fragment = new FragmentSignTabs();
        }

        if (fragment != null) {
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
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
                bundle.putString(USER_ID, firebaseAuth.getCurrentUser().getUid());
                bundle.putString(USER_NAME, user.getNickname());
                bundle.putInt(USER_ICON_ID, user.getIconId());

                Fragment fragment = new FragmentProfileTabs();
                fragment.setArguments(bundle);
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                showDialogAccessError();
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
            Bundle bundle = new Bundle();
            bundle.putString(USER_ID, firebaseAuth.getCurrentUser().getUid());
            bundle.putString(USER_NAME, user.getNickname());
            bundle.putInt(USER_ICON_ID, user.getIconId());

            Fragment fragment = new FragmentProfileTabs();
            fragment.setArguments(bundle);
            ft.replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Fragment fragment = new FragmentSignTabs();
            ft.replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void setBatteryImage() {
        if (batteryState == 1) {
            imageViewBattery.setImageResource(R.drawable.ic_battery_20_white_24dp);
        } else if (batteryState == 2) {
            imageViewBattery.setImageResource(R.drawable.ic_battery_60_white_24dp);
        } else if (batteryState == 3) {
            imageViewBattery.setImageResource(R.drawable.ic_battery_full_white_24dp);
        } else {
            imageViewBattery.setImageResource(R.drawable.ic_battery_unknown_white_24dp);
            Log.e("BATTERY", "Something wrong with battery data! Battery = " + batteryState);
        }
    }

    public boolean isUserSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void updateUI() {
        firebaseAuth = FirebaseAuth.getInstance();

        if (isLacersConnected) {
            imageViewLacersConnected.setVisibility(View.VISIBLE);
            imageViewBattery.setVisibility(View.VISIBLE);
            textViewStick.setVisibility(View.VISIBLE);

            setBatteryImage();
        }
        else {
            imageViewLacersConnected.setVisibility(View.GONE);
            imageViewBattery.setVisibility(View.GONE);
            textViewStick.setVisibility(View.GONE);
        }

        if (isUserSignedIn()) {
            user.setUid(firebaseAuth.getCurrentUser().getUid());

            fDatabase = FirebaseFirestore.getInstance();
            Log.i("INFO", firebaseAuth.getCurrentUser().getUid());   // Similar
            Log.i("INFO", firebaseAuth.getCurrentUser().getUid()); // Similar
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

    public void showDialogAccessError() {
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
        })
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showDialogMapPermission();
        }
        else {
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    if (!isCameraSetOnce) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 17);
                        googleMap.animateCamera(cameraUpdate);
                        isCameraSetOnce = true;
                    }
                }
            });

            googleMap.setMyLocationEnabled(true);
            googleMap.setMaxZoomPreference(20f);
            //googleMap.setOnMyLocationClickListener(this);
        }
    }

    public void showDialogMapPermission() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CODE && permissions.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение успешно получено!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Очень жаль, теперь карта не работает!\nНо вы всё еще можете дать разрешение в настройках", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}