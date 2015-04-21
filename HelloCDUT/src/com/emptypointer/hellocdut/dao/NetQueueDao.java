package com.emptypointer.hellocdut.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emptypointer.hellocdut.domain.NetTask;

public class NetQueueDao {

    private EPDataBaseHelper dbHelper;

    public NetQueueDao(Context context) {
        dbHelper = EPDataBaseHelper.getInstance(context);
    }

    public void pushIntoQuean(NetTask task) {
        String host = task.getHost();
        String params = task.getParams();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            boolean isInQueen = false;

            Cursor cursor = db.rawQuery(
                    "SELECT params FROM [asyn_task] WHERE host= ?",
                    new String[]{host});
            while (cursor.moveToNext()) {

                String paramsDB = cursor.getString(cursor
                        .getColumnIndex("params"));
                //如果這個任務已經在隊列中了
                if (params.equals(paramsDB)) {
                    isInQueen = true;
                    break;
                }
            }
            if (!isInQueen) {
                addTask(task);
            }
        }

    }

    /**
     * 更新队列
     *
     * @param task
     */
//	private void updateTask(NetTask task) {
//		SQLiteDatabase db = dbHelper.getW  ritableDatabase();
//		if (db.isOpen()) {
//			db.execSQL("UPDATE [asyn_task] SET [params] = ? WHERE [host]=?",
//					new Object[] { task.getParams(), task.getHost() });
//		}
//	}

    /**
     * 删除完成的请求
     *
     * @param id
     */

    public void removetask(NetTask task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("DELETE FROM [asyn_task] where task_id=?",
                    new Object[]{task.getId()});
        }
    }

    /**
     * 删除所有队列的请求
     *
     * @param id
     */

    public void removeAllTask() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("DELETE FROM [asyn_task]");
        }
    }

    /**
     * 添加新的请求
     */
    private void addTask(NetTask task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            long time = Calendar.getInstance().getTimeInMillis();
            db.execSQL(
                    "INSERT INTO [asyn_task] ([host],[params],[inserrt_time]) VALUES(?,?,?)",
                    new Object[]{task.getHost(), task.getParams(), time});
        }
    }

    /**
     * 獲取未完成的網絡請求
     *
     * @return
     */
    public List<NetTask> getUnFinishList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<NetTask> tasks = new ArrayList<NetTask>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT * FROM [asyn_task]", null);
            while (cursor.moveToNext()) {
                long id = cursor.getInt(cursor.getColumnIndex("task_id"));
                String host = cursor.getString(cursor.getColumnIndex("host"));
                String params = cursor.getString(cursor
                        .getColumnIndex("params"));
                String insertTime = cursor.getString(cursor
                        .getColumnIndex("inserrt_time"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                NetTask task = new NetTask(id, host, params, insertTime, status);
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }


}
