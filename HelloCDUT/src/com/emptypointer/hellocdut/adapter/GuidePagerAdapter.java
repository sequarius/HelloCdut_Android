package com.emptypointer.hellocdut.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.fragment.ImageFragment;

public class GuidePagerAdapter extends FragmentPagerAdapter {
    protected static final int[] ICONS = new int[]{
            R.drawable.rc_guide_image_1, R.drawable.rc_guide_image_2,
            R.drawable.rc_guide_image_3, R.layout.fragment_drawable_with_bottom_button,

    };

    public GuidePagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return new ImageFragment(ICONS[arg0]);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ICONS.length;
    }

}
