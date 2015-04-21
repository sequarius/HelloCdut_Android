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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LibraryActivity;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class LibraryInfoFragment extends Fragment {

    private boolean mOnRefresh = false;
    private static final String CACHE_READER_INFO = "reader_info";
    private TextView mTvName, mTvReaderID, mTvDegree, mTvInsitute, mTvFine,
            mTvReparation, mTvBookCount;
    private DataCacheDao mDao;

    private PtrFrameLayout mPtrFrame;

    private TextView mTvLastTime;
    private EPTimeService mTimeService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_library_info, null);
        mDao = new DataCacheDao(getActivity());
        mTvName = (TextView) view.findViewById(R.id.textView_name);
        mTvReaderID = (TextView) view.findViewById(R.id.textView_id);
        mTvDegree = (TextView) view.findViewById(R.id.textView_degree);
        mTvInsitute = (TextView) view.findViewById(R.id.textView_insitute);
        mTvFine = (TextView) view.findViewById(R.id.textView_fine);
        mTvReparation = (TextView) view.findViewById(R.id.textView_reparation);
        mTvBookCount = (TextView) view.findViewById(R.id.textView_borrow_count);

        mTimeService = new EPTimeService(getActivity());
        mTvLastTime = (TextView) view.findViewById(R.id.textView_last_updated);
        mPtrFrame = (PtrFrameLayout) view
                .findViewById(R.id.material_style_ptr_frame);

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
                    new QueryTask().execute();
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {

                mTvLastTime.setText(mTimeService
                        .getLastRefreshTime(CACHE_READER_INFO));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header);
            }
        });

        DataCache cache = mDao.getCache(CACHE_READER_INFO);

        if (cache != null) {
            JSONObject obj = JSONObject.parseObject(cache.getDate());
            jsonParse(obj.getJSONObject("reader"));
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

        return view;
    }

    private class QueryTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject mObject;
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
            params.add(new BasicNameValuePair("action", "queryLibInfo"));
            params.add(new BasicNameValuePair("flag", "1"));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERYSYSTEM, params);

                mObject = JSONObject.parseObject(str);
                boolean result = mObject.getBooleanValue("result");
                if (result) {
                    mDao.saveCache(CACHE_READER_INFO, str);
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
            mPtrFrame.refreshComplete();
            mOnRefresh = false;
            if (result) {
                mTimeService.setLastRefreshTime(CACHE_READER_INFO);
                JSONObject jsonReader = mObject.getJSONObject("reader");

                jsonParse(jsonReader);
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

    private void jsonParse(JSONObject jsonReader) {
        mTvName.setText(jsonReader.getString("readerName"));
        mTvReaderID.setText(jsonReader.getString("readerNum"));
        mTvDegree.setText(jsonReader.getString("readerDegree"));
        mTvInsitute.setText(jsonReader.getString("readerUnit"));
        mTvFine.setText(jsonReader.getString("readerFine"));
        mTvReparation.setText(jsonReader.getString("readerReparation"));
        mTvBookCount.setText(jsonReader.getString("readerBookCount"));
    }

}
