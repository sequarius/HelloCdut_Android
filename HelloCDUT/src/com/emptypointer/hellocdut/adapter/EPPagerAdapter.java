package com.emptypointer.hellocdut.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.fragment.ContacatsFragment;
import com.emptypointer.hellocdut.fragment.FunctionFragment;
import com.emptypointer.hellocdut.fragment.NotifyFragment;
import com.emptypointer.hellocdut.fragment.UserInfoFragment;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class EPPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    protected static final int[] ICONS = new int[]{
            R.drawable.selector_index_tab_function_list,
            R.drawable.selector_index_tab_contacts,
            R.drawable.selector_index_tab_notify,
            R.drawable.selector_index_tab_user_info,

    };
    private List<Fragment> mFragments;
    private int mCount = ICONS.length;
    private NotifyFragment mNotifyFragment;
    private ContacatsFragment mContactFragment;

    public EPPagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new FunctionFragment());
        mContactFragment = new ContacatsFragment();
        mFragments.add(mContactFragment);
        mNotifyFragment = new NotifyFragment();
        mFragments.add(mNotifyFragment);
        mFragments.add(new UserInfoFragment());


    }

    public void refreshNotify() {

        mNotifyFragment.refresh();
    }

    public void refreshContact() {
        mContactFragment.refresh();
    }

    @Override
    public int getIconResId(int index) {
        // TODO Auto-generated method stub
        return ICONS[index % ICONS.length];
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mCount;
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return mFragments.get(arg0);
    }


}
