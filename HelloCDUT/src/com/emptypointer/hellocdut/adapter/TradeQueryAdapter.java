package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.TradeItem;

import java.util.List;

public class TradeQueryAdapter extends BaseAdapter {
    private List<TradeItem> mItems;
    private Context mContext;
    private int mCurrentMode;

    public TradeQueryAdapter(List<TradeItem> mItems, Context mContext,
                             int mCurrentMode) {
        super();
        this.mCurrentMode = mCurrentMode;
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
            convertView = View.inflate(mContext, R.layout.item_trade_record,
                    null);
        }
        switch (mCurrentMode) {
            case 0:
                ((TextView) convertView.findViewById(R.id.textView_name))
                        .setText(item.getName());
                ((TextView) convertView.findViewById(R.id.textView_amount))
                        .setText(mContext.getString(R.string.str_format_amount,
                                item.getAmount()));
                ((TextView) convertView.findViewById(R.id.textView_balance))
                        .setText(mContext.getString(R.string.str_format_balance,
                                item.getBalance()));
                ((TextView) convertView.findViewById(R.id.textView_operator))
                        .setText(mContext.getString(
                                R.string.str_format_terminal_name,
                                item.getOpertator()));
                ((TextView) convertView.findViewById(R.id.textView_location))
                        .setText(mContext.getString(R.string.str_format_location,
                                item.getLocation()));
                ((TextView) convertView.findViewById(R.id.textView_operate_time))
                        .setText(mContext.getString(R.string.str_format_time,
                                item.getTime()));
                break;
            case 1:
                ((TextView) convertView.findViewById(R.id.textView_name))
                        .setText(item.getName());
                ((TextView) convertView.findViewById(R.id.textView_amount))
                        .setText(mContext.getString(
                                R.string.str_format_back_amount, item.getAmount()));
                ((TextView) convertView.findViewById(R.id.textView_balance))
                        .setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.textView_operator))
                        .setText(mContext.getString(R.string.str_format_bank_type,
                                item.getOpertator()));
                ((TextView) convertView.findViewById(R.id.textView_location))
                        .setText(mContext.getString(R.string.str_format_result,
                                item.getDescribe()));
                ((TextView) convertView.findViewById(R.id.textView_operate_time))
                        .setText(mContext.getString(R.string.str_format_time,
                                item.getTime()));
                break;
            case 2:

                ((TextView) convertView.findViewById(R.id.textView_name))
                        .setText(item.getName());
                ((TextView) convertView.findViewById(R.id.textView_amount))
                        .setText(mContext.getString(R.string.str_format_amount,
                                item.getAmount()));
                ((TextView) convertView.findViewById(R.id.textView_balance))
                        .setText(mContext.getString(R.string.str_format_balance,
                                item.getBalance()));
                ((TextView) convertView.findViewById(R.id.textView_operator))
                        .setText(mContext.getString(R.string.str_format_opetater,
                                item.getOpertator()));
                ((TextView) convertView.findViewById(R.id.textView_location))
                        .setText(mContext.getString(R.string.str_format_location,
                                item.getLocation()));
                ((TextView) convertView.findViewById(R.id.textView_operate_time))
                        .setText(mContext.getString(R.string.str_format_time,
                                item.getTime()));
                break;

            case 3:
                convertView = View.inflate(mContext,
                        R.layout.layout_consume_overview, null);
                String name = item.getName();
                JSONObject obj = JSONObject.parseObject(name);
                String meal = obj.getString("wallet_deals_amount");
                String shower = obj.getString("shower_amount");
                String shopping = obj.getString("shopping_amount");
                String bus = obj.getString("bus_amount");
                double total = Double.valueOf(meal.replaceAll(",", ""))
                        + Double.valueOf(shower.replaceAll(",", ""))
                        + Double.valueOf(shopping.replaceAll(",", ""))
                        + Double.valueOf(bus.replaceAll(",", ""));
                ((TextView) convertView.findViewById(R.id.textView_meal))
                        .setText(meal);
                ((TextView) convertView.findViewById(R.id.textView_shower))
                        .setText(shower);
                ((TextView) convertView.findViewById(R.id.textView_shopping))
                        .setText(shopping);
                ((TextView) convertView.findViewById(R.id.textView_total))
                        .setText(String.format("%.2f", total));
                ((TextView) convertView.findViewById(R.id.textView_bus))
                        .setText(String.format(bus));
                break;
            default:
                break;
        }

        return convertView;
    }
}
