package com.emptypointer.hellocdut.domain;

/**
 * 教务新闻的JavaBean；
 *
 * @author Sequarius
 */

public class AAONewsItem {
    private String newsTittle;
    private String newsUrl;
    private String newsPostDate;
    private boolean isReaded;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((newsPostDate == null) ? 0 : newsPostDate.hashCode());
        result = prime * result
                + ((newsTittle == null) ? 0 : newsTittle.hashCode());
        result = prime * result + ((newsUrl == null) ? 0 : newsUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AAONewsItem other = (AAONewsItem) obj;
        if (newsPostDate == null) {
            if (other.newsPostDate != null)
                return false;
        } else if (!newsPostDate.equals(other.newsPostDate))
            return false;
        if (newsTittle == null) {
            if (other.newsTittle != null)
                return false;
        } else if (!newsTittle.equals(other.newsTittle))
            return false;
        if (newsUrl == null) {
            if (other.newsUrl != null)
                return false;
        } else if (!newsUrl.equals(other.newsUrl))
            return false;
        return true;
    }

    public AAONewsItem(String newsTittle, String newsUrl, String newsPostDate,
                       boolean isReaded) {
        super();
        this.newsTittle = newsTittle;
        this.newsUrl = newsUrl;
        this.newsPostDate = newsPostDate;
        this.isReaded = isReaded;
    }

    public AAONewsItem() {

    }

    public String getNewsTittle() {
        return newsTittle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsTittle(String newsTittle) {
        this.newsTittle = newsTittle;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public void setNewsPostDate(String newsPostDate) {
        this.newsPostDate = newsPostDate;
    }

    public void setReaded(boolean isReaded) {
        this.isReaded = isReaded;
    }

    public String getNewsPostDate() {
        return newsPostDate;
    }

    public boolean isReaded() {
        return isReaded;
    }


}
