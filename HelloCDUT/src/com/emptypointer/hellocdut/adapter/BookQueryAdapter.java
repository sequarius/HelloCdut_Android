package com.emptypointer.hellocdut.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.QueryBook;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class BookQueryAdapter extends BaseAdapter {
    private List<QueryBook> mBooks;
    private Context mContext;
    //	ImageLoader imageLoader;
    private DisplayImageOptions options;


    public BookQueryAdapter(List<QueryBook> mBooks, Context mContext) {
        super();
        this.mBooks = mBooks;
        this.mContext = mContext;
//		imageLoader = ImageLoaderFactory.create(mContext);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.ic_error_loaded)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mBooks.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final QueryBook book = mBooks.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.row_query_book, null);
        }
        final ImageView image = ((ImageView) convertView.findViewById(R.id.imageView_avatar));
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ImageLoader.getInstance().displayImage(book.getImageURL(),
                        image, options);
            }
        });

        ((TextView) convertView.findViewById(R.id.textView_name)).setText(book
                .getName());
        ((TextView) convertView.findViewById(R.id.textView_writer))
                .setText(book.getWriter());
        ((TextView) convertView.findViewById(R.id.textView_index_id))
                .setText(book.getBookIndex().replace("图书馆", "索书号"));
        ((TextView) convertView.findViewById(R.id.textView_book_count))
                .setText(book.getBookCount());
        ((TextView) convertView.findViewById(R.id.textView_publish))
                .setText(mContext.getString(R.string.str_format_publish,
                        book.getPublish(), book.getPubTime()));
        ((TextView) convertView.findViewById(R.id.textView_campus))
                .setText(book.getLocation());
        ((TextView) convertView.findViewById(R.id.textView_introduction))
                .setText(book.getIntroduction());
        ((TextView) convertView.findViewById(R.id.textView_institution))
                .setText(book.getInstitution());
        ((TextView) convertView.findViewById(R.id.textView_database))
                .setText(book.getBookDatabase());
        return convertView;
    }

}
