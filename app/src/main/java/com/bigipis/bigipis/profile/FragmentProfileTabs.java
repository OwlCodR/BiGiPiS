package com.bigipis.bigipis.profile;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bigipis.bigipis.FragmentError;
import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.bigipis.bigipis.route.FragmentRoutes;
import com.bigipis.bigipis.source.NoTitleViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import static com.bigipis.bigipis.route.FragmentRoutes.NO_FILTER;
import static com.bigipis.bigipis.route.FragmentRoutes.TAG_DATA;
import static com.bigipis.bigipis.route.FragmentRoutes.TAG_FILTER;
import static com.bigipis.bigipis.route.FragmentRoutes.USER_FILTER;

public class FragmentProfileTabs extends Fragment implements TabLayout.OnTabSelectedListener {


    private View myInflatedView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView textViewUsername;
    private ImageView imageViewProfileIcon;
    private ProgressBar progressBarIcon;

    public final static String USER_ID = "ID";
    public final static String USER_ICON_ID = "ICON_ID";
    public final static String USER_NAME = "NAME";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Bundle waiting - USER_NAME, USER_ID, USER_ICON_ID

        myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);

        viewPager = myInflatedView.findViewById(R.id.viewpagerProfile);
        setupViewPager(viewPager);

        textViewUsername = myInflatedView.findViewById(R.id.textViewProfileName);
        imageViewProfileIcon = myInflatedView.findViewById(R.id.imageViewProfileIcon);
        progressBarIcon = myInflatedView.findViewById(R.id.progressBarProfileIcon);

        tabLayout = myInflatedView.findViewById(R.id.tableLayoutProfile);
        tabLayout.setupWithViewPager(viewPager);
        setupTab();
        tabLayout.addOnTabSelectedListener(this);

        setupProfile();

        setHasOptionsMenu(true);

        return myInflatedView;
    }

    private void setupProfile() {
        imageViewProfileIcon.setVisibility(View.GONE);
        progressBarIcon.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();

        if (bundle != null) {
            String username = getArguments().getString(USER_NAME);
            int iconId = getArguments().getInt(USER_ICON_ID);

            if (username != null && !username.isEmpty()) {
                textViewUsername.setText(getArguments().getString(USER_NAME));
            } else {
                textViewUsername.setText("Ошибка при загрузке имени профиля");
            }


            if (iconId == 1) {
                imageViewProfileIcon.setImageResource(R.mipmap.default_user_ico_1);
            } else if (iconId == 2) {
                imageViewProfileIcon.setImageResource(R.mipmap.default_user_ico_2);
            } else if (iconId == 3) {
                imageViewProfileIcon.setImageResource(R.mipmap.default_user_ico_3);
            } else if (iconId == 4) {
                imageViewProfileIcon.setImageResource(R.mipmap.default_user_ico_4);
            } else if (iconId == 5) {
                imageViewProfileIcon.setImageResource(R.mipmap.default_user_ico_5);
            } else if (iconId == 6) {
                imageViewProfileIcon.setImageResource(R.mipmap.default_user_ico_6);
            } else {
                imageViewProfileIcon.setImageResource(R.drawable.ic_warning_orange_48dp);
            }
            imageViewProfileIcon.setVisibility(View.VISIBLE);
            progressBarIcon.setVisibility(View.GONE);
        }
        else {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new FragmentError())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem sign = menu.findItem(R.id.action_sign);
        MenuItem exit = menu.findItem(R.id.action_exit);
        try {
            if (((MainActivity) Objects.requireNonNull(getActivity())).isUserSignedIn()) {
                sign.setVisible(false);
                exit.setVisible(true);
            } else {
                sign.setVisible(true);
                exit.setVisible(false);
            }
        } catch (Exception e) {
            sign.setVisible(false);
            exit.setVisible(false);
            Log.e("ERROR", e.getMessage());
        }
    }

    private void setupTab() {
        try {
            tabLayout.getTabAt(0).setIcon(R.mipmap.stat);
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).setIcon(R.mipmap.routes);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).setIcon(R.mipmap.achievements);
            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle newBundle = new Bundle();
        Bundle bundle = getArguments();

        if (bundle != null && bundle.getString(USER_ID) != null && !bundle.getString(USER_ID).isEmpty()) {
            newBundle.putString(TAG_FILTER, USER_FILTER);
            newBundle.putString(TAG_DATA, bundle.getString(USER_ID));
        }
        else {
            newBundle.putString(TAG_FILTER, NO_FILTER);
            newBundle.putString(TAG_DATA, null);
        }

        Fragment fragmentRoutes = new FragmentRoutes();
        fragmentRoutes.setArguments(newBundle);

        NoTitleViewPagerAdapter adapter = new NoTitleViewPagerAdapter(getChildFragmentManager(), getActivity());
        adapter.addFragment(new FragmentStatistic(), getResources().getString(R.string.stat));
        adapter.addFragment(fragmentRoutes, getResources().getString(R.string.routes));
        adapter.addFragment(new FragmentAchievements(), getResources().getString(R.string.achievements));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        try {
            Objects.requireNonNull(tab.getIcon()).setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Objects.requireNonNull(tab.getIcon()).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
