package com.emptypointer.hellocdut.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.EPSimpleViewPagerAdapter;
import com.emptypointer.hellocdut.fragment.ResetPwdByAAOFragment;
import com.emptypointer.hellocdut.fragment.ResetPwdByMailFragment;

import java.util.ArrayList;
import java.util.List;

public class RetrieveActivity extends BaseActivity implements View.OnClickListener,ResetPwdByAAOFragment.OnFragmentInteractionListener,ResetPwdByMailFragment.OnFragmentInteractionListener {
    public static final String PREFERENCE_SETTING="setting";

    private List<Fragment> mFragments;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private List<String> mTitle;


    private TextView mTvRecommand, mTvPublication;
    private View mVUnderLineRecommand, mVUnderLinePublication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTvRecommand = (TextView) findViewById(R.id.textView_recommand);
        mTvPublication = (TextView) findViewById(R.id.textView_publication);
        mVUnderLineRecommand = findViewById(R.id.view_underline_recommand);
        mVUnderLinePublication = findViewById(R.id.view_underline_publication);
        mFragments = new ArrayList<>();
        mTvRecommand.setOnClickListener(this);
        mTvPublication.setOnClickListener(this);
        mFragments.add(ResetPwdByAAOFragment.newInstance());
        mFragments.add(ResetPwdByMailFragment.newInstance());
        mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.str_reset_by_aao));
        mTitle.add(getString(R.string.str_reset_by_mail));
        mAdapter = new EPSimpleViewPagerAdapter(getSupportFragmentManager(), mFragments, mTitle);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    chooseRecommand();
                } else {
                    choosePublication();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        chooseRecommand();

//        BlankFragment blankFragment=new BlankFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_recommand) {
            mViewPager.setCurrentItem(0);
            chooseRecommand();
        } else if (v.getId() == R.id.textView_publication) {
            mViewPager.setCurrentItem(1);
            choosePublication();
        }
    }

    private void choosePublication() {
        mTvRecommand.setTextColor(getResources().getColor(R.color.color_ep_blank));
        mTvPublication.setTextColor(getResources().getColor(R.color.color_ep_blue));
        mVUnderLinePublication.setBackgroundColor(getResources().getColor(R.color.color_ep_blue));
        mVUnderLineRecommand.setBackgroundColor(getResources().getColor(R.color.color_ep_white));
    }

    private void chooseRecommand() {
        mTvRecommand.setTextColor(getResources().getColor(R.color.color_ep_blue));
        mTvPublication.setTextColor(getResources().getColor(R.color.color_ep_blank));
        mVUnderLineRecommand.setBackgroundColor(getResources().getColor(R.color.color_ep_blue));
        mVUnderLinePublication.setBackgroundColor(getResources().getColor(R.color.color_ep_white));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
