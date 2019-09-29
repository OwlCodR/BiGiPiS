package com.bigipis.bigipis;

import android.graphics.PorterDuff;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import static com.bigipis.bigipis.FragmentRoutes.USER_FILTER;
import static com.bigipis.bigipis.MainActivity.firebaseAuth;

public class FragmentProfileTabs extends Fragment implements TabLayout.OnTabSelectedListener {

    // @TODO Get info about profile with Bundle

    private View myInflatedView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    final public static String USER_ID = "ID";
    final public static String USER_NAME = "NAME";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_profile_tabs, container, false);

        viewPager = myInflatedView.findViewById(R.id.viewpagerProfile);
        setupViewPager(viewPager);

        tabLayout = myInflatedView.findViewById(R.id.tableLayoutProfile);
        tabLayout.setupWithViewPager(viewPager);
        setupTab();
        tabLayout.addOnTabSelectedListener(this);

        setHasOptionsMenu(true);

        return myInflatedView;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem sign = menu.findItem(R.id.action_sign);
        MenuItem exit = menu.findItem(R.id.action_exit);
        if (firebaseAuth.getCurrentUser() != null) {
            sign.setVisible(false);
            exit.setVisible(true);
        } else {
            sign.setVisible(true);
            exit.setVisible(false);
        }
    }

    private void setupTab() {
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getArguments().getString(USER_NAME));
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setSubtitle(R.string.stat);

        tabLayout.getTabAt(0).setIcon(R.mipmap.stat);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.mipmap.routes);
        tabLayout.getTabAt(2).setIcon(R.mipmap.achievements);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle bundle = new Bundle();
        if (getArguments() != null)
            bundle.putString(USER_FILTER, getArguments().getString(USER_ID));
        else
            bundle.putString(USER_FILTER, null);

        Fragment fragmentRoutes = new FragmentRoutes();
        fragmentRoutes.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentStatistic(), getResources().getString(R.string.stat));
        adapter.addFragment(fragmentRoutes, getResources().getString(R.string.routes));
        adapter.addFragment(new FragmentAchievements(), getResources().getString(R.string.achievements));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition())
        {
            case 0: {
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setSubtitle(R.string.stat);
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                break;
            }
            case 1: {
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setSubtitle(R.string.routes);
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                break;
            }
            case 2: {
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setSubtitle(R.string.achievements);
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.getIcon().setColorFilter(getResources().getColor(R.color.colorDivider), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        return;
    }
}

// @TODO update header(rating) when user wants it (call updateUI)