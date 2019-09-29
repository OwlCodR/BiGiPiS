package com.bigipis.bigipis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class FragmentSignTabs extends Fragment implements TabLayout.OnTabSelectedListener {

    private View myInflatedView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_sign_tabs, container, false);

        viewPager = myInflatedView.findViewById(R.id.viewpagerSign);
        setupViewPager(viewPager);

        tabLayout = myInflatedView.findViewById(R.id.tableLayoutSign);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(this);

        setHasOptionsMenu(true);

        return myInflatedView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_exit).setVisible(false);
        menu.findItem(R.id.action_sign).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentLogin(), "Вход");
        adapter.addFragment(new FragmentRegisterStart(), "Регистрация");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) { return;}

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        return;
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        return;
    }
}
