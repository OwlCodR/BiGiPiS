package com.bigipis.bigipis.menu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.R;
import com.google.android.material.snackbar.Snackbar;

public class FragmentDeveloper extends Fragment implements View.OnClickListener {

    private View view;
    private ImageView imageViewVk, imageViewInstagram, imageViewGmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_developers, container, false);
        imageViewVk = (ImageView) view.findViewById(R.id.imageViewVK);
        imageViewGmail = (ImageView) view.findViewById(R.id.imageViewGmail);
        imageViewInstagram = (ImageView) view.findViewById(R.id.imageViewInstagram);

        imageViewVk.setOnClickListener(this);
        imageViewGmail.setOnClickListener(this);
        imageViewInstagram.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewVK: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vk.com/bigipis"));
                startActivity(browserIntent);
                break;
            }
            case R.id.imageViewInstagram: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/bigipis"));
                startActivity(browserIntent);
                break;
            }
            case R.id.imageViewGmail: {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("BiGiPiS Gmail", "bigipis.app@gmail.com");
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Почта bigipis.app@gmail.com успешно скопирована", Snackbar.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
