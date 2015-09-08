package com.emptypointer.hellocdut.adapter;


 import android.support.v4.app.Fragment;
 import android.support.v4.app.FragmentManager;
 import android.support.v4.app.FragmentPagerAdapter;

 import java.util.List;

 /*** Created by Sequarius on 2015/4/12.
 */
public class EPSimpleViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTitles;

    public EPSimpleViewPagerAdapter(FragmentManager fm, List<Fragment> mFragments, List<String> mTitles) {
        super(fm);
        this.mFragments = mFragments;
        this.mTitles = mTitles;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

//    public CharSequence getPageTitle(int position) {
//        //重要，用于让PagerTitleStrip显示相关标题
//        return mTitles.get(position);
//    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
