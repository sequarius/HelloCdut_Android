package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.CallLogItem;

import java.util.List;

public class QueryTelAdapter extends BaseAdapter {
    private List<CallLogItem> mItems;
    private Context mContext;

    public QueryTelAdapter(List<CallLogItem> mItems, Context mContext) {
        super();
        this.mItems = mItems;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CallLogItem item = mItems.get(position);
        convertView = View.inflate(mContext, R.layout.item_query_tel_list, null);
        TextView tvName = (TextView) convertView.findViewById(R.id.textView_name);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.textView_phone_number);
        TextView tvTime = (TextView) convertView.findViewById(R.id.textView_calltime);
        TextView tvType = (TextView) convertView.findViewById(R.id.textView_type);
        String name = item.getName();
        if (!TextUtils.isEmpty(name)) {
            tvName.setText(item.getName());
        }
        tvNumber.setText(item.getNumber());
        tvTime.setText(item.getCallTime());
        if (item.getStatus() == 3) {
            tvType.setVisibility(View.VISIBLE);
        }


        return convertView;
    }

}
