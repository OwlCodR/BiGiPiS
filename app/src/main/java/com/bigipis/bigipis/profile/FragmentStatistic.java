package com.bigipis.bigipis.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.R;

public class FragmentStatistic extends Fragment {
    private View myInflatedView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_statistic, container, false);



        return myInflatedView;
    }
}
