package com.emptypointer.hellocdut.activity;


import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.UserInfoAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.dao.UserDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserInfoCardActivity extends BaseActivity {
    public static final String ENTENT_EXTRA_USER_NAME = "user_name";
    public static final String ENTENT_EXTRA_OPENMODE = "open_mode";
    private ListView mListView;
    private UserInfoAdapter mAapter;
    private static final String CHACHE_PRE = "user_info_temp";
    private DataCacheDao mDao;
    private String mMessage = "";
    private TextView mTvName, mTvNickname;

    private PtrFrameLayout mPtrFrame;

    private UserDao mUserDao;

    private User mUser;
    private String mUsername;

    private List<String> mListKey;
    private List<String> mListValue;
    private boolean firstOpen;
    ImageView mImage;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        mDao = new DataCacheDao(this);
        mUserDao = new UserDao(this);
        mUsername = getIntent().getStringExtra(ENTENT_EXTRA_USER_NAME);
        firstOpen = getIntent().getBooleanExtra(ENTENT_EXTRA_OPENMODE, true);

        setContentView(R.layout.activity_info_card);
        mListView = (ListView) findViewById(R.id.listView_info);
        mTvName = (TextView) findViewById(R.id.textView_username);
        mTvNickname = (TextView) findViewById(R.id.textView_nick_name);
        mImage = (ImageView) findViewById(R.id.imageView_avatar);

        mListKey = new ArrayList<String>();
        mListValue = new ArrayList<String>();
        mAapter = new UserInfoAdapter(mListKey, mListValue, this);
        mListView.setAdapter(mAapter);
        Button button = (Button) findViewById(R.id.button_start_chat);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (firstOpen) {
                    // TODO Auto-generated method stub
                    startActivity(new Intent(UserInfoCardActivity.this,
                            ChatActivity.class).putExtra("userId", mUsername));
                }
                UserInfoCardActivity.this.finish();
            }
        });
        if (!firstOpen) {
            button.setText(getString(R.string.str_return));
        }
        mTvName.setText(getString(R.string.str_format_user_name, mUsername));
        DataCache cache = mDao.getCache(CHACHE_PRE + mUsername);

        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(20), 0, dp2px(10));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                // TODO Auto-generated method stub
                // mTvLastTime.setText(mTimeService
                // .getLastRefreshTime("11111"));
                // mTvLastTime.setVisibility(View.VISIBLE);
                return false;
            }
        });

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.ic_error_loaded)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        if (cache != null) {
            JSONObject jsonObject = JSONObject.parseObject(cache.getDate());
            paraseData(jsonObject.getJSONObject("user_info"));
            new QueryUserTask().execute();
        } else {
            mPtrFrame.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mPtrFrame.autoRefresh(true);
                    new QueryUserTask().execute();

                }
            }, 150);
        }

    }

    private class QueryUserTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // mDialog = ProgressDialog.show(getActivity(),
            // getString(R.string.str_str_loading_search),
            // getString(R.string.message_loading));
        }

        @Override
        protected Boolean doInBackground(Void... param) {

            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "queryUser"));
            params.add(new BasicNameValuePair("key_words", mUsername));
            params.add(new BasicNameValuePair("query_by", "1"));
            params.add(new BasicNameValuePair("query_type", "2"));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);

                JSONObject jsonObject = JSONObject.parseObject(str);
                boolean result = jsonObject.getBooleanValue("result");
                if (result) {
                    mDao.saveCache(CHACHE_PRE + mUsername, str);
                    paraseData(jsonObject.getJSONObject("user_info"));
                    return result;
                } else {
                    mMessage = jsonObject.getString("message");
                    return result;
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            // mDialog.dismiss();
            mPtrFrame.refreshComplete();
            if (result) {
                mAapter.notifyDataSetChanged();
            } else {
                CommonUtils
                        .showCustomToast(Toast.makeText(
                                UserInfoCardActivity.this, mMessage,
                                Toast.LENGTH_LONG));

                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(UserInfoCardActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().logout();
                    EPApplication.getInstance().clearAcitivy();
                    startActivity(intent);
                }
            }
        }
    }

    private void paraseData(JSONObject jsonObject) {

        // JSONObject object = JSONObject.parseObject(str);
        Map<String, String> map = JSON.parseObject(jsonObject.toString(),
                new TypeReference<LinkedHashMap<String, String>>() {
                });
        Set<Entry<String, String>> entrySet = map.entrySet();

        Set<String> set = map.keySet();
        for (String string : set) {
        }
        mUser = new User();
        mUser.setUsername(mUsername);

        // Set<Entry<String, Object>> entrySet = object.entrySet();
        Iterator<Entry<String, String>> iterator = entrySet.iterator();
        mListKey.clear();
        mListValue.clear();
        while (iterator.hasNext()) {
            Map.Entry<String, String> me = iterator.next();
            String key = me.getKey();
            final String value = (String) me.getValue();
            if (key.equals("user_avatar_url")) {
                if (value != null) {
                    mUser.setImageURL(value);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ImageLoader.getInstance().displayImage(value,
                                    mImage, options);

                        }
                    });

                }

            } else if (key.equals("user_nick_name")) {
                if (value != null) {
                    mUser.setNicKName(value);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mTvNickname.setText(value);

                        }
                    });

                }
            } else if (key.equals("user_motto")) {
                if (value != null) {
                    mUser.setMotto(value);
                }
            } else {

                mListKey.add(key);
                mListValue.add(value);
            }
        }

        mUserDao.saveContact(mUser);
        EPApplication.getInstance().refreshContact();
    }

}
