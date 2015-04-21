package com.emptypointer.hellocdut.fragment;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.activity.QueryCampusCardActivity;
import com.emptypointer.hellocdut.adapter.TradeRecordAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.domain.TradeItem;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CampusRecordFragment extends Fragment {

    public static final String CACHE_RECORD = "campus_record";
    private PtrFrameLayout mPtrFrame;
    private PullToRefreshListView mListView;
    private TradeRecordAdapter mAdapter;
    private List<TradeItem> mItems;

    private int mCurrentPage = 1;
    private int mEndPage = 1;
    private boolean mOntop;

    private DataCacheDao mDao;
    private TextView mTvLastTime;
    private EPTimeService mTimeService;
    private boolean mOnRefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_library_borrowed, null);
        mTimeService = new EPTimeService(getActivity());
        mTvLastTime = (TextView) view.findViewById(R.id.textView_last_updated);
        mPtrFrame = (PtrFrameLayout) view
                .findViewById(R.id.material_style_ptr_frame);
        mListView = (PullToRefreshListView) view
                .findViewById(R.id.PullToRefreshListView);

        mItems = new ArrayList<TradeItem>();

        mAdapter = new TradeRecordAdapter(mItems, getActivity());

        mListView.setAdapter(mAdapter);

        // mListView.setOnLastItemVisibleListener(new
        // OnLastItemVisibleListener() {
        //
        // @Override
        // public void onLastItemVisible() {
        // // TODO Auto-generated method stub
        // if (mCurrentPage < mEndPage) {
        // new QueryTask().execute(++mCurrentPage);
        // } else {
        // mListView.onRefreshComplete();
        //
        // }
        // }
        // });

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                if (mCurrentPage < mEndPage) {
                    new QueryTask().execute(mCurrentPage + 1);
                } else {
                    refreshView.post(new Runnable() {
                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    });
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                                    getString(R.string.str_no_more), Toast.LENGTH_SHORT)
                    );
                    return;
                }

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
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, ((QueryCampusCardActivity) getActivity()).dp2px(30), 0,
                ((QueryCampusCardActivity) getActivity()).dp2px(4));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!mOnRefresh) {
                    new QueryTask().execute(0);
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                mTvLastTime.setText(mTimeService
                        .getLastRefreshTime(CACHE_RECORD));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header) & mOntop;
            }
        });
        mDao = new DataCacheDao(getActivity());
        DataCache cache = mDao.getCache(CACHE_RECORD);
        if (cache != null) {
            jsonParse(JSONObject.parseObject(cache.getDate()), false);

            mAdapter.notifyDataSetChanged();

        }
        return view;
    }

    public void initData() {
        DataCache cache = mDao.getCache(CACHE_RECORD);
        if (cache == null) {

            mPtrFrame.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mPtrFrame.autoRefresh(true);
                    new QueryTask().execute(0);
                }
            }, 150);

        }
    }

    private class QueryTask extends AsyncTask<Integer, Void, Boolean> {
        private JSONObject mObject;
        private int impage;
        private String imMessage;

        @Override
        protected void onPreExecute() {
            mOnRefresh = true;
        }

        @Override
        protected Boolean doInBackground(Integer... params1) {
            impage = params1[0];
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("action", "queryConsumeInfo"));
            params.add(new BasicNameValuePair("start_time", "2014-09-01"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String endTime = format
                    .format(new Date(System.currentTimeMillis()));
            params.add(new BasicNameValuePair("end_time", endTime));

            if (impage != 0) {
                params.add(new BasicNameValuePair("jump_page", String
                        .valueOf(impage)));
            }
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_CAMPUS_CARD, params);

                mObject = JSONObject.parseObject(str);
                boolean result = mObject.getBooleanValue("result");
                if (result) {
                    if (impage == 0) {
                        mDao.saveCache(CACHE_RECORD, str);
                        mTimeService.setLastRefreshTime(CACHE_RECORD);
                    }
                } else {
                    imMessage = mObject.getString("message");
                }
                return result;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                imMessage = getString(R.string.message_weak_internet);
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            mTvLastTime.setVisibility(View.INVISIBLE);
            mListView.onRefreshComplete();
            mPtrFrame.refreshComplete();
            mOnRefresh = false;
            if (result) {
                mTimeService.setLastRefreshTime(CACHE_RECORD);
                if (impage == 0) {
                    jsonParse(mObject, false);

                } else {
                    jsonParse(mObject, true);
                }

            } else {
                if (imMessage.equals("time_out")) {
                    ((QueryCampusCardActivity) getActivity()).showCapcheDialog();
                } else {
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(), imMessage, Toast.LENGTH_LONG)
                    );
                    if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                        Intent intent = new Intent(getActivity(),
                                LoginActivity.class);
                        EPApplication.getInstance().clearAcitivy();

                        EPApplication.getInstance().logout();
                        startActivity(intent);
                    }
                }

            }
        }

    }

    private void jsonParse(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mItems.clear();
        }

        mCurrentPage = obj.getIntValue("current_page");
        int endPage = obj.getIntValue("total_page");
        mEndPage = endPage;
        JSONArray array = obj.getJSONArray("consume_info");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("item");
                String time = new StringBuilder(object.getString("date"))
                        .append(" ").append(object.getString("time"))
                        .toString();
                String amount = object.getString("money");
                String balance = object.getString("balance");
                String operator = object.getString("operator");
                String location = object.getString("workstation");
                mItems.add(new TradeItem(time, amount, balance, operator,
                        location, name));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
