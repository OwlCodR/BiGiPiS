package com.bigipis.bigipis.source;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bigipis.bigipis.FragmentError;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> tabNamesList = new ArrayList<>();
    private Fragment fragmentError = new FragmentError();

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return mFragmentList.get(position);
        } catch (Exception e) {
            Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            return fragmentError;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            return tabNamesList.get(position);
        } catch (Exception e) {
            return "Ошибка";
        }
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        tabNamesList.add(title);
    }
}
