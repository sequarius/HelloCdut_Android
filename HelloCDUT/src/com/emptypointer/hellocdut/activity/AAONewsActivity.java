package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.AAOListAdapter;
import com.emptypointer.hellocdut.domain.AAONewsItem;
import com.emptypointer.hellocdut.service.EPAAONewsService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AAONewsActivity extends BaseActivity {

    protected static final String CACHE_AAOTIME = "aao_time_cache";
    private PullToRefreshListView mListView;
    private boolean mOnRefresh = false;

    private EPAAONewsService mNewsGetter;
    private List<AAONewsItem> mItems;

    private AAOListAdapter mAdapter;

    private int mCurrentPage = 1;

    private PtrFrameLayout mPtrFrame;

    private boolean mOntop;

    private TextView mTvLastTime;
    private EPTimeService mTimeService;


    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_aao_news);


        mTimeService = new EPTimeService(this);
        mTvLastTime = (TextView) findViewById(R.id.textView_last_updated);

        mListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);

        mNewsGetter = new EPAAONewsService(this);
        mItems = mNewsGetter.getmNewsList();
        mAdapter = new AAOListAdapter(this, mItems);
        mListView.setAdapter(mAdapter);


        mPtrFrame = (PtrFrameLayout)
                findViewById(R.id.material_style_ptr_frame);

        // mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
        //
        // @Override
        // public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        // // TODO Auto-generated method stub
        // new GetDataTask().execute();
        //
        // }
        // });
        // mListView.setOnLastItemVisibleListener(new
        // OnLastItemVisibleListener() {
        //
        // @Override
        // public void onLastItemVisible() {
        // // TODO Auto-generated method stub
        // Log.i(TAG, "LAST");
        // }
        // });
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
//				
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                new LoadMoreDateTask().execute();
            }

        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                AAONewsItem item = mItems.get(--position);
                Intent intent = new Intent(GlobalVariables.ACTION_AAONEWS_DETAIL);
                intent.putExtra(GlobalVariables.INTENT_EXTRA_AAONEWS_DETAIL, item.getNewsUrl());
                intent.putExtra(GlobalVariables.INTENT_EXTRA_AAONEWS_TITLE, item.getNewsTittle());
                startActivity(intent);

            }
        });

        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                if (firstVisibleItem == 0) {
                    mOntop = true;
                } else {
                    mOntop = false;
                }

            }
        });

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(30), 0,
                dp2px(4));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!mOnRefresh) {
                    new RefreshDataTask().execute();
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                mTvLastTime.setText(mTimeService
                        .getLastRefreshTime(CACHE_AAOTIME));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header) & mOntop;
            }
        });
    }

    private class RefreshDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = mNewsGetter.getNews();


            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mListView.onRefreshComplete();
            mPtrFrame.refreshComplete();
            mOnRefresh = false;
            // TODO Auto-generated method stub
            if (!result) {
                CommonUtils.showCustomToast(Toast.makeText(AAONewsActivity.this, getString(R.string.message_aao_list_toast), Toast.LENGTH_SHORT));
            } else {
                mAdapter.notifyDataSetChanged();
                mTvLastTime.setVisibility(View.INVISIBLE);
                mTimeService.setLastRefreshTime(CACHE_AAOTIME);

            }
        }

    }

    private class LoadMoreDateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            List<AAONewsItem> loadList = mNewsGetter.getNewsByPage(mCurrentPage++);
            mItems.addAll(loadList);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            mAdapter.notifyDataSetChanged();
            mListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}
