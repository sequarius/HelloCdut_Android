package com.emptypointer.hellocdut.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.AAONewsItem;

public class AAONewsDao {
    private EPDataBaseHelper helper;
    private Context mContext;

    public AAONewsDao(Context context) {
        helper = EPDataBaseHelper.getInstance(context);
        mContext = context;
    }

    public List<AAONewsItem> getAll(List<AAONewsItem> items) {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM aao_news limit 0,16",
                null);
        while (cursor.moveToNext()) {
            String tittle = cursor.getString(cursor.getColumnIndex("title"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            String postDate = cursor.getString(cursor
                    .getColumnIndex("post_time"));
            boolean isRead = cursor
                    .getInt(cursor.getColumnIndex("read_status")) == 0 ? false
                    : true;
            AAONewsItem item = new AAONewsItem(tittle, url, postDate, isRead);
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        database.close();
        return items;
    }

    public void delete() {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL("DELETE FROM aao_news;");
        database.close();
    }

    public long getTime() {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database
                .rawQuery(
                        "select post_time from aao_news ORDER BY get_time limit 0,1",
                        null);
        long time = Long.MIN_VALUE;
        if (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("post_time"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                time = dateFormat.parse(date).getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        database.close();
        return time;
    }

    public String getFistTitle() {
        SQLiteDatabase database = helper.getReadableDatabase();
        if (database.isOpen()) {
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM aao_news limit 0,1", null);
            if (database.isOpen() && cursor.moveToNext()) {
                return cursor.getString(cursor.getColumnIndex("title"));
            }
        }
        database.close();
        return "";
    }

    public void add(String tittle, String url, String postDate, boolean isRead) {
        SQLiteDatabase database = helper.getWritableDatabase();
        Integer tempTran = isRead ? 1 : 0;
        long time = Calendar.getInstance().getTimeInMillis();
        database.execSQL(
                "INSERT INTO aao_news (title,url,post_time,read_status,get_time) VALUES(?,?,?,?,?);",
                new Object[]{tittle, url, postDate, tempTran, time});
        database.close();
    }
}
