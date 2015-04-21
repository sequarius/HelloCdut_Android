package com.emptypointer.hellocdut.domain;

public class NetQueueItem {
    private int id;
    private String action;
    private String value;
    private String permission;
    private int isFinish;

    public NetQueueItem(int id, String action, String value, String permission,
                        int isFinish) {
        super();
        this.action = action;
        this.value = value;
        this.permission = permission;
        this.isFinish = isFinish;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public String getPermission() {
        return permission;
    }

    public int getIsFinish() {
        return isFinish;
    }

}
