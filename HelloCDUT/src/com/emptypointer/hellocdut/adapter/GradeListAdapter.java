package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.GradeInfo;
import com.emptypointer.hellocdut.utils.StringChecker;

import java.util.List;

public class GradeListAdapter extends BaseAdapter {

    private List<GradeInfo> mGrades;
    private Context mContext;

    public GradeListAdapter(List<GradeInfo> mGrades, Context mContext) {
        super();
        this.mGrades = mGrades;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mGrades.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mGrades.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // TODO Auto-generated method stub
            convertView = View.inflate(mContext, R.layout.item_grade, null);
        }
        GradeInfo grade = mGrades.get(position);
        TextView tvName = (TextView) convertView
                .findViewById(R.id.textView_coursename);
        TextView tvScore = (TextView) convertView
                .findViewById(R.id.textView_score);
        TextView tvUpdateTime = (TextView) convertView
                .findViewById(R.id.textView_update_time);
        TextView tvSemester = (TextView) convertView
                .findViewById(R.id.textView_semester);
        TextView tvCourseID = (TextView) convertView
                .findViewById(R.id.textView_course_id);
        TextView tvCredits = (TextView) convertView
                .findViewById(R.id.textView_credits);
        TextView tvTeacher = (TextView) convertView
                .findViewById(R.id.textView_teacher);

        // 判斷分數區間
        String strScore = grade.getScore();
        if (StringChecker.isInteger(strScore)) {
            int score = Integer.valueOf(strScore);

            if (score >= 90) {
                tvScore.setBackgroundResource(R.drawable.course_info_04);
            } else if (score >= 80) {
                tvScore.setBackgroundResource(R.drawable.course_info_17);
            } else if (score >= 70) {
                tvScore.setBackgroundResource(R.drawable.course_info_08);
            } else if (score >= 60) {
                tvScore.setBackgroundResource(R.drawable.course_info_09);
            } else {
                tvScore.setBackgroundResource(R.drawable.course_info_15);
            }

        } else {
            switch (strScore) {
                case "优":
                    tvScore.setBackgroundResource(R.drawable.course_info_04);
                    break;

                case "良":
                    tvScore.setBackgroundResource(R.drawable.course_info_09);
                    break;

                case "中":
                    tvScore.setBackgroundResource(R.drawable.course_info_09);
                    break;

                case "及格":
                    tvScore.setBackgroundResource(R.drawable.course_info_09);
                    break;

                case "差":
                    tvScore.setBackgroundResource(R.drawable.course_info_15);
                    break;

                default:
                    tvScore.setBackgroundResource(R.drawable.course_info_01);
                    break;
            }
        }

        tvName.setText(grade.getName());
        tvScore.setText(String.valueOf(grade.getScore()));
        tvUpdateTime.setText(mContext.getString(R.string.str_format_insert_time,
                grade.getUpdateTime()));
        tvSemester.setText(mContext.getString(R.string.str_format_semester,
                grade.getSemester()));
        tvCourseID.setText(mContext.getString(R.string.str_format_course_id,
                grade.getCourseID()));
        tvCredits.setText(mContext.getString(R.string.str_format_credits,
                String.valueOf(grade.getCredits())));
        tvTeacher.setText(mContext.getString(R.string.str_format_teacher,
                grade.getTeacherName()));

        return convertView;
    }

}
