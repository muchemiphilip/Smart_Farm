package com.muchemi.smartfarm.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.muchemi.smartfarm.activities.CropsFragment;
import com.muchemi.smartfarm.activities.HorticultureFragment;
import com.muchemi.smartfarm.activities.LivestockFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new CropsFragment();
        }
        else if (position == 1)
        {
            fragment = new LivestockFragment();
        }
        else if (position == 2)
        {
            fragment = new HorticultureFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Crops";
        }
        else if (position == 1)
        {
            title = "Livestock";
        }
        else if (position == 2)
        {
            title = "Horticulture";
        }
        return title;
    }
}
