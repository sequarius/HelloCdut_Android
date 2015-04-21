package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import mirko.android.datetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.ClassRoomAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.ClassRoom;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPTimeService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;


public class QueryClassroomActivity extends BaseActivity {
    private static final String CACHE_EMPTY_CLASS_ROOM = "empty_class_room";

    private DataCacheDao mDao;

    public static final String FINAL_SEARCH_INFO = "final_search_class_room_info";
    private Spinner mSpinner;
    private TextView mTvDate;
    private Calendar mCalendar;
    private DatePickerDialog mDatePickerDialog;
    private ClassRoomAdapter mAdapter;
    private List<ClassRoom> mClassRrooms;
    private ListView mListView;
    // private SwipeRefreshLayout mSwipeLayout;
    private String mCurrentDate;
    private int mCurrentBuilding;
    private final String[] BUILDING_TABLE = {"1", "2", "3", "4", "5", "6a",
            "6b", "6c", "7", "8", "9", "art"};
    private String[] BUILDING_NAME_TABLE;
    private LinearLayout mLayoutSearch;
    private PtrFrameLayout mPtrFrame;

    private TextView mTvFinalUpdate;

    private EPTimeService mTimeService;
    private boolean mOnRefresh = false;
    private TextView mTvLastTime;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        BUILDING_NAME_TABLE = getResources().getStringArray(
                R.array.building_name_array);
        setContentView(R.layout.activity_query_class_room);
        // mSwipeLayout = (SwipeRefreshLayout)
        // findViewById(R.id.swipe_container);
        // mSwipeLayout.setOnRefreshListener(this);
        // mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
        // R.color.color_ep_white, android.R.color.holo_blue_bright,
        // R.color.color_ep_white);
        // mSwipeLayout.setEnabled(false);

        mTimeService = new EPTimeService(this);
        mCalendar = Calendar.getInstance();
        mTvFinalUpdate = (TextView) findViewById(R.id.textView_info_desc);
        mLayoutSearch = (LinearLayout) findViewById(R.id.layout_search);
        mTvLastTime = (TextView) findViewById(R.id.textView_last_updated);

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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        mCurrentDate = format.format(new Date());
        mDatePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePickerDialog datePickerDialog,
                                          int year, int month, int day) {
                        month++;
                        StringBuilder sb = new StringBuilder();
                        sb.append(year);
                        sb.append('-');
                        if (month < 10) {
                            sb.append('0');
                        }
                        sb.append(month);
                        sb.append('-');
                        if (day < 10) {
                            sb.append('0');
                        }
                        sb.append(day);
                        mCurrentDate = sb.toString();
                        mTvDate.setText(mCurrentDate);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        mClassRrooms = new ArrayList<ClassRoom>();
        mAdapter = new ClassRoomAdapter(mClassRrooms, this);

        mListView = (ListView) findViewById(R.id.listView_class_room);
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(new OnScrollListener() {
            private int mScollState;
            private int mFirstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                mScollState = scrollState;
                if (mFirstItem == 0
                        && mScollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    mLayoutSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                mFirstItem = firstVisibleItem;
                // if (firstVisibleItem == 0
                // && mScollState == OnScrollListener.SCROLL_STATE_IDLE) {
                // mLayoutSearch.setVisibility(View.VISIBLE);
                //
                // } else {
                // mLayoutSearch.setVisibility(View.GONE);
                // }
                // if(firstVisibleItem!=0){
                // mLayoutSearch.setVisibility(View.GONE);
                // }
                if (mFirstItem > 3) {
                    mLayoutSearch.setVisibility(View.GONE);
                }
            }
        });

        mSpinner = (Spinner) findViewById(R.id.spinner_teaching_area);
        mSpinner.setSelection(6);

        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                mCurrentBuilding = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        mTvDate = (TextView) findViewById(R.id.textView_date);
        mTvDate.setText(mCurrentDate);
        // new QueryTaskTask().execute("6a",);

        mDao = new DataCacheDao(this);
        DataCache cache = mDao.getCache(CACHE_EMPTY_CLASS_ROOM);
        if (cache == null || cache.getDate().equals("null")) {
            mPtrFrame.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mPtrFrame.autoRefresh(true);
                    new QueryTaskTask()
                            .execute(BUILDING_TABLE[5], mCurrentDate);

                }
            }, 150);

        } else {

            SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 HH:mm");
            mTvFinalUpdate.setText(getString(
                    R.string.str_format_time_final_update,
                    timeFormat.format(new Date(cache.getTime()))));
            DataCache cacheTittle = mDao.getCache(FINAL_SEARCH_INFO);
            if (cacheTittle != null) {
                getActionBar().setTitle(
                        getString(R.string.str_format_teach_build_data,
                                cacheTittle.getDate()));
            }
            JSONObject obj = JSONObject.parseObject(cache.getDate());
            jsonParse(obj.getJSONArray("rooms"));
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 日期控件点击事件
     *
     * @param view
     */
    public void onClickDataPick(View view) {
        if (!mDatePickerDialog.isVisible()) {

            mDatePickerDialog.show(getFragmentManager(),
                    String.valueOf(System.currentTimeMillis()));
        }
    }

    /**
     * 查询异步任务
     *
     * @author Sequarius
     */
    private class QueryTaskTask extends AsyncTask<String, Void, Boolean> {

        private String mSearchData;
        private String imMessage;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mOnRefresh = true;
        }

        @Override
        protected Boolean doInBackground(String... params1) {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("action", "queryEmptyRoom"));
            params.add(new BasicNameValuePair("building_num", params1[0]));
            params.add(new BasicNameValuePair("query_date", params1[1]));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERYSYSTEM, params);
                mDao.saveCache(CACHE_EMPTY_CLASS_ROOM, str);
                mSearchData = BUILDING_NAME_TABLE[mCurrentBuilding] + "("
                        + mCurrentDate + ")";
                JSONObject obj = JSONObject.parseObject(str);
                boolean result = obj.getBooleanValue("result");
                if (result) {
                    mDao.saveCache(FINAL_SEARCH_INFO, mSearchData);
                    jsonParse(obj.getJSONArray("rooms"));
                    mTimeService.setLastRefreshTime(CACHE_EMPTY_CLASS_ROOM);
                } else {
                    imMessage = obj.getString("message");
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
            mOnRefresh = false;
            mTvLastTime.setVisibility(View.INVISIBLE);
            if (result) {
                // TODO Auto-generated method stub
                SimpleDateFormat timeFormat = new SimpleDateFormat(
                        "MM月dd日 HH:mm");
                mTvFinalUpdate.setText(getString(
                        R.string.str_format_time_final_update,
                        timeFormat.format(System.currentTimeMillis())));
                getActionBar().setTitle(
                        getString(R.string.str_format_teach_build_data,
                                mSearchData));
                mAdapter.notifyDataSetChanged();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(QueryClassroomActivity.this, imMessage,
                        Toast.LENGTH_LONG));
                if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(QueryClassroomActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();
                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }

            }
            mPtrFrame.refreshComplete();
        }

    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onSearch(View view) {
        if (!mOnRefresh) {
            mTvLastTime.setText(mTimeService
                    .getLastRefreshTime(CACHE_EMPTY_CLASS_ROOM));
            mTvLastTime.setVisibility(View.VISIBLE);
            mPtrFrame.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mPtrFrame.autoRefresh(true);
                    new QueryTaskTask().execute(
                            BUILDING_TABLE[mCurrentBuilding], mCurrentDate);
                }
            }, 150);
        }

    }

    /**
     * json解析
     */
    private void jsonParse(JSONArray array) {
        try {
            mClassRrooms.clear();
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String roomName = obj.getString("roomName");
                String seatNum = obj.getString("seatNum");
                JSONArray statusArray = obj.getJSONArray("status");
                int[] a = new int[5];
                if (statusArray != null) {
                    for (int j = 0; j < statusArray.size(); j++) {
                        a[j] = statusArray.getIntValue(j);
                    }
                }
                mClassRrooms.add(new ClassRoom(roomName, seatNum, a));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
