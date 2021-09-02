package com.bigipis.bigipis.tutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.R;

import java.util.List;
import java.util.Map;

public class FragmentTutorialStartNavigation extends FragmentTutorial {

    private View view;
    private Button buttonFinish;
    private List<Map<String, Double>> pointsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tutorial_start_navigation, container, false);
        super.textView = view.findViewById(R.id.textViewStartNavigation);

        Bundle bundle = getArguments();
        pointsList = (List<Map<String, Double>>) bundle.getSerializable("pointsList");

        buttonFinish = view.findViewById(R.id.buttonFinishTutorial);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(createUri()));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        return view;
    }

    private String createUri() {
        // | %7C
        // , %2C

        String uri = "https://www.google.com/maps/dir/?api=1";
        uri += "&origin=";
        uri += pointsList.get(0).get("latitude") + "%2C";
        uri += pointsList.get(0).get("longitude");

        uri += "&destination=";
        uri += pointsList.get(pointsList.size() - 1).get("latitude") + "%2C";
        uri += pointsList.get(pointsList.size() - 1).get("longitude");


        for (int i = 1; i < pointsList.size() - 1; i++) {
            if (i == 1){

                uri += "&waypoints=";
                uri += pointsList.get(i).get("latitude") + "%2C";
                uri += pointsList.get(i).get("longitude") + "%7C";

            } else if (i == pointsList.size() - 2) {

                uri += pointsList.get(i).get("latitude") + "%2C";
                uri += pointsList.get(i).get("longitude");

            } else {

                uri += pointsList.get(i).get("latitude") + "%2C";
                uri += pointsList.get(i).get("longitude") + "%7C";

            }
        }

        Log.i("FragmentMapRoutes", "uri = " + uri);
        return uri;
    }
}

