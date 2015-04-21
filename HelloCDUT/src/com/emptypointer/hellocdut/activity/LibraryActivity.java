package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.fragment.LibraryBorrowedFragment;
import com.emptypointer.hellocdut.fragment.LibraryInfoFragment;
import com.emptypointer.hellocdut.fragment.LibraryOnBorrowFragment;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class LibraryActivity extends BaseActivity implements OnClickListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private TextView mTvInfo, mTvOnBorrow, mTvBorrowed;
    private ImageView mIndicator;
    private int mTabLineWith;
    private int mCurrentPageIndex;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_library);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new LibraryOnBorrowFragment());
        mFragments.add(new LibraryBorrowedFragment());
        mFragments.add(new LibraryInfoFragment());
        mIndicator = (ImageView) findViewById(R.id.imageView_under_line);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                // TODO Auto-generated method stub
                return mFragments.get(arg0);
            }
        };
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mAdapter);
        mTvInfo = (TextView) findViewById(R.id.textView_info);
        mTvOnBorrow = (TextView) findViewById(R.id.textView_on_borrow);
        mTvBorrowed = (TextView) findViewById(R.id.textView_borrowed);


        mTvInfo.setOnClickListener(this);
        mTvOnBorrow.setOnClickListener(this);
        mTvBorrowed.setOnClickListener(this);

        mTabLineWith = getScreenWith() / 3;
        LayoutParams params = mIndicator.getLayoutParams();
        params.width = mTabLineWith;
        mIndicator.setLayoutParams(params);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                resetColor();
                switch (arg0) {
                    case 0:
                        mTvInfo.setTextColor(getResources().getColor(
                                R.color.color_ep_blue));
                        break;
                    case 1:
                        mTvOnBorrow.setTextColor(getResources().getColor(
                                R.color.color_ep_blue));
                        break;
                    case 2:
                        mTvBorrowed.setTextColor(getResources().getColor(
                                R.color.color_ep_blue));
                        break;

                    default:
                        break;
                }
                mCurrentPageIndex = arg0;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPx) {
                // TODO Auto-generated method stub
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mIndicator
                        .getLayoutParams();

                if (mCurrentPageIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (positionOffset * mTabLineWith + mCurrentPageIndex
                            * mTabLineWith);
                } else if (mCurrentPageIndex == 1 && position == 0)// 1->0
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mTabLineWith + (positionOffset - 1)
                            * mTabLineWith);
                } else if (mCurrentPageIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mTabLineWith + positionOffset
                            * mTabLineWith);
                } else if (mCurrentPageIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mTabLineWith + (positionOffset - 1)
                            * mTabLineWith);
                }
                mIndicator.setLayoutParams(lp);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            private void resetColor() {
                mTvInfo.setTextColor(getResources().getColor(
                        R.color.color_ep_blank));
                mTvOnBorrow.setTextColor(getResources().getColor(
                        R.color.color_ep_blank));
                mTvBorrowed.setTextColor(getResources().getColor(
                        R.color.color_ep_blank));
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, QueryBookActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.library, menu);

//	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
//			
//			@Override
//			public boolean onQueryTextSubmit(String query) {
//				// TODO Auto-generated method stub
//				new CommitTask().execute(query);
//				return false;
//			}
//			
//			@Override
//			public boolean onQueryTextChange(String newText) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
//	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default、

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.textView_info:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.textView_on_borrow:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.textView_borrowed:
                mViewPager.setCurrentItem(2);
                break;

            default:
                break;
        }
    }

}
