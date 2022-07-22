package edu.ozyegin.cs.entity;

import com.google.common.base.Objects;

public class RoutesStops {
    private int rid;
    private int sid;

    public RoutesStops() {
    }

    public RoutesStops rid(int id) {
        this.rid = id;
        return this;
    }

    public int getRId() {
        return rid;
    }

    public void setRId(int id) {
        this.rid = id;
    }

    public RoutesStops sid(int id) {
        this.sid = id;
        return this;
    }

    public int getSId() {
        return sid;
    }

    public void setSId(int id) {
        this.sid = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoutesStops routesstops = (RoutesStops) o;
        return getRId() == routesstops.getRId();
//                Objects.equal(getName(), routesstops.getName()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRId());
    }
}
