package com.bigipis.bigipis.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


public class FragmentMapMainMenu extends Fragment implements View.OnClickListener {
    private View myInflateView;
    private FloatingActionButton fabRoutes, fabCreateRoute;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflateView = inflater.inflate(R.layout.fragment_map_main_menu, container, false);
        fabRoutes = myInflateView.findViewById(R.id.floatingActionButtonNewMarker);
        fabCreateRoute = myInflateView.findViewById(R.id.floatingActionButtonStartFinish);

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
                case R.id.floatingActionButtonStartFinish:
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.content_frame, new FragmentMapCreateRoute())
                            .remove(this)
                            .addToBackStack(null)
                            .commit();
                    break;
                }
                case R.id.floatingActionButtonNewMarker:
                {
                    break;
                }
            }
        } else {
            mainActivity.showDialogAccessError();
        }
    }
}
