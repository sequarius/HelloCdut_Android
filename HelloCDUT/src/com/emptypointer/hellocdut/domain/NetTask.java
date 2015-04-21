package com.emptypointer.hellocdut.domain;

public class NetTask {
    private long id;
    private String host;
    private String params;
    private String insertTime;
    private int status;

    public long getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getParams() {
        return params;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public int getStatus() {
        return status;
    }

    public NetTask(String host, String params) {
        super();
        this.host = host;
        this.params = params;
    }

    public NetTask(long id, String host, String params, String insertTime,
                   int status) {
        super();
        this.id = id;
        this.host = host;
        this.params = params;
        this.insertTime = insertTime;
        this.status = status;
    }

    public NetTask(String host, String params, String insertTime) {
        super();
        this.host = host;
        this.params = params;
        this.insertTime = insertTime;
    }


}
