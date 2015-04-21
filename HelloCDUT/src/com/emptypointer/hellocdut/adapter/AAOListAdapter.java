package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.AAONewsItem;

import java.util.List;

public class AAOListAdapter extends BaseAdapter {
    private Context mConText;
    private List<AAONewsItem> mItems;

    public AAOListAdapter(Context mConText, List<AAONewsItem> mItems) {
        super();
        this.mConText = mConText;
        this.mItems = mItems;
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
        View view = View.inflate(mConText, R.layout.item_aao_activity_list, null);
        AAONewsItem item = mItems.get(position);
        TextView tvTitle = (TextView) view.findViewById(R.id.textView_aao_news_activity_title);
        TextView tvPostTime = (TextView) view.findViewById(R.id.textView_aao_news_activity_post_time);
        tvTitle.setText(item.getNewsTittle());
        tvPostTime.setText(item.getNewsPostDate());
        return view;
    }

}
