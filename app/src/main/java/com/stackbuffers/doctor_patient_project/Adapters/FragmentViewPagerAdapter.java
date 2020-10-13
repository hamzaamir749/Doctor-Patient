package com.stackbuffers.doctor_patient_project.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    final List<Fragment> FragmentList=new ArrayList<>();
    final List<String> FragmentTitles=new ArrayList<>();

    public FragmentViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentTitles.get(position);
    }

    public void AddFragments(Fragment fragment,String title)
    {
        FragmentList.add(fragment);
        FragmentTitles.add(title);
    }
}
