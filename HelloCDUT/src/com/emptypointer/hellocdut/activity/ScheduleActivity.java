package com.emptypointer.hellocdut.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.CourseScheduleAdapter;
import com.emptypointer.hellocdut.customer.DividerItemDecoration;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.ScheduleDao;
import com.emptypointer.hellocdut.domain.Course;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPScheduleService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPShareService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class ScheduleActivity extends BaseActivity {
    public static final String TAG = "ScheduleActivity2nd";
    public static final String PRE_STATR_TIME = "start_time";
    public static final String SHOW_NOON_CLASS = "show_noon_class";
    public static final String BACKGROUND_COLOR = "background_color";
    private RecyclerView mRecycleView;
    private CourseScheduleAdapter mAdapter;
    private EPScheduleService mService;
    private List<List<Course>> mData;

    private static final String FILE_NAME = "hello_cdut/schedule_backgroud";

    private static final String[] BACK_GROUND_TABLE = {"#FFF9E0", "#F4FFF4",
            "#EDEDED", "#FFE8EC", "#CCEEFF", "#FFFFFF"

    };
    protected static final int MODE_GALLERY = 0;
    private static final int MODE_CUT = 2;

    private LinearLayout mLayoutBackground;

    private EPShareService mShareService;

    private boolean showNoonClass = true;
    private Spinner mSpinner;
    private static final long PRE_WEEK_TIME_MILLION = 1000 * 60 * 60 * 24 * 7;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activitiy_shedule_new);
        mService = new EPScheduleService(this);
        mShareService = new EPShareService(this);

        showNoonClass = getSharedPreferences("setting", MODE_PRIVATE)
                .getBoolean(SHOW_NOON_CLASS, true);

        ActionBar actionBar = getActionBar();
        // actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_schedule);

        mSpinner = (Spinner) actionBar.getCustomView().findViewById(
                R.id.action_bar_spinner);

        ((LinearLayout) findViewById(R.id.layout_back))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (EPApplication.getInstance().getBottomActivity() instanceof ScheduleActivity) {
                            startActivity(new Intent(
                                    GlobalVariables.ACTION_MAIN_ACTIVITY));
                        } else {
                            ScheduleActivity.this.finish();
                        }
                    }
                });

        mRecycleView = (RecyclerView) findViewById(R.id.recyclerview);

        // 创建线性布局管理器（默认是垂直方向）
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 修正重力方向
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecycleView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));

        // 为RecyclerView指定布局管理对象
        mRecycleView.setLayoutManager(layoutManager);

        mLayoutBackground = (LinearLayout) findViewById(R.id.layout_background);
        int colorPosition = getSharedPreferences("setting", MODE_PRIVATE)
                .getInt(BACKGROUND_COLOR, 0);
        if (colorPosition != 6) {
            mLayoutBackground.setBackgroundColor(Color
                    .parseColor(BACK_GROUND_TABLE[colorPosition]));
        } else {

            File file = new File(Environment.getExternalStorageDirectory(),
                    FILE_NAME);
            Drawable drawable = Drawable.createFromPath(file.getPath());
            if (drawable != null) {
                mLayoutBackground.setBackgroundDrawable(drawable);
            }
        }
        initSpiner();
        if (mService.isEmptyShedule()) {
            new GetScheduleTask().execute();
        }

        // Log.i(TAG, getCurrentWeek()+"week");
    }

    /**
     * 初始化spinner
     */
    private void initSpiner() {
        String[] weekList = getWeekList();
        SpinnerAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.item_week_list, weekList);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(getCurrentWeek() - 1);

        mSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());
    }

    private String[] getWeekList() {
        int count = mService.getMaxWeek();
        String[] weekList = new String[count];

        for (int i = 0; i < count; i++) {

            weekList[i] = new String(getString(R.string.str_format_week,
                    String.valueOf(i + 1)));

        }
        return weekList;
    }

    private int getCurrentWeek() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences preferences = getSharedPreferences("setting",
                MODE_PRIVATE);
        Date date = new Date(
                preferences.getLong(PRE_STATR_TIME, Long.MAX_VALUE));
        long startTime = date.getTime();
        long timeDiffer = currentTime - startTime;
        // 如果当前时间在起始时间之前
        if (timeDiffer < 0) {
            return 0;
        }

        int week = (int) (timeDiffer / PRE_WEEK_TIME_MILLION) + 1;
        if(week>mService.getMaxWeek()){
            return mService.getMaxWeek();
        }
        return week;
    }

    public void backToCurrentWeek() {
        mSpinner.setSelection(getCurrentWeek() - 1);
        ;
        // mService.getCourseListByWeek(getCurrentWeek(), mData);
        // mAdapter = new CourseScheduleAdapter(mData, getScreenWith() / 11,
        // mService.getColorMap2(), showNoonClass, this,
        // getWeekTimeMillion(getCurrentWeek()));
        // mRecycleView.setAdapter(mAdapter);
    }

    /**
     * 获得课表
     */
    public class GetScheduleTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(ScheduleActivity.this);
            mDialog.setCanceledOnTouchOutside(false);

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
                Log.i(TAG,str);
                JSONObject object = JSONObject.parseObject(str);
                mMessage = object.getString("message");
                boolean result = object.getBooleanValue("result");
                JSONArray time = object.getJSONArray("term_begin");
                if (time != null) {
                    int month = time.getIntValue(0);
                    int day = time.getIntValue(1);
                    // Date date = new Date(Calendar.getInstance().get(
                    // Calendar.YEAR), month - 1, day);
                    GregorianCalendar calendar = new GregorianCalendar(Calendar
                            .getInstance().get(Calendar.YEAR), month - 1, day);
                    // long timeillion=date.getTime();
                    long timeillion = calendar.getTimeInMillis();
                    SharedPreferences preferences = getSharedPreferences(
                            "setting", MODE_PRIVATE);
                    preferences.edit().putLong(PRE_STATR_TIME, timeillion)
                            .commit();
                }
                JSONArray array = object.getJSONArray("class");
                if (array != null) {
                    mService.clearSchedule();
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
                        ScheduleActivity.this, mMessage, Toast.LENGTH_SHORT));
                // 通知数据库变化
                mService.notifyDataSetChanged();
                // 重新初始化spinner
                initSpiner();
                mRecycleView.setAdapter(mAdapter);
                if (!mService.hasNoonClass()) {
                    showNoonClass = true;
                    Dialog dialog = new Builder(ScheduleActivity.this)
                            .setTitle(R.string.str_hint)
                            .setMessage(R.string.message_dont_show_noon_class)
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub

                                            setShowNoonCourse(false);

                                        }

                                    })
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    }).show();
                    CommonUtils.dialogTitleLineColor(dialog);
                }

            } else {
                CommonUtils.showCustomToast(Toast.makeText(
                        ScheduleActivity.this, mMessage, Toast.LENGTH_LONG));
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(ScheduleActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();

                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (EPApplication.getInstance().getBottomActivity() instanceof ScheduleActivity) {
            startActivity(new Intent(GlobalVariables.ACTION_MAIN_ACTIVITY));
        } else {
            finish();
        }
    }

    /**
     * 设置是否显示中午课程
     *
     * @param isShow
     */
    private void setShowNoonCourse(boolean isShow) {
        if (getSharedPreferences("setting", MODE_PRIVATE).edit()
                .putBoolean(SHOW_NOON_CLASS, isShow).commit()) {

            showNoonClass = isShow;
        }
        mAdapter = new CourseScheduleAdapter(mData, getScreenWith() / 11,
                mService.getColorMap2(), showNoonClass, this,
                getWeekTimeMillion(getCurrentWeek()));
        mRecycleView.setAdapter(mAdapter);
    }

    public long getWeekTimeMillion(int week) {
        SharedPreferences preferences = getSharedPreferences("setting",
                MODE_PRIVATE);
        Date date = new Date(
                preferences.getLong(PRE_STATR_TIME, Long.MIN_VALUE));
        long startTime = date.getTime();
        // long preweek = 1000 * 60 * 60 * 24 * 7;
        return startTime + PRE_WEEK_TIME_MILLION * (week - 1);

    }

    /**
     * 显示课程详情
     *
     * @param course
     * @param view
     */
    public void showCourseDetail(Course course, View view, int backColor) {
        String[] strArray = getResources().getStringArray(R.array.course_info);
        int[] ids = {R.id.textView_name, R.id.textView_classroom,
                R.id.textView_teacher, R.id.textView_time, R.id.textView_week,
                R.id.textView_overlay};
        TextView[] textViews = new TextView[6];
        String[] info = new String[6];
        // 课程名
        info[0] = course.getFullName() + "[" + course.getCategroy() + "]";
        // 教室
        info[1] = course.getRoom();
        // 老师
        info[2] = course.getTeacher();
        String[] WEEK_TABLE = getResources().getStringArray(R.array.week_array);
        // 时间
        int begin = course.getBegin();
        //中午課程判斷
        if (begin == 5) {
            begin = -1;

        } else if (begin > 5) {
            begin--;
        }
        int end = course.getBegin() + course.getSection() - 1;
        if (end == 5) {
            end = -1;
        } else if (end > 5) {
            end--;
        }
        //課程時間
        info[3] = new StringBuilder().append(WEEK_TABLE[course.getWhatDay()])
                .append(" ").append(begin == -1 ? "中午" : begin).append((begin == end) ? "" : "-")
                .append((begin == end) ? "" : end).append(begin == -1 ? "" : "节").toString();
        // 周数
        JSONArray array = JSONArray.parseArray(course.getWeekNum());
        StringBuilder sb = new StringBuilder(" ");
        if (array != null) {
            int[] intArray = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                intArray[i] = array.getIntValue(i);
            }
            Arrays.sort(intArray);
            int startFlag = 0;
            for (int i = 1; i < intArray.length; i++) {
                int current = intArray[i];
                int front = intArray[i - 1];
                if (current - front != 1) {
                    int startWeek = intArray[startFlag];
                    if (startWeek != front) {
                        sb.append(startWeek).append("-").append(front)
                                .append(",");
                    } else {
                        sb.append(startWeek).append(",");
                    }
                    startFlag = i;
                }

            }
            int startWeek = intArray[startFlag];
            int front = intArray[intArray.length - 1];
            if (startWeek != front) {
                sb.append(startWeek).append("-").append(front).append(" ");
            } else {
                sb.append(startWeek).append(" ");
            }
        }
        info[4] = getString(R.string.str_format_week, sb.toString());
        // 重课
        info[5] = getString(course.getOverLapStatus() == 0 ? R.string.str_negative
                : R.string.str_positive);

        // 备注
//        info[6] = course.getNote().equals("NONE") ? getString(R.string.str_none)
//                : course.getNote().replace("备注:", "");
        View contentView = View.inflate(this, R.layout.popmenu_course_detail,
                null);
        ((RelativeLayout) contentView
                .findViewById(R.id.layout_course_detail_background))
                .setBackgroundColor(backColor);
        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = (TextView) contentView.findViewById(ids[i]);
            textViews[i].setText(String.format(strArray[i], info[i]));
        }
        PopupWindow window = new PopupWindow(contentView,
                (int) (getScreenWith() * 0.75), LayoutParams.WRAP_CONTENT, true);
        window.setTouchable(true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new BitmapDrawable(getResources(),
                (Bitmap) null));
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
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

                new GetScheduleTask().execute();
                break;
            case R.id.action_setting_back_ground:
                setCustomBackground();

                break;
            case R.id.action_setting_schedule:
                createScheduleSettingDialog();
                break;
            case R.id.action_add_laucher:
                createShortCut();
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
     * 修改activity的背景
     */
    private void setCustomBackground() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.str_set_background));
        LinearLayout contentViewLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.layout_dialog_schedule_background, null);
        final Dialog dialog = builder.setView(contentViewLayout).show();
        CommonUtils.dialogTitleLineColor(dialog);
        RadioGroup group = (RadioGroup) contentViewLayout
                .findViewById(R.id.radioGroup);
        final int[] radioIDs = {R.id.radio_yellow, R.id.radio_green,
                R.id.radio_gray, R.id.radio_pink, R.id.radio_blue,
                R.id.radio_white, R.id.radio_from_gallery};
        RadioButton[] buttons = new RadioButton[7];
        for (int i = 0; i < radioIDs.length; i++) {
            buttons[i] = (RadioButton) contentViewLayout
                    .findViewById(radioIDs[i]);
        }
        int cheackedItem = getSharedPreferences("setting", MODE_PRIVATE)
                .getInt(BACKGROUND_COLOR, 0);
        buttons[cheackedItem].setChecked(true);

        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int position = 0;
                switch (checkedId) {
                    case R.id.radio_yellow:
                        position = 0;
                        break;
                    case R.id.radio_green:
                        position = 1;
                        break;
                    case R.id.radio_gray:
                        position = 2;
                        break;
                    case R.id.radio_pink:
                        position = 3;
                        break;
                    case R.id.radio_blue:
                        position = 4;
                        break;
                    case R.id.radio_white:
                        position = 5;
                        break;
                    case R.id.radio_from_gallery:
                        position = 6;
                        break;

                    default:
                        break;
                }
                if (position != 6) {
                    mLayoutBackground.setBackgroundColor(Color
                            .parseColor(BACK_GROUND_TABLE[position]));
                    getSharedPreferences("setting", MODE_PRIVATE).edit()
                            .putInt(BACKGROUND_COLOR, position).commit();
                } else {
                    if (!CommonUtils.isExitsSdcard()) {
                        CommonUtils.customToast(
                                R.string.message_wrong_unexsisted_sd_card,
                                ScheduleActivity.this, true);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, MODE_GALLERY);
                }
                dialog.dismiss();
            }
        });

    }

    /**
     * 创建桌面快捷方式
     */
    private void createShortCut() {
        Intent shortcut = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");

        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.str_schedule));
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        Intent shortcutIntent = new Intent("com.emptypointer.action.Schedule");
        shortcutIntent.setClassName(this, this.getClass().getName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        // 快捷方式的图标
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
                this, R.drawable.ic_function_schedule);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        sendBroadcast(shortcut);
        CommonUtils.customToast(R.string.message_create_shortcut, this, false);
    }

    /**
     * 修改课程设置
     */
    private void createScheduleSettingDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.str_course_setting));
        RelativeLayout contentViewLayout = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.layout_dialog_schedule_setting, null);
        builder.setView(contentViewLayout);
        final RadioButton buttonShow = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_show);
        final RadioButton buttonHide = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_hide);
        if (showNoonClass) {
            buttonShow.setChecked(true);
        } else {
            buttonHide.setChecked(true);
        }
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 如果选项没改变
                        if (buttonShow.isChecked() == showNoonClass) {
                            return;
                        }
                        if (buttonShow.isChecked()) {
                            setShowNoonCourse(true);
                        } else {
                            if (mService.hasNoonClass()) {// 如果中午还有课
                                Dialog dialogHint = new Builder(
                                        ScheduleActivity.this)
                                        .setCancelable(false)
                                        .setTitle(R.string.str_hint)
                                        .setMessage(
                                                R.string.message_has_noon_class)
                                        .setPositiveButton(
                                                R.string.ok,
                                                new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        // TODO Auto-generated
                                                        // method stub
                                                        setShowNoonCourse(false);
                                                    }
                                                })
                                        .setNegativeButton(
                                                R.string.cancel,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {

                                                    }
                                                }).show();
                                CommonUtils.dialogTitleLineColor(dialogHint);
                            } else {
                                setShowNoonCourse(false);
                            }
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
        CommonUtils.dialogTitleLineColor(builder.show());

    }

    class SpinnerItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // TODO Auto-generated method stub
            int week = position + 1;
            if (mData == null) {
                mData = new ArrayList<List<Course>>();
            }
            mService.getCourseListByWeek(week, mData);

            mAdapter = new CourseScheduleAdapter(mData, getScreenWith() / 11,
                    mService.getColorMap2(), showNoonClass,
                    ScheduleActivity.this, getWeekTimeMillion(week));
            mRecycleView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            if (week == getCurrentWeek()) {
                int dayOfWeek = Calendar.getInstance()
                        .get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek > 4 || dayOfWeek == 1) {
                    mRecycleView.scrollToPosition(7);
                }
            } else {
                mRecycleView.scrollToPosition(0);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == MODE_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                // 裁剪框的比例，1：1
                intent.putExtra("aspectX", 9);
                intent.putExtra("aspectY", 16);
                // 裁剪后输出图片的尺寸大小
                intent.putExtra("outputX", 720);
                intent.putExtra("outputY", 1080);
                // 图片格式
                intent.putExtra("outputFormat", "JPEG");
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("noFaceDetection", true);// 取消人脸识别
                intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
                File dir = Environment.getExternalStorageDirectory();
                File file = new File(dir, FILE_NAME);
                // intent.putExtra("output", Uri.parse(file.getAbsolutePath()));
                intent.putExtra("outputFormat",
                        Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, MODE_CUT);
            }

        } else if (requestCode == MODE_CUT) {
            if (data == null) {
                return;
            }
            // Bitmap bitmap = data.getParcelableExtra("data");
            // try {
            // saveBitmap(bitmap);
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir, FILE_NAME);
            Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
            mLayoutBackground.setBackgroundDrawable(drawable);

            getSharedPreferences("setting", MODE_PRIVATE).edit()
                    .putInt(BACKGROUND_COLOR, 6).commit();
        }
    }

}
