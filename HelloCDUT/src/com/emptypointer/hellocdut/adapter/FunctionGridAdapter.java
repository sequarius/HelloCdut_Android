package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.FunctionItem;

import java.util.List;

public class FunctionGridAdapter extends BaseAdapter {
    private List<FunctionItem> mItems;
    private Context mContext;

    //将每个子项目封装成一个Item封装成一个List对象对象传入Adapter
    //同时传入上下文
    public FunctionGridAdapter(List<FunctionItem> mItems, Context mContext) {
        super();
        this.mItems = mItems;
        this.mContext = mContext;
    }

    @Override
    //通常情况下返回传入Items的size即可
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
    //目前尚不需要返回ID
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        FunctionItem item = mItems.get(position);
        //使用压力泵通过XML实例化一个View对象
        View view = View.inflate(mContext, R.layout.item_fucntion_gird, null);
        ImageView imAvatar = (ImageView) view.findViewById(R.id.imageView_item_fuction_avatar);
        TextView tvName = (TextView) view.findViewById(R.id.textView_item_fuction_name);
        tvName.setText(item.getFuctionName());
        Drawable drawable = mContext.getResources().getDrawable(item.getIconResID());
        imAvatar.setImageDrawable(drawable);
        return view;
    }

}
