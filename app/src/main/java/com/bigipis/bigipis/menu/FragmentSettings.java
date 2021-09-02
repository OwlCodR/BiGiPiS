package com.bigipis.bigipis.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.bigipis.bigipis.bluetooth.FragmentBluetoothList;
import com.bigipis.bigipis.bluetooth.FragmentTestLacers;
import com.google.android.material.snackbar.Snackbar;

public class FragmentSettings extends Fragment implements View.OnClickListener {
    private View view;
    private CardView cardViewTest, cardViewConnect;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container,false);
        cardViewTest = view.findViewById(R.id.cardViewTest);
        cardViewTest.setOnClickListener(this);
        cardViewConnect = view.findViewById(R.id.cardViewConnect);
        cardViewConnect.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardViewTest:
            {
                if (((MainActivity) getActivity()).isLacersConnected) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new FragmentTestLacers();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Snackbar.make(view, "Для начала подключите Lacers!", Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
            case  R.id.cardViewConnect:
            {
                if (!((MainActivity) getActivity()).isLacersConnected) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new FragmentBluetoothList();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Snackbar.make(view, "Lacers уже подключены!", Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem sign = menu.findItem(R.id.action_sign);
        MenuItem exit = menu.findItem(R.id.action_exit);
        MenuItem settings = menu.findItem(R.id.action_settings);
        try {
            if (((MainActivity) getActivity()).isUserSignedIn()) {
                sign.setVisible(false);
                exit.setVisible(true);
            } else {
                sign.setVisible(true);
                exit.setVisible(false);
            }
            settings.setVisible(false);
        } catch (Exception e) {
            sign.setVisible(false);
            exit.setVisible(false);
            Log.e("ERROR", e.getMessage());
        }
        super.onPrepareOptionsMenu(menu);
    }
}
