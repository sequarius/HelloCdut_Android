package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.emptypointer.hellocdut.R;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<EMGroup> {

    private LayoutInflater inflater;

    public GroupAdapter(Context context, int res, List<EMGroup> groups) {
        super(context, res, groups);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_group, null);
        }

        ((TextView) convertView.findViewById(R.id.name)).setText(getItem(
                position).getGroupName());

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}