package com.emptypointer.hellocdut.domain;

public class ClassRoom {
    private String name;
    private String seatNum;
    private int[] status;

    public String getName() {
        return name;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public int[] getStatus() {
        return status;
    }

    public ClassRoom(String name, String seatNum, int[] status) {
        super();
        this.name = name;
        this.seatNum = seatNum;
        this.status = status;
    }


}
