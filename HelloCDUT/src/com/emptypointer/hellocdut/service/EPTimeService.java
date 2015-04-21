package com.emptypointer.hellocdut.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;

import android.content.Context;

public class EPTimeService {
    private Context mContext;
    private DataCacheDao mDao;
    private static final long MINUTES = 60000; // 一分钟
    private static final long HOURS = 3600000;// 一小时
    private static final long DAYS = 86400000;// 一天
    private static final String TIME_CATCHE = "time_cathe_";

    public EPTimeService(Context mContext) {
        super();
        this.mContext = mContext;
        mDao = new DataCacheDao(mContext);
    }

    public void setLastRefreshTime(String key) {
        mDao.saveCache(TIME_CATCHE + key,
                String.valueOf(System.currentTimeMillis()));
    }

    public String getLastRefreshTime(String key) {
        DataCache result = mDao.getCache(TIME_CATCHE + key);
        if (result == null) {
            return "";
        }
        long lastTime = result.getTime();

        long timeDif = System.currentTimeMillis() - lastTime;
        if (timeDif < MINUTES) {
            StringBuffer sb = new StringBuffer();
            sb.append(timeDif / 1000 + 1).append("秒");
            String resultStr = sb.toString();
            return mContext.getString(R.string.str_format_update_time,
                    resultStr);
        } else if (timeDif < HOURS) {
            StringBuffer sb = new StringBuffer();
            sb.append(timeDif / 1000 / 60).append("分钟");
            String resultStr = sb.toString();
            return mContext.getString(R.string.str_format_update_time,
                    resultStr);
        } else if (timeDif < DAYS) {
            StringBuffer sb = new StringBuffer();
            sb.append(timeDif / 1000 / 60 / 60).append("小时");
            String resultStr = sb.toString();
            return mContext.getString(R.string.str_format_update_time,
                    resultStr);
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日");
            return mContext.getString(R.string.str_format_update_date,
                    timeFormat.format(new Date(lastTime)));
        }
    }
}
