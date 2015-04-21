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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.activity.QueryCampusCardActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class CampusOverviewFragment extends Fragment {
    private TextView mTvUserName, mTvID, mTvStatus, mTvGender, mTvMainBalance,
            mTvSubSideyBalance;
    private DataCacheDao mDao;

    private static final String CACHE_CAMPUS_OVERVIEW = "cmapus_over_view_cusumuse";

    private TextView mTvLastTime;
    private EPTimeService mTimeService;
    private PtrFrameLayout mPtrFrame;
    private boolean mOnRefresh = false;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_campus_overview, null);
        mTvUserName = (TextView) mRootView.findViewById(R.id.textView_name);
        mTvID = (TextView) mRootView.findViewById(R.id.textView_id);
        mTvStatus = (TextView) mRootView.findViewById(R.id.textView_status);
        mTvMainBalance = (TextView) mRootView
                .findViewById(R.id.textView_balance);
        mTvSubSideyBalance = (TextView) mRootView
                .findViewById(R.id.textView_subsidy_balance);
        mTvGender = (TextView) mRootView.findViewById(R.id.textView_gender);

        mTimeService = new EPTimeService(getActivity());
        mTvLastTime = (TextView) mRootView
                .findViewById(R.id.textView_last_updated);
        mPtrFrame = (PtrFrameLayout) mRootView
                .findViewById(R.id.material_style_ptr_frame);

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
                    new QueryTask().execute();
                } else {
                    mPtrFrame.refreshComplete();
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                mTvLastTime.setText(mTimeService
                        .getLastRefreshTime(CACHE_CAMPUS_OVERVIEW));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header);
            }
        });
        mDao = new DataCacheDao(getActivity());
        DataCache cache = mDao.getCache(QueryCampusCardActivity.CACHE_CAMPUS_INFO);
        if (cache != null) {
            setDataOnUI(JSONObject.parseObject(cache.getDate()));
        }
        DataCache cacheCusume = mDao.getCache(CACHE_CAMPUS_OVERVIEW);
        if (cacheCusume != null) {
            JsonParse(JSONObject.parseObject(cacheCusume.getDate()));
        }

        return mRootView;

    }

    private class QueryTask extends AsyncTask<Integer, Void, Boolean> {
        private JSONObject imObject;
        private String imMessage;

        @Override
        protected void onPreExecute() {
            mOnRefresh = true;
        }

        @Override
        protected Boolean doInBackground(Integer... params1) {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("action", "queryCustStateInfo"));
            params.add(new BasicNameValuePair("start_time", "2014-09-01"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String endTime = format
                    .format(new Date(System.currentTimeMillis()));
            params.add(new BasicNameValuePair("end_time", endTime));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_CAMPUS_CARD, params);

                imObject = JSONObject.parseObject(str);
                boolean result = imObject.getBooleanValue("result");
                if (result) {

                    mDao.saveCache(CACHE_CAMPUS_OVERVIEW, str);
                    mTimeService.setLastRefreshTime(CACHE_CAMPUS_OVERVIEW);

                } else {
                    imMessage = imObject.getString("message");

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
            mPtrFrame.refreshComplete();
            if (result) {
                mOnRefresh = false;
                mTimeService.setLastRefreshTime(CACHE_CAMPUS_OVERVIEW);
                JsonParse(imObject);

            } else {
                if (imMessage.equals("time_out")) {
                    ((QueryCampusCardActivity) getActivity()).showCapcheDialog();
                } else {
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(), imMessage, Toast.LENGTH_LONG));
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

    public void inintData() {
        DataCache cacheCusume = mDao.getCache(CACHE_CAMPUS_OVERVIEW);
        if (cacheCusume == null) {
            mPtrFrame.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mPtrFrame.autoRefresh(true);
                    new QueryTask().execute();
                }
            }, 150);

        }

    }

    public void setDataOnUI(JSONObject object) {
        String id = object.getString("user_stu_num");
        String userName = object.getString("user_real_name");
        String status = object.getString("user_card_status");
        String balance = object.getString("user_card_balance");
        String subsidyBanlance = object.getString("user_subsidy_balance");
        String gender = object.getString("user_gender");
        mTvUserName.setText(userName);
        mTvID.setText(id);
        mTvStatus.setText(status);
        mTvMainBalance.setText(balance);
        mTvSubSideyBalance.setText(subsidyBanlance);
        mTvGender.setText(gender);
    }

    private void JsonParse(JSONObject obj) {
        String meal = obj.getString("wallet_deals_amount");
        String shower = obj.getString("shower_amount");
        String shopping = obj.getString("shopping_amount");
        String bus = obj.getString("bus_amount");
        double total = Double.valueOf(meal.replaceAll(",", ""))
                + Double.valueOf(shower.replaceAll(",", ""))
                + Double.valueOf(shopping.replaceAll(",", ""))
                + Double.valueOf(bus.replaceAll(",", ""));
        ((TextView) mRootView.findViewById(R.id.textView_meal)).setText(meal);
        ((TextView) mRootView.findViewById(R.id.textView_shower))
                .setText(shower);
        ((TextView) mRootView.findViewById(R.id.textView_shopping))
                .setText(shopping);
        ((TextView) mRootView.findViewById(R.id.textView_bus)).setText(bus);
        ((TextView) mRootView.findViewById(R.id.textView_total)).setText(String
                .format("%.2f", total));
    }

}
