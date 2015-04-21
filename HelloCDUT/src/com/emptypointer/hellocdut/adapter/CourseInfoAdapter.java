package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.CourseInfoGallery;
import com.emptypointer.hellocdut.domain.Course;

import java.util.List;

public class CourseInfoAdapter extends BaseAdapter {
    private Context context;
    private TextView[] courseTextViewList;
    private int screenWidth;
    private int currentWeek;

    public CourseInfoAdapter(Context context, List<Course> courseList,
                             int width, int currentWeek) {
        super();
        this.screenWidth = width;
        this.context = context;
        this.currentWeek = currentWeek;
        createGalleryWithCourseList(courseList);
    }

    private void createGalleryWithCourseList(List<Course> courseList) {
        // 五种颜色的背景
        int[] background = {R.drawable.course_info_01,
                R.drawable.course_info_02, R.drawable.course_info_04,
                R.drawable.course_info_04, R.drawable.course_info_05};
        this.courseTextViewList = new TextView[courseList.size()];
        for (int i = 0; i < courseList.size(); i++) {
            final Course course = courseList.get(i);
            TextView textView = new TextView(context);
            textView.setText(course.getFullName() + "@" + course.getRoom());
            textView.setLayoutParams(new CourseInfoGallery.LayoutParams(
                    (screenWidth / 6) * 3, (screenWidth / 6) * 3));
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(10, 0, 0, 0);
            String currentWeekString = Integer.toString(currentWeek);

            String str = "," + currentWeekString + ",";
            if (course.getWeekNum().contains(str)) {
                // 选择一个颜色背景
                int colorIndex = ((course.getBegin() - 1) * 8 + course
                        .getWhatDay()) % (background.length - 1);
                textView.setBackgroundResource(background[colorIndex]);
            } else {
                textView.setBackgroundResource(R.drawable.course_info_03);
            }
            textView.getBackground().setAlpha(222);
            // textView.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View arg0) {
            // // TODO Auto-generated method stub
            // Intent intent = new Intent();
            // Bundle mBundle = new Bundle();
            // mBundle.putSerializable("courseInfo", course);
            // intent.putExtras(mBundle);
            // intent.setClass(context, DetailCourseInfoActivity.class);
            // context.startActivity(intent);
            // }
            // });
            this.courseTextViewList[i] = textView;
        }
    }

    @Override
    public int getCount() {

        return courseTextViewList.length;
    }

    @Override
    public Object getItem(int index) {

        return courseTextViewList[index];
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return courseTextViewList[position];
    }

    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
    }

}
