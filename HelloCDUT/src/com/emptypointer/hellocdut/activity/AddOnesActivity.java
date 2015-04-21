package com.emptypointer.hellocdut.activity;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.AddonesAdapter;
import com.emptypointer.hellocdut.adapter.GradeListAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.Addone;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.domain.GradeInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class AddOnesActivity extends BaseActivity {
    private static final String CACHE_ADDONES = "addones";

    public static final String TAG = "AddOnesActivity";

    private ListView mListView;
    private AddonesAdapter mAdapter;

    private boolean mOnRefresh = false;

    private EPTimeService mTimeService;

    private DataCacheDao mDao;

    private List<Addone> mAddOnes;

    private PtrFrameLayout mPtrFrame;

    private TextView mTvLastTime;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mAdapter = new AddonesAdapter(mAddOnes, this);
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_query_grade);
        mTimeService = new EPTimeService(this);
        mListView = (ListView) findViewById(R.id.ListView_grade);
        mTvLastTime = (TextView) findViewById(R.id.textView_last_updated);
        mAddOnes = new ArrayList<Addone>();

        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(30), 0, dp2px(10));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!mOnRefresh) {
                    new QueryTask().execute();
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                // TODO Auto-generated method stub
                mTvLastTime.setText(mTimeService
                        .getLastRefreshTime(CACHE_ADDONES));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header);
            }
        });

        mAdapter = new AddonesAdapter(mAddOnes, this);
        mListView.setAdapter(mAdapter);
        mDao = new DataCacheDao(this);
        DataCache cache = mDao.getCache(CACHE_ADDONES);
        if (cache != null) {

            jsonParse(JSONObject.parseObject(cache.getDate()));

            mAdapter.notifyDataSetChanged();

        } else {
            mPtrFrame.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mPtrFrame.autoRefresh(true);
                    new QueryTask().execute();
                }
            }, 150);
        }

        mTvLastTime.setText(mTimeService.getLastRefreshTime(CACHE_ADDONES));
        mTvLastTime.setVisibility(View.VISIBLE);


    }

    private class QueryTask extends AsyncTask<Void, Void, Boolean> {
        private String imMessage;

        @Override
        protected void onPreExecute() {

            mOnRefresh = true;

        }

        @Override
        protected Boolean doInBackground(Void... params1) {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("action", "queryGrade"));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_ADDONES, params);
                JSONObject obj = JSONObject.parseObject(str);
                boolean result = obj.getBooleanValue("result");
                if (result) {
                    mDao.saveCache(CACHE_ADDONES, str);
                    jsonParse(obj);
                } else {
                    imMessage = obj.getString("message");
                }
                return result;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                imMessage = getString(R.string.message_weak_internet);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            mOnRefresh = false;
            mTvLastTime.setVisibility(View.INVISIBLE);
            mPtrFrame.refreshComplete();

            if (result) {
                // TODO Auto-generated method stub
                mTimeService.setLastRefreshTime(CACHE_ADDONES);
                mAdapter.notifyDataSetChanged();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(
                        AddOnesActivity.this, imMessage, Toast.LENGTH_LONG));
                if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(AddOnesActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();

                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }
        }

    }

    private void jsonParse(JSONObject obj) {
        JSONArray array = obj.getJSONArray("addon_list");
        mAddOnes.clear();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String packageName = object.getString("package");
                String action = object.getString("action");
                String author = object.getString("author");
                String updateTime = object.getString("update_time");
                String version = object.getString("version");
                String dowloadCount = object.getString("download_count");
                String icUrl = object.getString("ic_url");
                String addoneUrl = object.getString("addon_url");
                String introduction = object.getString("introduction");
                mAddOnes.add(new Addone(name, packageName, action, author,
                        updateTime, version, dowloadCount, icUrl, addoneUrl,
                        introduction));
            }
        }
    }

}
