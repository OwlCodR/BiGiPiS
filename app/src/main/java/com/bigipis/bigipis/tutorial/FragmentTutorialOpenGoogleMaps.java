package com.bigipis.bigipis.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.R;

public class FragmentTutorialOpenGoogleMaps extends FragmentTutorial {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tutorial_gmap, container, false);
        super.textView = view.findViewById(R.id.textViewOpenGoogleMaps);
        super.startAnimation();
        return view;
    }
}

