package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoAdapter extends BaseAdapter {
    private List<String> mKeys;
    private List<String> mValues;
    private Context mContext;
    private Map<String, String> KEY_MAPPING;
    private String[] loveStatusTable;
    private String[] orientationTable;
    private String[] genderTable;

    public UserInfoAdapter(List<String> mKeys, List<String> mValues,
                           Context mContext) {
        super();
        this.mKeys = mKeys;
        this.mValues = mValues;
        this.mContext = mContext;
        loveStatusTable = mContext.getResources().getStringArray(
                R.array.str_array_love_status);
        orientationTable = mContext.getResources().getStringArray(
                R.array.str_array_sex_orientation);
        genderTable = mContext.getResources().getStringArray(
                R.array.str_array_gender);

        KEY_MAPPING = new HashMap<String, String>();
        KEY_MAPPING.put("user_love_status",
                mContext.getString(R.string.str_love_status));
        KEY_MAPPING.put("user_sex_orientation",
                mContext.getString(R.string.str_sex_orientation));
        KEY_MAPPING.put("user_real_name",
                mContext.getString(R.string.str_real_name));
        KEY_MAPPING.put("user_birthdate",
                mContext.getString(R.string.str_birth_date));
        KEY_MAPPING.put("user_institute",
                mContext.getString(R.string.str_institute));
        KEY_MAPPING.put("user_stu_id",
                mContext.getString(R.string.str_student_number));
        KEY_MAPPING.put("user_major", mContext.getString(R.string.str_major));
        KEY_MAPPING
                .put("user_class_id", mContext.getString(R.string.str_class));
        KEY_MAPPING.put("user_entrance_year",
                mContext.getString(R.string.str_entrance_date));
        KEY_MAPPING.put("user_motto", mContext.getString(R.string.str_motto));
        KEY_MAPPING.put("user_gender", mContext.getString(R.string.str_gender));
        KEY_MAPPING.put("user_nick_name",
                mContext.getString(R.string.str_nick_name));

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mKeys.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mKeys.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_user_info, null);
        }

        TextView tvKey = (TextView) convertView.findViewById(R.id.textView_key);
        TextView tvValue = (TextView) convertView
                .findViewById(R.id.textView_value);
        String key = mKeys.get(position);
        tvKey.setText(KEY_MAPPING.get(key));
        if (key.equals("user_love_status")) {
            tvValue.setText(loveStatusTable[Integer.valueOf(mValues.get(position))]);
        } else if (key.equals("user_sex_orientation")) {
            tvValue.setText(orientationTable[Integer.valueOf(mValues.get(position))]);
        } else if (key.equals("user_gender")) {
            tvValue.setText(genderTable[Integer.valueOf(mValues.get(position))]);
        } else {
            tvValue.setText(mValues.get(position));
        }
        return convertView;
    }

}
