package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.LibBooksItem;

import java.util.List;

public class LibBookHistoryAdapter extends BaseAdapter {

    protected List<LibBooksItem> mItems;
    protected Context mContext;

    public LibBookHistoryAdapter(List<LibBooksItem> mItems, Context mContext) {
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
        LibBooksItem item = mItems.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_borrow_history,
                    null);
        }
        ((TextView) convertView.findViewById(R.id.textView_title)).setText(item
                .getTitle());
        ((TextView) convertView.findViewById(R.id.textView_id))
                .setText(mContext.getString(R.string.str_format_book_id,
                        item.getId()));
        ((TextView) convertView.findViewById(R.id.textView_operate_time))
                .setText(mContext.getString(R.string.str_format_return_time,
                        item.getReturnTime()));

        return convertView;
    }

}
