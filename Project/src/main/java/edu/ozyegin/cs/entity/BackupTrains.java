package edu.ozyegin.cs.entity;

public class BackupTrains {
    private int sid;
    private int tid;

    public BackupTrains() {
    }

    public BackupTrains sid(int id) {
        this.sid = id;
        return this;
    }

    public int getSId() {
        return sid;
    }

    public void setSId(int id) {
        this.sid = id;
    }

    public BackupTrains tid(int id) {
        this.tid = id;
        return this;
    }

    public int getTId() {
        return tid;
    }

    public void setTId(int id) {
        this.tid = id;
    }
}
