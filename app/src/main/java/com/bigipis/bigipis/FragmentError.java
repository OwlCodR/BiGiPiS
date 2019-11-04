package com.bigipis.bigipis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentError extends Fragment {

    private View view;
    private TextView textViewError, textViewJoke;
    private ImageView imageViewLogo;
    private Animation animationAlphaShort, animationAlphaLong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_error, container, false);
        textViewError = (TextView) view.findViewById(R.id.textViewError);
        textViewJoke = (TextView) view.findViewById(R.id.textViewJoke);
        imageViewLogo = (ImageView) view.findViewById(R.id.imageViewLogoError);
        animationAlphaLong = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_long);
        animationAlphaShort = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_short);

        textViewError.startAnimation(animationAlphaShort);
        textViewJoke.startAnimation(animationAlphaLong);
        imageViewLogo.startAnimation(animationAlphaShort);
        return view;
    }
}
