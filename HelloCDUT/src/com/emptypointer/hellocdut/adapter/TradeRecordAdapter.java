package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.TradeItem;

import java.util.List;

public class TradeRecordAdapter extends BaseAdapter {
    private List<TradeItem> mItems;
    private Context mContext;

    public TradeRecordAdapter(List<TradeItem> mItems, Context mContext) {
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TradeItem item = mItems.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_trade_record, null);
        }
        ((TextView) convertView.findViewById(R.id.textView_name)).setText(item.getName());
        ((TextView) convertView.findViewById(R.id.textView_amount)).setText(mContext.getString(R.string.str_format_amount, item.getAmount()));
        ((TextView) convertView.findViewById(R.id.textView_balance)).setText(mContext.getString(R.string.str_format_balance, item.getBalance()));
        ((TextView) convertView.findViewById(R.id.textView_operator)).setText(mContext.getString(R.string.str_format_opetater, item.getOpertator()));
        ((TextView) convertView.findViewById(R.id.textView_location)).setText(mContext.getString(R.string.str_format_location, item.getLocation()));
        ((TextView) convertView.findViewById(R.id.textView_operate_time)).setText(mContext.getString(R.string.str_format_time, item.getTime()));
        return convertView;
    }

}
