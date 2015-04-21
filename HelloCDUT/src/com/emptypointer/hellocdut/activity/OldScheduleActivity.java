package com.emptypointer.hellocdut.activity;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.ScheduleDao;
import com.emptypointer.hellocdut.domain.Course;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPScheduleService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPShareService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class OldScheduleActivity extends BaseActivity {
    // 标题栏文字
    protected TextView textTitle;
    // 第一个无内容的格子
    protected TextView empty;
    // 星期一的格子
    protected TextView monColum;
    // 星期二的格子
    protected TextView tueColum;
    // 星期三的格子
    protected TextView wedColum;
    // 星期四的格子
    protected TextView thrusColum;
    // 星期五的格子
    protected TextView friColum;
    // 星期六的格子
    protected TextView satColum;
    // 星期日的格子
    protected TextView sunColum;
    // 课程表body部分布局
    protected RelativeLayout course_table_layout;
    // 选择周数弹出窗口
    protected PopupWindow weekListWindow;
    // 显示周数的listview
    protected ListView weekListView;
    // 选择周数弹出窗口的layout
    protected View popupWindowLayout;
    // 课程信息
    protected Map<String, List<Course>> courseInfoMap;
    // 保存显示课程信息的TextView
    protected List<TextView> courseTextViewList = new ArrayList<TextView>();
    // 保存每个textview对应的课程信息 map,key为哪一天（如星期一则key为1）
    protected Map<Integer, List<Course>> textviewCourseInfoMap = new HashMap<Integer, List<Course>>();
    // 课程格子平均宽度
    protected int aveWidth;
    // 屏幕宽度
    protected int screenWidth;
    // 格子高度
    protected int gridHeight = 80;
    // 最大课程节数
    protected final int maxCourseNum = 12;
    // 当前星期数
    int currentWeek;
    // 颜色
    protected Map<String, Integer> colorMap = new HashMap<String, Integer>();

    protected Button goBackButton;

    protected ProgressDialog pDialog;

    protected Handler mhandler = new Handler();

    private EPScheduleService mservice;

    private EPShareService mShareService;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);

        mShareService = new EPShareService(this);

        setContentView(R.layout.activity_schedule_table);

        empty = (TextView) this.findViewById(R.id.textview_empty);
        monColum = (TextView) this.findViewById(R.id.textview_monday_course);
        tueColum = (TextView) this.findViewById(R.id.textview_tuesday_course);
        wedColum = (TextView) this.findViewById(R.id.textview_wednesday_course);
        thrusColum = (TextView) this
                .findViewById(R.id.textview_thursday_course);
        friColum = (TextView) this.findViewById(R.id.textview_friday_course);
        satColum = (TextView) this.findViewById(R.id.textview_saturday_course);
        sunColum = (TextView) this.findViewById(R.id.textview_sunday_course);

        course_table_layout = (RelativeLayout) this
                .findViewById(R.id.relativelayout_body);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度
        int width = dm.widthPixels;
        // 平均宽度
        int aveWidth = width / 8;
        // 给列头设置宽度
        empty.setWidth(aveWidth * 3 / 4);
        monColum.setWidth(aveWidth * 33 / 32 + 1);
        tueColum.setWidth(aveWidth * 33 / 32 + 1);
        wedColum.setWidth(aveWidth * 33 / 32 + 1);
        thrusColum.setWidth(aveWidth * 33 / 32 + 1);
        friColum.setWidth(aveWidth * 33 / 32 + 1);
        satColum.setWidth(aveWidth * 33 / 32 + 1);
        sunColum.setWidth(aveWidth * 33 / 32 + 1);
        this.screenWidth = width;
        this.aveWidth = aveWidth;
        // 初始化body部分
        mservice = new EPScheduleService(this);
        int currentWeek = getCurrentWeek();
        // new GetScheduleTask().execute();
        String[] weekList = getWeekList();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_schedule);

        Spinner mSpinner = (Spinner) actionBar.getCustomView().findViewById(
                R.id.action_bar_spinner);
        // TextView mTextView = (TextView)
        // actionBar.getCustomView().findViewById(
        // R.id.action_bar_title);

        SpinnerAdapter mAdapter = new ArrayAdapter<String>(this,
                R.layout.item_week_list, weekList);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setSelection(currentWeek - 1);

        // String titleString = "第" + currentWeek + "周（本周）";
        // mTextView.setText(titleString);
        mSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());
        // 使自定义的普通View能在title栏显示, actionBar.setCustomView能起作用.

        // SpinnerAdapter mAdapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_spinner_dropdown_item, weekList);
        // ActionBar actionBar = getActionBar();
        // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // actionBar.setListNavigationCallbacks(mAdapter,
        // new OnNavigationListener() {
        //
        // @Override
        // public boolean onNavigationItemSelected(int itemPosition,
        // long itemId) {
        // // TODO 选择周数查看课表
        // int id = itemPosition + 1;
        // init(id);
        // return false;
        // }
        // });
        if (mservice.isEmptyShedule()) {
            new GetScheduleTask().execute();
        }

        init(currentWeek);
    }

    class SpinnerItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // TODO Auto-generated method stub
            int _id = (int) id + 1;
            init(_id);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }
    }

    protected String[] getWeekList() {
        // TODO 获取周数列表
        String[] weekList = new String[20];
        int m = getCurrentWeek();
        for (int i = 1; i < 21; i++) {
            if (i == m) {
                weekList[i - 1] = new String("第" + i + "周（本周）");
            } else {
                weekList[i - 1] = new String("第" + i + "周");
            }
        }
        return weekList;
    }

    @SuppressLint("SimpleDateFormat")
    public int getCurrentWeek() {
        // TODO 获取当前周
        Date date = new Date();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        simpledateformat.format(date);
        Date date1 = null;
        try {
            date1 = simpledateformat.parse("2015-03-08");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpledateformat.parse("2015-03-16");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date3 = null;
        try {
            date3 = simpledateformat.parse("2015-03-15");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date4 = null;
        try {
            date4 = simpledateformat.parse("2015-03-23");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date5 = null;
        try {
            date5 = simpledateformat.parse("2015-03-22");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date6 = null;
        try {
            date6 = simpledateformat.parse("2015-03-30");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date7 = null;
        try {
            date7 = simpledateformat.parse("2015-03-29");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date8 = null;
        try {
            date8 = simpledateformat.parse("2015-04-06");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date9 = null;
        try {
            date9 = simpledateformat.parse("2015-04-05");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date10 = null;
        try {
            date10 = simpledateformat.parse("2015-04-13");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date11 = null;
        try {
            date11 = simpledateformat.parse("2015-04-12");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date12 = null;
        try {
            date12 = simpledateformat.parse("2015-04-20");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date13 = null;
        try {
            date13 = simpledateformat.parse("2015-04-19");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date14 = null;
        try {
            date14 = simpledateformat.parse("2015-04-27");
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        Date date15 = null;
        try {
            date15 = simpledateformat.parse("2015-04-26");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date16 = null;
        try {
            date16 = simpledateformat.parse("2015-05-04");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date17 = null;
        try {
            date17 = simpledateformat.parse("2015-05-03");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date18 = null;
        try {
            date18 = simpledateformat.parse("2015-05-11");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date19 = null;
        try {
            date19 = simpledateformat.parse("2015-05-10");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date20 = null;
        try {
            date20 = simpledateformat.parse("2015-05-18");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date21 = null;
        try {
            date21 = simpledateformat.parse("2015-05-17");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date22 = null;
        try {
            date22 = simpledateformat.parse("2015-05-25");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date23 = null;
        try {
            date23 = simpledateformat.parse("2015-05-27");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date24 = null;
        try {
            date24 = simpledateformat.parse("2015-06-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date25 = null;
        try {
            date25 = simpledateformat.parse("2015-05-31");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date26 = null;
        try {
            date26 = simpledateformat.parse("2015-06-08");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date27 = null;
        try {
            date27 = simpledateformat.parse("2015-06-07");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date28 = null;
        try {
            date28 = simpledateformat.parse("2015-06-15");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date29 = null;
        try {
            date29 = simpledateformat.parse("2015-06-14");
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        Date date30 = null;
        try {
            date30 = simpledateformat.parse("2015-06-22");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date31 = null;
        try {
            date31 = simpledateformat.parse("2015-06-21");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date32 = null;
        try {
            date32 = simpledateformat.parse("2015-06-29");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date33 = null;
        try {
            date33 = simpledateformat.parse("2015-06-28");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date34 = null;
        try {
            date34 = simpledateformat.parse("2015-07-06");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date35 = null;
        try {
            date35 = simpledateformat.parse("2015-07-05");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date36 = null;
        try {
            date36 = simpledateformat.parse("2015-07-13");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1.after(date)) {
            return 1;
        } else if (date1.before(date) && date2.after(date)) {
            return 1;
        } else if (date3.before(date) && date4.after(date)) {
            return 2;
        } else if (date5.before(date) && date6.after(date)) {
            return 3;
        } else if (date7.before(date) && date8.after(date)) {
            return 4;
        } else if (date9.before(date) && date10.after(date)) {
            return 5;
        } else if (date11.before(date) && date12.after(date)) {
            return 6;
        } else if (date13.before(date) && date14.after(date)) {
            return 7;
        } else if (date15.before(date) && date16.after(date)) {
            return 8;
        } else if (date17.before(date) && date18.after(date)) {
            return 9;
        } else if (date19.before(date) && date20.after(date)) {
            return 10;
        } else if (date21.before(date) && date22.after(date)) {
            return 11;
        } else if (date23.before(date) && date24.after(date)) {
            return 12;
        } else if (date25.before(date) && date26.after(date)) {
            return 13;
        } else if (date27.before(date) && date28.after(date)) {
            return 14;
        } else if (date29.before(date) && date30.after(date)) {
            return 15;
        } else if (date31.before(date) && date32.after(date)) {
            return 16;
        } else if (date33.before(date) && date34.after(date)) {
            return 17;
        } else {
            return 18;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_schedule_refresh:
                mservice.clearSchedule();
                new GetScheduleTask().execute();
                break;
            case R.id.action_schedule_share:
                View view = this.getWindow().getDecorView().getRootView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();
                mShareService.shareContent("jsd", null, bitmap, "课表分享");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化课程表body部分
     *
     * @param aveWidth
     */
    @SuppressWarnings("deprecation")
    protected void init(int week) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 屏幕高度
        int height = dm.heightPixels;
        gridHeight = height / maxCourseNum;
        // 设置课表界面
        // 动态生成7 * maxCourseNum个textview
        for (int i = 1; i <= maxCourseNum; i++) {

            for (int j = 1; j <= 8; j++) {

                TextView tx = new TextView(OldScheduleActivity.this);
                tx.setId((i - 1) * 8 + j);
                // 除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                if (j < 8)
                    tx.setBackgroundDrawable(OldScheduleActivity.this
                            .getResources().getDrawable(
                                    R.drawable.course_text_view_bg));
                else
                    tx.setBackgroundDrawable(OldScheduleActivity.this
                            .getResources().getDrawable(
                                    R.drawable.course_table_last_colum));
                // 相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                        aveWidth * 33 / 32 + 1, gridHeight);
                // 文字对齐方式
                tx.setGravity(Gravity.CENTER);
                // 如果是第一列，需要设置课的序号（1 到 12）
                if (j == 1) {
                    if (i == 5) {
                        tx.setText("午");
                    } else if (i > 4) {
                        tx.setText(String.valueOf(i - 1));
                    } else {
                        tx.setText(String.valueOf(i));
                    }
                    rp.width = aveWidth * 3 / 4;
                    // 设置他们的相对位置
                    if (i == 1)
                        rp.addRule(RelativeLayout.BELOW, empty.getId());
                    else
                        rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                } else {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
                    tx.setText("");
                }

                tx.setLayoutParams(rp);
                course_table_layout.addView(tx);
            }
        }

        // TODO 获取课程信息
        currentWeek = week;
        // 从数据库中读取课程信息，存放在courseInfoMap中，key为星期几，value是这一天的课程信息
        courseInfoMap = new HashMap<String, List<Course>>();
        courseInfoMap = mservice.getAllByWeek(courseInfoMap, currentWeek);
        // 获得颜色map
        colorMap = mservice.getColorMap(colorMap);
        // 发送更新界面信息
        Message msg = new Message();

        InitMessageObj msgObj = new InitMessageObj(aveWidth, currentWeek,
                screenWidth, maxCourseNum);
        msg.obj = msgObj;
        courseInfoInitMessageHandler.sendMessage(msg);

    }

    CourseInfoInitMessageHandler courseInfoInitMessageHandler = new CourseInfoInitMessageHandler(
            this);

    static class InitMessageObj {

        int aveWidth;
        int currentWeek;
        int screenWidth;
        int maxCourseNum;

        public InitMessageObj(int aveWidth, int currentWeek, int screenWidth,
                              int maxCourseNum) {
            super();
            this.aveWidth = aveWidth;
            this.currentWeek = currentWeek;
            this.screenWidth = screenWidth;
            this.maxCourseNum = maxCourseNum;
        }

    }

    // 初始化课程表的messageHandler
    static class CourseInfoInitMessageHandler extends Handler {

        WeakReference<OldScheduleActivity> mActivity;

        public CourseInfoInitMessageHandler(OldScheduleActivity activity) {

            mActivity = new WeakReference<OldScheduleActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            // 五种颜色的背景
            int[] background = {R.drawable.course_info_01,
                    R.drawable.course_info_02, R.drawable.course_info_03,
                    R.drawable.course_info_04, R.drawable.course_info_05,
                    R.drawable.course_info_06, R.drawable.course_info_07,
                    R.drawable.course_info_08, R.drawable.course_info_09,
                    R.drawable.course_info_10, R.drawable.course_info_11,
                    R.drawable.course_info_12, R.drawable.course_info_13,
                    R.drawable.course_info_14, R.drawable.course_info_15,
                    R.drawable.course_info_16, R.drawable.course_info_17,
                    R.drawable.course_info_18, R.drawable.course_info_19,
                    R.drawable.course_info_20};
            // 获取课程信息的map
            Map<String, List<Course>> courseInfoMap = mActivity.get().courseInfoMap;
            // 获取课程颜色的map
            Map<String, Integer> colorMap = mActivity.get().colorMap;
            // 一些传过来的参数
            final InitMessageObj msgObj = (InitMessageObj) msg.obj;
            // 当前周数
            // int currentWeek = msgObj.currentWeek;
            // 最大课程节数
            // int maxCourseNum = msgObj.maxCourseNum;
            for (Map.Entry<String, List<Course>> entry : courseInfoMap
                    .entrySet()) {

                Course upperCourse = null;
                // list里保存的是一周内某一天的课程
                final List<Course> list = new ArrayList<Course>(
                        entry.getValue());
                // 按开始的时间（哪一节）进行排序
                Collections.sort(list, new Comparator<Course>() {
                    @Override
                    public int compare(Course arg0, Course arg1) {

                        if (arg0.getBegin() < arg1.getBegin())
                            return -1;
                        else
                            return 1;
                    }

                });
                int lastListSize;
                do {

                    lastListSize = list.size();
                    Iterator<Course> iter = list.iterator();
                    // 先查找出第一个在周数范围内的课
                    while (iter.hasNext()) {
                        Course c = iter.next();//
                        // if ((c.getBegin() <= currentWeek && c.getSection() >=
                        // currentWeek)
                        // && c.getSection() <= maxCourseNum) {
                        iter.remove();
                        upperCourse = c;
                        break;

                        // }
                    }
                    if (upperCourse != null) {
                        List<Course> courseInfoList = new ArrayList<Course>();
                        courseInfoList.add(upperCourse);
                        int index = 0;
                        // iter = list.iterator();
                        // TODO 重课-------| 查找这一天有哪些课与刚刚查找出来的顶层课相交
                        // while (iter.hasNext()) {
                        // CourseInfo c = iter.next();
                        // // 先判断该课程与upperCourse是否相交，如果相交加入courseInfoList中
                        // if ((c.getbegin() <= upperCourse;
                        // .getBegin() && upperCourse
                        // .getBegin() < c.getsection())
                        // || (upperCourse.getbegin() <= c
                        // .getBegin() && c
                        // .getBegin() < upperCourse
                        // .getSection())) {
                        // courseInfoList.add(c);
                        // iter.remove();
                        // // 在判断哪个跨度大，跨度大的为顶层课程信息
                        // if ((c.getsection() - c.getsection()) >
                        // (upperCourse
                        // .getSection() - upperCourse
                        // .getBegin())
                        // && ((c.getBeginWeek() <= currentWeek && c
                        // .getEndWeek() >= currentWeek) || currentWeek == -1))
                        // {
                        // upperCourse = c;
                        // index++;
                        // }
                        //
                        // }
                        //
                        // }
                        // 记录顶层课程在courseInfoList中的索引位置
                        // final int upperCourseIndex = index;
                        // 动态生成课程信息TextView
                        TextView courseInfo = new TextView(mActivity.get());
                        courseInfo.setId(1000 + upperCourse.getWhatDay() * 100
                                + upperCourse.getBegin() * 10);
                        int id = courseInfo.getId();
                        mActivity.get().textviewCourseInfoMap.put(id,
                                courseInfoList);
                        courseInfo.setText(upperCourse.getFullName() + "\n@"
                                + upperCourse.getRoom());
                        // 该textview的高度根据其节数的跨度来设置
                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                                msgObj.aveWidth * 31 / 32,
                                (mActivity.get().gridHeight - 5) * 2
                                        + (upperCourse.getSection() - 2)
                                        * mActivity.get().gridHeight);
                        // textview的位置由课程开始节数和上课的时间确定
                        rlp.topMargin = 5 + (upperCourse.getBegin() - 1)
                                * mActivity.get().gridHeight;
                        rlp.leftMargin = 1;
                        // 前面生成格子时的ID就是根据Day来设置的
                        rlp.addRule(RelativeLayout.RIGHT_OF,
                                upperCourse.getWhatDay());
                        // 字体居中
                        courseInfo.setGravity(Gravity.CENTER);
                        // 选择颜色背景
                        String courseMd5 = CommonUtils.getStringMD5(upperCourse
                                .getFullName());
                        int colorIndex = colorMap.get(courseMd5);
                        // int colorIndex = ((upperCourse.getBegin() - 1) * 8 +
                        // upperCourse
                        // .getWhatDay()) % (background.length - 1);
                        // int colorIndex = 0;
                        String temp = colorIndex + "";
                        // Log.i("colorMap", temp + upperCourse.getFullName());
                        courseInfo
                                .setBackgroundResource(background[colorIndex]);
                        courseInfo.setTextSize(12);
                        courseInfo.setLayoutParams(rlp);
                        courseInfo.setTextColor(Color.WHITE);
                        // 设置不透明度
                        courseInfo.getBackground().setAlpha(210);
                        // 设置监听事件
//						courseInfo.setOnClickListener(new OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								Map<Integer, List<Course>> map = mActivity
//										.get().textviewCourseInfoMap;
//								final List<Course> tempList = map.get(v.getId());
//								// if (tempList.size() > 1) {
//								// // 如果有多个课程，则设置点击弹出gallery 3d 对话框
//								// LayoutInflater layoutInflater =
//								// (LayoutInflater) mActivity
//								// .get()
//								// .getSystemService(
//								// Context.LAYOUT_INFLATER_SERVICE);
//								// View galleryView = layoutInflater.inflate(
//								// R.layout.gallery_course_info, null);
//								// final Dialog coursePopupDialog = new
//								// AlertDialog.Builder(
//								// mActivity.get()).create();
//								// coursePopupDialog
//								// .setCanceledOnTouchOutside(true);
//								// coursePopupDialog.setCancelable(true);
//								// coursePopupDialog.show();
//								// WindowManager.LayoutParams params =
//								// coursePopupDialog
//								// .getWindow().getAttributes();
//								// params.width = LayoutParams.FILL_PARENT;
//								// coursePopupDialog.getWindow()
//								// .setAttributes(params);
//								// CourseInfoAdapter adapter = new
//								// CourseInfoAdapter(
//								// mActivity.get(), tempList,
//								// msgObj.screenWidth,
//								// msgObj.currentWeek);
//								// CourseInfoGallery gallery =
//								// (CourseInfoGallery) galleryView
//								// .findViewById(R.id.gallery_info_course);
//								// gallery.setSpacing(10);
//								// gallery.setAdapter(adapter);
//								// gallery.setSelection(upperCourseIndex);
//								// // TODO 详细课程查看
//								// gallery.setOnItemClickListener(new
//								// OnItemClickListener() {
//								// @Override
//								// public void onItemClick(
//								// AdapterView<?> arg0, View arg1,
//								// int arg2, long arg3) {
//								// CourseInfo courseInfo = tempList
//								// .get(arg2);
//								// Intent intent = new Intent();
//								// Bundle mBundle = new Bundle();
//								// mBundle.putSerializable(
//								// "courseInfo", courseInfo);
//								// intent.putExtras(mBundle);
//								// intent.setClass(
//								// mActivity.get(),
//								// DetailCourseInfoActivity.class);
//								// mActivity.get().startActivity(
//								// intent);
//								// coursePopupDialog.dismiss();
//								// }
//								// });
//								// coursePopupDialog
//								// .setContentView(galleryView);
//								// } else {
//								// Intent intent = new Intent();
//								// Bundle mBundle = new Bundle();
//								// mBundle.putSerializable("courseInfo",
//								// tempList.get(0));
//								// intent.putExtras(mBundle);
//								// intent.setClass(mActivity.get(),
//								// DetailCourseInfoActivity.class);
//								// mActivity.get().startActivity(intent);
//								// }
//								Intent intent = new Intent();
//								Bundle mBundle = new Bundle();
//								mBundle.putSerializable("courseInfo",
//										tempList.get(0));
//								intent.putExtras(mBundle);
////								intent.setClass(mActivity.get(),
////										ScheduleDetailActivity.class);
//								mActivity.get().startActivity(intent);
//							}
//						});
                        mActivity.get().course_table_layout.addView(courseInfo);
                        // mActivity.get().courseTextViewList.add(courseInfo);
                        upperCourse = null;
                    }
                } while (list.size() < lastListSize && list.size() != 0);
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 获得课表
     */
    public class GetScheduleTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(OldScheduleActivity.this);
            mDialog.setMessage(getString(R.string.str_load_schedule));
            mDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params1) {
            ScheduleDao dao = new ScheduleDao(getApplicationContext());
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            params.add(new BasicNameValuePair("action", "querySchedule"));
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERYSYSTEM, params);
                JSONObject object = JSONObject.parseObject(str);
                mMessage = object.getString("message");
                boolean result = object.getBooleanValue("result");
                JSONArray array = object.getJSONArray("class");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonCourse = array.getJSONObject(i);
                    String weekNum = jsonCourse.getString("week_num");
                    int whatDay = jsonCourse.getIntValue("what_day");
                    int begin = jsonCourse.getIntValue("begin");
                    int section = jsonCourse.getIntValue("section");
                    String fullName = jsonCourse.getString("full_name");
                    String categroy = jsonCourse.getString("category");
                    int overLapStatus = jsonCourse
                            .getIntValue("is_OverLap_Class");
                    String credits = jsonCourse.getString("credits");
                    String teacher = jsonCourse.getString("teacher");
                    String period = jsonCourse.getString("period");
                    String room = jsonCourse.getString("room");
                    String note = jsonCourse.getString("note");
                    Course course = new Course(weekNum, whatDay, begin,
                            section, fullName, categroy, overLapStatus,
                            credits, teacher, period, room, note);
                    dao.addCourse(course);
                }
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            mDialog.dismiss();
            if (result) {
                CommonUtils.showCustomToast(Toast.makeText(
                        OldScheduleActivity.this, mMessage, Toast.LENGTH_SHORT));
            } else {
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(OldScheduleActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().setToken(null);
                    EPApplication.getInstance().clearAcitivy();
                    startActivity(intent);
                }
                CommonUtils.showCustomToast(Toast.makeText(
                        OldScheduleActivity.this, mMessage, Toast.LENGTH_LONG));
            }
            mservice.notifyDataSetChanged();
            int m = getCurrentWeek();
            init(m);
        }
    }

}
