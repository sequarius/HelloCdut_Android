package com.emptypointer.hellocdut.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.dao.NetQueueDao;
import com.emptypointer.hellocdut.domain.NetTask;

public class EPHttpQueueService {
    public static final String TAG = "HttpQueueService";
    private static EPHttpQueueService instance;
    private static Context mContext;
    private boolean isExcuting = false;

    private EPHttpQueueService() {

    }

    public static EPHttpQueueService getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new EPHttpQueueService();
        }
        return instance;
    }

    public void startService() {
        isExcuting = true;
        new AsynHttpTask().execute();

    }

    public void saveRequest(NetTask netTask) {
        NetQueueDao dao = new NetQueueDao(mContext);
        dao.pushIntoQuean(netTask);
    }

    public void clearAllTask() {
        NetQueueDao dao = new NetQueueDao(mContext);
        dao.removeAllTask();
    }

    private class AsynHttpTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... void1) {
            NetQueueDao dao = new NetQueueDao(mContext);
            List<NetTask> tasks = dao.getUnFinishList();
            for (NetTask task : tasks) {
                String host = task.getHost();
                String strParam = task.getParams();
                JSONObject object = JSONObject.parseObject(strParam);
                Set<Entry<String, Object>> entrySet = object.entrySet();
                Iterator<Entry<String, Object>> iterator = entrySet.iterator();
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> me = iterator.next();
                    String key = me.getKey();
                    String value = (String) me.getValue();
                    params.add(new BasicNameValuePair(key, value));
                }
                try {
                    String str = EPHttpService.customerPostString(host, params);
                    Log.i(TAG,"result=="+str);
                    JSONObject obj = JSONObject.parseObject(str);
                    if (obj.getBooleanValue("result")) {
                        dao.removetask(task);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            isExcuting = false;
            super.onPostExecute(result);
        }

    }

}
