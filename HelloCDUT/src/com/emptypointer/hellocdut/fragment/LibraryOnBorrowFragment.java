package com.emptypointer.hellocdut.fragment;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
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
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LibraryActivity;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.adapter.LibBookOnBorrowAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.domain.LibBooksItem;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LibraryOnBorrowFragment extends Fragment {
    public static final String CACHE_ON_BORROW = "book_on_borrow";
    public PtrFrameLayout mPtrFrame;
    private PullToRefreshListView mListView;
    private LibBookOnBorrowAdapter mAdapter;
    private List<LibBooksItem> mItems;

    private int mCurrentPage = 1;
    // private int mEndPage = 1;
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

        mItems = new ArrayList<LibBooksItem>();

        mAdapter = new LibBookOnBorrowAdapter(mItems, getActivity(), this);

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

        mListView.setMode(Mode.DISABLED);

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
        header.setPadding(0, ((LibraryActivity) getActivity()).dp2px(30), 0,
                ((LibraryActivity) getActivity()).dp2px(4));
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
                        .getLastRefreshTime(CACHE_ON_BORROW));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header) & mOntop;
            }
        });
        mDao = new DataCacheDao(getActivity());
        DataCache cache = mDao.getCache(CACHE_ON_BORROW);
        if (cache != null) {
            JSONObject obj = JSONObject.parseObject(cache.getDate());
            jsonParse(obj, false);

            mAdapter.notifyDataSetChanged();

        } else {
            mPtrFrame.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mPtrFrame.autoRefresh(true);
                    new QueryTask().execute(0);
                }
            }, 150);
        }
        return view;
    }

    public void LoadDate() {
        new QueryTask().execute(0);
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
            params.add(new BasicNameValuePair("action", "queryLibInfo"));
            params.add(new BasicNameValuePair("flag", "2"));
            if (impage != 0) {
                params.add(new BasicNameValuePair("jump_page", String
                        .valueOf(impage)));
            }
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERYSYSTEM, params);

                mObject = JSONObject.parseObject(str);
                boolean result = mObject.getBooleanValue("result");
                if (result) {
                    if (impage == 0) {
                        mDao.saveCache(CACHE_ON_BORROW, str);
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
                mTimeService.setLastRefreshTime(CACHE_ON_BORROW);

                if (impage == 0) {
                    jsonParse(mObject, false);

                } else {
                    jsonParse(mObject, true);
                }

            } else {
                CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                        imMessage, Toast.LENGTH_LONG));
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

    private void jsonParse(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mItems.clear();
        }
        // mCurrentPage = obj.getIntValue("current_page");
        // mEndPage = obj.getIntValue("total_page");
        JSONArray array = obj.getJSONArray("borrowed_book");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String title = object.getString("title");
                String id = object.getString("barCode");
                String returnTime = object.getString("returnTime");
                String borrowTime = object.getString("borrowTime");
                String renewTime = object.getString("renewTime");
                String indexID = object.getString("indexCode");
                String location = object.getString("bookLocation");
                String renewURL = object.getString("renewHref");

                LibBooksItem item = new LibBooksItem(title, id, borrowTime,
                        returnTime, location, indexID, renewURL, renewTime);
                mItems.add(item);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
