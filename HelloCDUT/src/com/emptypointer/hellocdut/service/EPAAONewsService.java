package com.emptypointer.hellocdut.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.emptypointer.hellocdut.dao.AAONewsDao;
import com.emptypointer.hellocdut.domain.AAONewsItem;

public class EPAAONewsService {
    protected static final String CACHE_AAOTIME = "aao_time_cache";
    private EPTimeService mTimeService;
    private List<AAONewsItem> mNewsList;
    private AAONewsDao aaoNewsDao;
    private static final String DEFAULT_NEWS_HOST = "http://aao.cdut.edu.cn/aao/aao.php?sort=389&sorid=391&from=more";

    public EPAAONewsService(Context context) {
        aaoNewsDao = new AAONewsDao(context);
        mNewsList = new ArrayList<AAONewsItem>();
        aaoNewsDao.getAll(mNewsList);
        mTimeService = new EPTimeService(context);
    }

    /**
     * 获取新闻类 如果有新的条目返回true，没有则返回false
     *
     * @return
     */
    public boolean getNews() {
        try {
            String str = EPHttpService.customerGetString(DEFAULT_NEWS_HOST,
                    null);
            String[] strs = str.split("\\r\\n");
            aaoNewsDao.delete();
            for (String string : strs) {

                if (string.contains("images/1.gif")) {
                    AAONewsItem item = new AAONewsItem();
                    String regexURL = "aao.+passg";
                    Pattern patternURL = Pattern.compile(regexURL);
                    Matcher matcherURL = patternURL.matcher(string);
                    if (matcherURL.find()) {
                        item.setNewsUrl("http://www.aao.cdut.edu.cn/"
                                + matcherURL.group());
                    }
                    String regexTitle = "k\">.+<span";
                    Pattern patterTitle = Pattern.compile(regexTitle);
                    Matcher matcherTitle = patterTitle.matcher(string);
                    if (matcherTitle.find()) {
                        String temp = matcherTitle.group();
                        temp = temp.replace("k\">", "");
                        temp = temp.replace("<span", "");
                        item.setNewsTittle(temp);
                    }
                    String regxDate = "\\d{4}-\\d{2}-\\d{2}";
                    Pattern patterDate = Pattern.compile(regxDate);
                    Matcher matcherDate = patterDate.matcher(string);
                    if (matcherDate.find()) {
                        item.setNewsPostDate(matcherDate.group());
                    }

                    aaoNewsDao.add(item.getNewsTittle(), item.getNewsUrl(),
                            item.getNewsPostDate(), false);
                }
            }
            mNewsList = aaoNewsDao.getAll(mNewsList);
            mTimeService.setLastRefreshTime(CACHE_AAOTIME);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取制定页的新闻
     *
     * @param page
     * @return
     */
    public List<AAONewsItem> getNewsByPage(int page) {
        List<AAONewsItem> items = new ArrayList<AAONewsItem>();
        StringBuilder sb = new StringBuilder(DEFAULT_NEWS_HOST);
        sb.append("&offset=").append(page * 16);

        try {
            String str = EPHttpService.customerGetString(sb.toString(), null);
            String[] strs = str.split("\\r\\n");
            for (String string : strs) {

                if (string.contains("images/1.gif")) {
                    AAONewsItem item = new AAONewsItem();
                    String regexURL = "aao.+passg";
                    Pattern patternURL = Pattern.compile(regexURL);
                    Matcher matcherURL = patternURL.matcher(string);
                    if (matcherURL.find()) {
                        item.setNewsUrl("http://www.aao.cdut.edu.cn/"
                                + matcherURL.group());
                    }
                    String regexTitle = "k\">.+<span";
                    Pattern patterTitle = Pattern.compile(regexTitle);
                    Matcher matcherTitle = patterTitle.matcher(string);
                    if (matcherTitle.find()) {
                        String temp = matcherTitle.group();
                        temp = temp.replace("k\">", "");
                        temp = temp.replace("<span", "");
                        item.setNewsTittle(temp);
                    }
                    String regxDate = "\\d{4}-\\d{2}-\\d{2}";
                    Pattern patterDate = Pattern.compile(regxDate);
                    Matcher matcherDate = patterDate.matcher(string);
                    if (matcherDate.find()) {
                        item.setNewsPostDate(matcherDate.group());
                        items.add(item);
                    }

                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return items;
    }

    public int getUnReadCount() {
        int count = 0;
        for (AAONewsItem aaoNewsItem : mNewsList) {
            if (!aaoNewsItem.isReaded()) {

            }
        }
        return count;
    }

    public List<AAONewsItem> getmNewsList() {
        return mNewsList;
    }

}
