package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.fragment.CampusOverviewFragment;
import com.emptypointer.hellocdut.fragment.CampusQueryFragment;
import com.emptypointer.hellocdut.fragment.CampusRecordFragment;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

public class QueryCampusCardActivity extends BaseActivity implements OnClickListener {
    public static final String CACHE_CAMPUS_INFO = "cache_campus_info";

    public static final String ACTIVITY_RESULT_KEY = "result";
    public static final int ACTIVITY_REQUEST_CODE = 2 << 3;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private TextView mTvInfo, mTvOnBorrow, mTvBorrowed;
    private ImageView mIndicator;
    private int mTabLineWith;
    private int mCurrentPageIndex;
    public boolean isOnLoadCaptcha = false;
    private Button mBtnRefresh, mBtnCommit;
    private RelativeLayout mLayout;
    private DataCacheDao mDao;
    private EPTimeService mService;

    private EditText mEtCaptcha;

    private ImageView mImgeView;
    private CampusOverviewFragment mCampusOverview;
    private CampusRecordFragment mCampusRecord;
    private static String SHARED_PREFERENCE_KEY;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (!data.getExtras().getBoolean(ACTIVITY_RESULT_KEY)) {
                showCapcheDialog();
            }
        }

    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_campus_card);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mBtnRefresh = (Button) findViewById(R.id.button_refresh);
        mImgeView = (ImageView) findViewById(R.id.imageView_captcha);
        mEtCaptcha = (EditText) findViewById(R.id.editText_captcha);
        mBtnCommit = (Button) findViewById(R.id.button_commit);

        mLayout = (RelativeLayout) findViewById(R.id.layout_captcha);
        mBtnCommit.setOnClickListener(this);
        mBtnRefresh.setOnClickListener(this);

        mFragments = new ArrayList<Fragment>();

        mCampusOverview = new CampusOverviewFragment();
        mFragments.add(mCampusOverview);
        mCampusRecord = new CampusRecordFragment();
        mFragments.add(mCampusRecord);
        mFragments.add(new CampusQueryFragment());
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

        mTvInfo.setText(R.string.str_consumption_statistics);
        mTvOnBorrow.setText(R.string.str_consumption_record);
        mTvBorrowed.setText(R.string.str_consumption_query);

        mTvInfo.setOnClickListener(this);
        mTvOnBorrow.setOnClickListener(this);
        mTvBorrowed.setOnClickListener(this);

        mTabLineWith = getScreenWith() / 3;
        LayoutParams params = mIndicator.getLayoutParams();
        params.width = mTabLineWith;
        mIndicator.setLayoutParams(params);
        mViewPager.setOffscreenPageLimit(5);

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
        mDao = new DataCacheDao(this);
        mService = new EPTimeService(this);
        SharedPreferences preferences = getSharedPreferences(
                GlobalVariables.SHARED_PERFERENCR_INIT, MODE_PRIVATE);
        SHARED_PREFERENCE_KEY = "need_chaptcha";
        if (preferences.getBoolean(SHARED_PREFERENCE_KEY, true)) {
            showCapcheDialog();
        }
        mEtCaptcha.clearFocus();
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
            case R.id.button_refresh:
                new GetChaptchaTask().execute();
                break;
            case R.id.button_commit:
                String strEtText = mEtCaptcha.getText().toString();
                if (StringChecker.isChaptcha(strEtText)) {
                    new CommitTask().execute(strEtText);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEtCaptcha.getWindowToken(), 0);
                    mEtCaptcha.clearFocus();
                } else {

                    CommonUtils.showCustomToast(Toast.makeText(QueryCampusCardActivity.this,
                            getString(R.string.message_wrong_format_captcha),
                            Toast.LENGTH_LONG));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示验证码窗口
     */
    public void showCapcheDialog() {
        mEtCaptcha.requestFocus();
        SharedPreferences preferences = getSharedPreferences(
                GlobalVariables.SHARED_PERFERENCR_INIT, MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(SHARED_PREFERENCE_KEY, true);
        editor.commit();

        new GetChaptchaTask().execute();
        mLayout.setVisibility(View.VISIBLE);

    }

    private class CommitTask extends AsyncTask<String, Void, Boolean> {
        private String imMessage = "";
        private ProgressDialog imDialog;
        private JSONObject imObject;

        @Override
        protected void onPreExecute() {
            imDialog = new ProgressDialog(QueryCampusCardActivity.this);
            imDialog.setCanceledOnTouchOutside(false);

            imDialog.setMessage(getString(R.string.str_on_captcha_commit));
            imDialog.show();
            mBtnRefresh.setClickable(false);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            imDialog.dismiss();
            // TODO Auto-generated method stub
            if (result) {
                mEtCaptcha.clearFocus();
                mLayout.setVisibility(View.GONE);
                mCampusOverview.setDataOnUI(imObject);
                mCampusOverview.inintData();
                mCampusRecord.initData();
                SharedPreferences preferences = getSharedPreferences(
                        GlobalVariables.SHARED_PERFERENCR_INIT, MODE_PRIVATE);
                Editor editor = preferences.edit();
                editor.putBoolean(SHARED_PREFERENCE_KEY, false);
                editor.commit();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(getBaseContext(), imMessage, Toast.LENGTH_LONG));
                if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(QueryCampusCardActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();

                    EPApplication.getInstance().logout();
                    startActivity(intent);
                    return;
                }
                new GetChaptchaTask().execute();

            }
        }

        @Override
        protected Boolean doInBackground(String... param) {
            isOnLoadCaptcha = true;
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "campusUserLogin"));
            params.add(new BasicNameValuePair("captcha", param[0]));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_CAMPUS_CARD, params);
                JSONObject object = JSONObject.parseObject(str);
                boolean result = object.getBooleanValue("result");
                if (!result) {
                    imMessage = object.getString("message");
                } else {
                    imObject = object;
                    mDao.saveCache(CACHE_CAMPUS_INFO, str);
                    mService.setLastRefreshTime(CACHE_CAMPUS_INFO);
                }
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                imMessage = getString(R.string.message_weak_internet);
                e.printStackTrace();
                return false;
            }
        }

    }

    private class GetChaptchaTask extends AsyncTask<Void, Void, Boolean> {
        private String im;
        private String imMessage;

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {
                Bitmap bitmap = CommonUtils.decodeBase64(im);
                mImgeView.setImageBitmap(bitmap);
                isOnLoadCaptcha = false;
                mBtnRefresh.setClickable(true);

            } else {
                CommonUtils.showCustomToast(Toast.makeText(QueryCampusCardActivity.this, imMessage,
                        Toast.LENGTH_LONG));
                if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(QueryCampusCardActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();
                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            isOnLoadCaptcha = true;
            mBtnRefresh.setClickable(false);
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "getAuthCode"));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_CAMPUS_CARD, params);
                JSONObject object = JSONObject.parseObject(str);
                boolean result = object.getBooleanValue("result");
                if (result) {
                    im = object.getString("captcha");
                } else {
                    imMessage = object.getString("message");
                }
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                imMessage = getString(R.string.message_weak_internet);
                e.printStackTrace();
                return false;
            }
        }

    }
}
