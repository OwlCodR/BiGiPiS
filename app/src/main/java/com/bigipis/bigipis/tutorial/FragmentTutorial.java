package com.bigipis.bigipis.tutorial;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.R;

public class FragmentTutorial extends Fragment {

    TextView textView;

    public void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.creating);
        textView.startAnimation(animation);
    }
}
