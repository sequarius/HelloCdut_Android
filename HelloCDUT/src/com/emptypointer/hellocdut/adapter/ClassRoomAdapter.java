package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.ClassRoom;

import java.util.List;

public class ClassRoomAdapter extends BaseAdapter {
    private List<ClassRoom> mIterms;
    private Context mContext;
    private String[] statuMaps = {"空闲", "教学", "借用", "考试"};
    private int[] background = {R.drawable.course_info_07,
            R.drawable.course_info_02, R.drawable.course_info_03,
            R.drawable.course_info_06};

    public ClassRoomAdapter(List<ClassRoom> mIterms, Context mContext) {
        super();
        this.mIterms = mIterms;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mIterms.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mIterms.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_classroom, null);
        }
        ClassRoom room = mIterms.get(position);
        TextView tvName = (TextView) convertView
                .findViewById(R.id.textView_class_name);
        TextView tvSeat = (TextView) convertView
                .findViewById(R.id.textView_seat_num);
        TextView tv1_2 = (TextView) convertView
                .findViewById(R.id.textView_class1_2);
        TextView tv3_4 = (TextView) convertView
                .findViewById(R.id.textView_class3_4);
        TextView tv5_6 = (TextView) convertView
                .findViewById(R.id.textView_class5_6);
        TextView tv7_8 = (TextView) convertView
                .findViewById(R.id.textView_class7_8);
        TextView tv9_11 = (TextView) convertView
                .findViewById(R.id.textView_class9_11);
        tvName.setText(room.getName());
        tvSeat.setText(room.getSeatNum());
        tv1_2.setText(statuMaps[room.getStatus()[0]]);
        tv3_4.setText(statuMaps[room.getStatus()[1]]);
        tv5_6.setText(statuMaps[room.getStatus()[2]]);
        tv7_8.setText(statuMaps[room.getStatus()[3]]);
        tv9_11.setText(statuMaps[room.getStatus()[4]]);
        tv1_2.setBackgroundResource(background[room.getStatus()[0]]);
        tv3_4.setBackgroundResource(background[room.getStatus()[1]]);
        tv5_6.setBackgroundResource(background[room.getStatus()[2]]);
        tv7_8.setBackgroundResource(background[room.getStatus()[3]]);
        tv9_11.setBackgroundResource(background[room.getStatus()[4]]);
        if (position % 2 == 0) {
            tvName.setBackgroundResource(R.drawable.course_info_basic);
            tvSeat.setBackgroundResource(R.drawable.course_info_basic_gray);
        } else {
            tvName.setBackgroundResource(R.drawable.course_info_basic_gray);
            tvSeat.setBackgroundResource(R.drawable.course_info_basic);
        }
        return convertView;
    }
}
