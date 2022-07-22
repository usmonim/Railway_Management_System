package edu.ozyegin.cs.entity;

import java.sql.Timestamp;

public class Schedules {
    private int id;
    private int Rid;
    private int Tid;
    private String Stime;

    public Schedules() {
    }

    public Schedules id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Schedules Rid(int id) {
        this.Rid = id;
        return this;
    }

    public int getRid() {
        return Rid;
    }

    public void setRid(int id) {
        this.Rid = id;
    }

    public Schedules Tid(int id) {
        this.Tid = id;
        return this;
    }

    public int getTid() {
        return Tid;
    }

    public void setTid(int id) {
        this.Tid = id;
    }

    public Schedules time(String Stime) {
        this.Stime = Stime;
        return this;
    }

    public String getTime() {
        return Stime;
    }

    public void setTime(String Stime) {
        this.Stime = Stime;
    }


}
