package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.GradeListAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.domain.GradeInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.GradeComparator;

public class QueryGradeActivity extends BaseActivity {
    private static final String CACHE_GRADE = "grade";

    private ListView mListView;
    private GradeListAdapter mAdapter;

    private boolean mOnRefresh = false;

    private EPTimeService mTimeService;

    private DataCacheDao mDao;

    private List<GradeInfo> mGrades;

    private PtrFrameLayout mPtrFrame;

    private TextView mTvLastTime;

    private int mSortMode;

    private double mCgpa = 0X0f;

    private double mGpa = 0X0f;

    private static final String SHARED_PREFERENCE_SORT_MODE = "grade_sort_mode";
    private static final String SHARED_PREFERENCE_SORT_DES = "grade_sort_des";
    private static final int SORT_MODE_BY_TIME = 1;
    private static final int SORT_MODE_BY_GRADE = 2;
    private static final int SORT_MODE_BY_CREDITS = 3;

    private static final int SORT_MODE_BY_NORLMORL = 4;

    public static final String TAG = "QueryGradeActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.query_grade, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_grade_statistics:
                showGradeStatistics();
                break;
            case R.id.action_sort_by:
                createSortGradeDialog();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createSortGradeDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.str_sort_type);
        View view = View.inflate(this, R.layout.dialog_sort_grade, null);
        builder.setView(view);
        final RadioGroup group = (RadioGroup) view
                .findViewById(R.id.radioGroup_sort_type);
        switch (getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING,
                MODE_PRIVATE).getInt(SHARED_PREFERENCE_SORT_MODE,
                SORT_MODE_BY_TIME)) {
            case SORT_MODE_BY_TIME:
                group.check(R.id.radio_time);
                break;
            case SORT_MODE_BY_GRADE:
                group.check(R.id.radio_grade);
                break;
            case SORT_MODE_BY_CREDITS:
                group.check(R.id.radio_credits);
                break;
            case SORT_MODE_BY_NORLMORL:
                group.check(R.id.radio_normol);
                break;

            default:
                break;
        }
        final CheckBox checkBox = (CheckBox) view
                .findViewById(R.id.checkBox_sort_des);
        boolean sortDes = getSharedPreferences(
                GlobalVariables.SHARED_PERFERENCR_SETTING, MODE_PRIVATE)
                .getBoolean(SHARED_PREFERENCE_SORT_DES, true);
        checkBox.setChecked(sortDes);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.radio_normol) {
                    checkBox.setVisibility(View.GONE);
                } else {
                    checkBox.setVisibility(View.VISIBLE);
                }
            }
        });

        builder.setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING,
                        MODE_PRIVATE)
                        .edit()
                        .putBoolean(SHARED_PREFERENCE_SORT_DES,
                                checkBox.isChecked()).commit();
                switch (group.getCheckedRadioButtonId()) {

                    case R.id.radio_time:
                        getSharedPreferences(
                                GlobalVariables.SHARED_PERFERENCR_SETTING,
                                MODE_PRIVATE)
                                .edit()
                                .putInt(SHARED_PREFERENCE_SORT_MODE,
                                        SORT_MODE_BY_TIME).commit();
                        setGradeSortMode(SORT_MODE_BY_TIME);
                        break;
                    case R.id.radio_grade:
                        getSharedPreferences(
                                GlobalVariables.SHARED_PERFERENCR_SETTING,
                                MODE_PRIVATE)
                                .edit()
                                .putInt(SHARED_PREFERENCE_SORT_MODE,
                                        SORT_MODE_BY_GRADE).commit();
                        setGradeSortMode(SORT_MODE_BY_GRADE);
                        break;
                    case R.id.radio_credits:
                        getSharedPreferences(
                                GlobalVariables.SHARED_PERFERENCR_SETTING,
                                MODE_PRIVATE)
                                .edit()
                                .putInt(SHARED_PREFERENCE_SORT_MODE,
                                        SORT_MODE_BY_CREDITS).commit();
                        setGradeSortMode(SORT_MODE_BY_CREDITS);
                        break;
                    case R.id.radio_normol:
                        getSharedPreferences(
                                GlobalVariables.SHARED_PERFERENCR_SETTING,
                                MODE_PRIVATE)
                                .edit()
                                .putInt(SHARED_PREFERENCE_SORT_MODE,
                                        SORT_MODE_BY_NORLMORL).commit();
                        DataCache cache = mDao.getCache(CACHE_GRADE);
                        if (cache != null) {

                            jsonParse(JSONObject.parseObject(cache.getDate()));

                        }
                        break;

                    default:
                        break;
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        CommonUtils.dialogTitleLineColor(builder.show());
    }

    /**
     * 設置排序模式
     *
     * @param mode
     */
    private void setGradeSortMode(int mode) {
        switch (mode) {
            case SORT_MODE_BY_TIME:
                Collections.sort(mGrades,
                        new GradeComparator().new ComparateByTime());

                break;
            case SORT_MODE_BY_GRADE:

                Collections.sort(mGrades,
                        new GradeComparator().new ComparateByGrade());
                break;
            case SORT_MODE_BY_CREDITS:
                Collections.sort(mGrades,
                        new GradeComparator().new ComparateByCredit());
                break;
            case SORT_MODE_BY_NORLMORL:

                break;

            default:
                break;
        }
        if (getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING,
                MODE_PRIVATE).getBoolean(SHARED_PREFERENCE_SORT_DES, true)
                && (getSharedPreferences(
                GlobalVariables.SHARED_PERFERENCR_SETTING, MODE_PRIVATE)
                .getInt(SHARED_PREFERENCE_SORT_MODE, 1) != SORT_MODE_BY_NORLMORL)) {
            Collections.reverse(mGrades);
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_query_grade);
        mSortMode = getSharedPreferences(
                GlobalVariables.SHARED_PERFERENCR_SETTING, MODE_PRIVATE)
                .getInt(SHARED_PREFERENCE_SORT_MODE, 1);
        mTimeService = new EPTimeService(this);
        mListView = (ListView) findViewById(R.id.ListView_grade);
        mTvLastTime = (TextView) findViewById(R.id.textView_last_updated);
        mGrades = new ArrayList<GradeInfo>();

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
                        .getLastRefreshTime(CACHE_GRADE));
                mTvLastTime.setVisibility(View.VISIBLE);
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        content, header);
            }
        });

        mAdapter = new GradeListAdapter(mGrades, this);
        mListView.setAdapter(mAdapter);
        mDao = new DataCacheDao(this);
        DataCache cache = mDao.getCache(CACHE_GRADE);
        if (cache != null) {

            jsonParse(JSONObject.parseObject(cache.getDate()));

        }

        mTvLastTime.setText(mTimeService.getLastRefreshTime(CACHE_GRADE));
        mTvLastTime.setVisibility(View.VISIBLE);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                mPtrFrame.autoRefresh(true);
                new QueryTask().execute();
            }
        }, 150);

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
                        GlobalVariables.SERVICE_HOST_QUERYSYSTEM, params);
                Log.i(TAG, str);
                JSONObject obj = JSONObject.parseObject(str);
                boolean result = obj.getBooleanValue("result");
                if (result) {
                    mDao.saveCache(CACHE_GRADE, str);
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
                mTimeService.setLastRefreshTime(CACHE_GRADE);
                mAdapter.notifyDataSetChanged();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(
                        QueryGradeActivity.this, imMessage, Toast.LENGTH_LONG));
                if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(QueryGradeActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();

                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }
        }

    }

    private void showGradeStatistics() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.str_grade_stastics);
        builder.setMessage(getString(R.string.str_grade_stastics_message, mCgpa, mGpa));
        builder.setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        CommonUtils.dialogTitleLineColor(builder.show());
    }

    private void jsonParse(JSONObject obj) {
        JSONArray array = obj.getJSONArray("subject");
        mGrades.clear();
        mCgpa = obj.getDoubleValue("cgpa");
        mGpa = obj.getDoubleValue("gpa");
        Log.i(TAG, "cgpa===" + mCgpa + ";gpa" + mGpa);
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String semester = object.getString("semester");
                String courseID = object.getString("course_code");
                String courseName = object.getString("course_name");
                String teacherName = object.getString("teacher");
                double credits = object.getDoubleValue("credits");
                String score = object.getString("score");
                String updateTime = object.getString("storage_time");
                GradeInfo grade = new GradeInfo(courseName, score, updateTime,
                        semester, courseID, credits, teacherName);
                mGrades.add(grade);
            }
        }
        setGradeSortMode(getSharedPreferences(
                GlobalVariables.SHARED_PERFERENCR_SETTING, MODE_PRIVATE)
                .getInt(SHARED_PREFERENCE_SORT_MODE, 1));
        // Collections.sort(mGrades, new GradeComparator().new
        // ComparateByTime());
        // Collections.reverse(mGrades);
    }

}
