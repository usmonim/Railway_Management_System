package edu.ozyegin.cs.entity;

import com.google.common.base.Objects;

public class Trains {
    private int id;
    private String name;

    public Trains() {
    }

    public Trains id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Trains name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trains trains = (Trains) o;
        return getName() == trains.getName();
//                Objects.equal(getName(), stops.getName()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}