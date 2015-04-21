package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.TradeQueryAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.TradeItem;
import com.emptypointer.hellocdut.fragment.CampusQueryFragment;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CampusQueryResultActivity extends BaseActivity {

    public static final String CACHE_RECORD = "campus_record";
    private PtrFrameLayout mPtrFrame;
    private PullToRefreshListView mListView;
    private TradeQueryAdapter mAdapter;
    private List<TradeItem> mItems;

    private int mCurrentPage = 1;
    private int mEndPage = 1;

    private TextView mTvLastTime;
    private boolean mOnRefresh = false;
    private int mCurrentType = -1;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        String intentDate = getIntent().getStringExtra(
                CampusQueryFragment.INTENT_ACTION);
        for (int i = 0; i < CampusQueryFragment.INTENT_TYPE_QUERYDEPOSITINFO.length; i++) {
            if (intentDate
                    .equals(CampusQueryFragment.INTENT_TYPE_QUERYDEPOSITINFO[i])) {
                mCurrentType = i;
                String[] labels = getResources().getStringArray(
                        R.array.trade_query_type);
                getActionBar().setTitle(labels[i]);
                break;
            }
        }

        setContentView(R.layout.fragment_library_borrowed);
        mTvLastTime = (TextView) findViewById(R.id.textView_last_updated);
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);
        mListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
        if (mCurrentType == 3) {
            mListView.setPullToRefreshEnabled(false);
            mListView.setClickable(false);

        }
        mItems = new ArrayList<TradeItem>();

        mAdapter = new TradeQueryAdapter(mItems, this, mCurrentType);

        mListView.setAdapter(mAdapter);

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
                    CommonUtils.showCustomToast(Toast.makeText(CampusQueryResultActivity.this,
                            getString(R.string.str_no_more), Toast.LENGTH_SHORT));
                    return;
                }

            }

        });

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(10), 0, dp2px(4));
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

                return false;
            }
        });

        mPtrFrame.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPtrFrame.autoRefresh(true);
                new QueryTask().execute(0);
            }
        }, 150);

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
            String strAction = getIntent().getStringExtra(
                    CampusQueryFragment.INTENT_ACTION);
            params.add(new BasicNameValuePair("action", strAction));
            params.add(new BasicNameValuePair("start_time", getIntent()
                    .getStringExtra(CampusQueryFragment.INTENT_START_DATE)));
            params.add(new BasicNameValuePair("end_time", getIntent()
                    .getStringExtra(CampusQueryFragment.INTENT_END_DATE)));

            if (impage != 0) {
                params.add(new BasicNameValuePair("jump_page", String
                        .valueOf(impage)));
            }
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_CAMPUS_CARD, params);

                mObject = JSONObject.parseObject(str);
                boolean result = mObject.getBooleanValue("result");
                if (!result) {
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
                switch (mCurrentType) {
                    case 0:
                        if (impage == 0) {

                            jsonParseDeposit(mObject, false);

                        } else {
                            jsonParseDeposit(mObject, true);
                        }

                        break;
                    case 1:
                        if (impage == 0) {

                            jsonParseBank(mObject, false);

                        } else {
                            jsonParseBank(mObject, true);
                        }

                        break;
                    case 2:
                        if (impage == 0) {

                            jsonParseConSumen(mObject, false);

                        } else {
                            jsonParseConSumen(mObject, true);
                        }

                        break;
                    case 3:
                        jsonParseTotal(mObject);
                        break;

                    default:
                        break;
                }

            } else {
                if (imMessage.equals("time_out")) {
                    Intent intent = new Intent();
                    intent.putExtra(QueryCampusCardActivity.ACTIVITY_RESULT_KEY,
                            false);
                    // 设置返回数据
                    CampusQueryResultActivity.this.setResult(RESULT_OK, intent);
                    // 关闭Activity
                    CampusQueryResultActivity.this.finish();

                } else {
                    CommonUtils.showCustomToast(Toast.makeText(getApplicationContext(), imMessage,
                            Toast.LENGTH_LONG));
                }
                CampusQueryResultActivity.this.finish();
            }
        }

    }

    /**
     * 交易记录查询Json解析
     *
     * @param obj
     * @param appendable
     */
    private void jsonParseConSumen(JSONObject obj, boolean appendable) {
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

    /**
     * 存款记录查询Json解析
     *
     * @param obj
     * @param appendable
     */
    private void jsonParseDeposit(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mItems.clear();
        }

        mCurrentPage = obj.getIntValue("current_page");
        int endPage = obj.getIntValue("total_page");
        mEndPage = endPage;
        JSONArray array = obj.getJSONArray("deposit_info");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("terminal");
                String time = new StringBuilder(object.getString("date"))
                        .append(" ").append(object.getString("time"))
                        .toString();
                String amount = object.getString("money");
                String balance = object.getString("balance");
                String operator = object.getString("terminal_name");
                String location = object.getString("workstation");
                mItems.add(new TradeItem(time, amount, balance, operator,
                        location, name));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 交易记录查询Json解析
     *
     * @param obj
     * @param appendable
     */
    private void jsonParseBank(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mItems.clear();
        }

        mCurrentPage = obj.getIntValue("current_page");
        int endPage = obj.getIntValue("total_page");
        mEndPage = endPage;

        JSONArray array = obj.getJSONArray("bank_info");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = "圈存记录";
                String time = new StringBuilder(object.getString("date"))
                        .append(" ").append(object.getString("time"))
                        .toString();
                String amount = object.getString("balance");
                String tpye = object.getString("terminal");
                String result = object.getString("description");
                mItems.add(new TradeItem(time, result, amount, tpye, name));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 解析交易汇总json
     *
     * @param obj
     */
    private void jsonParseTotal(JSONObject obj) {
        mItems.clear();
        mItems.add(new TradeItem(null, null, null, null, obj.toString()));
        mAdapter.notifyDataSetChanged();
    }
}
